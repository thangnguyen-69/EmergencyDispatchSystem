package com.n3t.dispatcher.service;

import com.n3t.dispatcher.DatahubConnection;
import com.n3t.dispatcher.domain.Ambulance;
import com.n3t.dispatcher.domain.AmbulanceProvider;
import com.n3t.dispatcher.domain.Emergency;
import com.n3t.dispatcher.domain.GeoLocation;
import com.n3t.dispatcher.domain.RouteInfoWithAmbulance;
import com.n3t.dispatcher.domain.User;
import com.n3t.dispatcher.domain.Emergency.Status;
import com.n3t.dispatcher.repository.AmbulanceProviderRepository;
import com.n3t.dispatcher.repository.AmbulanceRepository;
import com.n3t.dispatcher.repository.EmergencyRepository;

import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;
import wisepaas.datahub.java.sdk.EdgeAgent;

import org.locationtech.jts.geom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class AmbulanceService {

    private static final Logger logger = LoggerFactory.getLogger(AmbulanceService.class);

    @Autowired
    private AmbulanceRepository ambulanceRepository;

    @Autowired
    private AmbulanceProviderRepository providerRepository;

    @Autowired
    private GoogleMapService googleMapService;
    @Autowired
    private EmergencyService emergencyService;
    @Autowired
    private EmergencyRepository emergencyRepository;
    @Autowired
    private EdgeAgent edgeAgent;

    public Ambulance registerAmbulance(Long providerId, String carNumber, double latitude, double longitude) {
        AmbulanceProvider provider = this.providerRepository.findById(providerId).orElseThrow(
                () -> new NoSuchElementException("Ambulance provider with id " + providerId + " not found"));
        Ambulance ambulance = new Ambulance();
        ambulance.setProvider(provider);
        ambulance.setCarNumber(carNumber);
        ambulance.setAvailable(true);
        Geometry newLocation = GeoLocation.fromLatLngToGeometryPoint(latitude, longitude);
        ambulance.setLocation(newLocation);
        ambulance = this.ambulanceRepository.save(ambulance);
        // for now we make the datahub create a new device + tags
        DatahubConnection.registerAmbulance(edgeAgent, ambulance.getId());
        return ambulance;
    }

    public void unregisterAmbulance(Long ambulanceId) {
        Ambulance ambulance = ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new NoSuchElementException("Ambulance with id " + ambulanceId + " not found"));
        if (!ambulance.isAvailable()) {
            throw new NoSuchElementException(
                    "Cannot unregister ambulance " + ambulanceId + " because it is currently dispatched");
        }
        ambulanceRepository.delete(ambulance);
    }

    public AmbulanceProvider registerProvider(String name, String email, String phone) {
        AmbulanceProvider provider = new AmbulanceProvider();
        provider.setProviderName(name);
        provider.setEmail(email);
        provider.setPhone(phone);
        return this.providerRepository.save(provider);
    }

    @Transactional
    public Optional<Ambulance> dispatchAmbulanceToUser(User user, GeoLocation patientLocation) throws Exception {
        Optional<Emergency> existingEmergencyForTheUser = emergencyService.findEmergencyOfCurrentUser(user.getId());
        if (existingEmergencyForTheUser.isPresent()) {
            throw new Exception("an emergency is already issued and an ambulance is already dispatched for the user!");
        }
        List<Ambulance> ambulances = this.ambulanceRepository.findNearestAvailableAmbulances(patientLocation.toPoint(),
                10);
        Stream<GeoLocation> ambLocs = ambulances.stream().map((ambulance) -> {
            return GeoLocation.fromGeometry(ambulance.getLocation());
        });
        List<Pair<RouteInfoWithAmbulance, Integer>> distanceAndETAsWithOriginIndex = googleMapService
                .calculateDistanceInMetersAndETAinSecondsFromAllAmbulancesToOnePatient(ambLocs, patientLocation);
        Stream<RouteInfoWithAmbulance> routeMetricWithAmbulanceInfo = distanceAndETAsWithOriginIndex.stream()
                .map((obj) -> {
                    obj.getFirst().setAmbulance(ambulances.get(obj.getSecond()));
                    return obj.getFirst();
                }).filter((routeInfo) -> (routeInfo.getEta() != 0)).sorted();


        // problem is how to know if the ambulance is still available after we call the
        // API?
        // lock the database, at that one
        // to check if the ambulance is available
        // if yes then dispatch it
        // unlock
        for (RouteInfoWithAmbulance routeInfoWithAmbulance : routeMetricWithAmbulanceInfo.toList()) {
            try {
                Ambulance chosenAmb = reserveAmbulance(routeInfoWithAmbulance.getAmbulance().getId());
                // create a transit between user and ambulance
                // we dispatch the Ambulance then.
                Emergency emergency = Emergency.builder().ambulance(chosenAmb).user(user)
                        .hospitalLocation(GeoLocation.fromLatLngToGeometryPoint(10, 104)) // hardcode for the moment
                        .patientLocation(patientLocation.toPoint()).status(Status.ENROUTE_TO_PATIENT)
                        .distanceFromAmbulanceToPatient(routeInfoWithAmbulance.getDistance())
                        .etaToTarget(routeInfoWithAmbulance.getEta()).build();
                // start the eta counting job
                // or do multiple eta?
                // job will try to find all enroute emergency and check eta
                emergencyRepository.save(emergency);
                return Optional.of(chosenAmb);
            } catch (PessimisticLockException e) {
                logger.info("seems like sb booked that ambulance, switch to the next closest available amb");
            }
        }
        return Optional.empty();
    }

    @Transactional
    private Ambulance reserveAmbulance(long ambulanceId) {
        Ambulance amb = ambulanceRepository.getAndLockAmbulance(ambulanceId);
        amb.setAvailable(false);
        // create a user transit object!
        amb = ambulanceRepository.save(amb);

        return amb;
    }

    public List<Ambulance> getNearestAvailableAmbulances(GeoLocation geoLocation, Integer numOfSuggestions) {
        return this.ambulanceRepository.findNearestAvailableAmbulances(geoLocation.toPoint(), numOfSuggestions);
    }

    public List<Ambulance> getNearestAvailableAmbulances(Double currentLatitude, Double currentLongitude,
                                                         Integer numOfSuggestions) {
        Geometry currentLocation = GeoLocation.fromLatLngToGeometryPoint(currentLatitude, currentLongitude);
        return this.ambulanceRepository.findNearestAvailableAmbulances(currentLocation, numOfSuggestions);
    }

    public List<Ambulance> getAmbulancesByProvider(Long providerId) {
        AmbulanceProvider provider = this.providerRepository.findById(providerId).orElseThrow(
                () -> new NoSuchElementException("Ambulance provider with id " + providerId + " not found"));
        return this.ambulanceRepository.findByProvider(provider);
    }

    public void updateAmbulanceLocation(long ambulanceId, double latitude, double longitude) {
        Ambulance ambulance = this.ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new NoSuchElementException("Ambulance with id " + ambulanceId + " not found"));
        Geometry newLocation = GeoLocation.fromLatLngToGeometryPoint(latitude, longitude);
        ambulance.setLocation(newLocation);
        this.ambulanceRepository.save(ambulance);
    }

    public void updateAmbulanceStatus(Long id, boolean isAvailable) {
        Ambulance ambulance = this.ambulanceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ambulance with id " + id + " not found"));
        ambulance.setAvailable(isAvailable);
        this.ambulanceRepository.save(ambulance);
    }

    public void updateAmbulanceProvider(Long ambulanceId, Long providerId) {
        Ambulance ambulance = this.ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new NoSuchElementException("Ambulance with id " + ambulanceId + " not found"));
        AmbulanceProvider provider = this.providerRepository.findById(providerId).orElseThrow(
                () -> new NoSuchElementException("Ambulance provider with id " + providerId + " not found"));
        ambulance.setProvider(provider);
        this.ambulanceRepository.save(ambulance);
    }

}
