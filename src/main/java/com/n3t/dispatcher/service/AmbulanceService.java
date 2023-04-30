package com.n3t.dispatcher.service;

import com.n3t.dispatcher.domain.Ambulance;
import com.n3t.dispatcher.domain.AmbulanceProvider;
import com.n3t.dispatcher.domain.GeoLocation;
import com.n3t.dispatcher.domain.RouteInfoWithAmbulance;
import com.n3t.dispatcher.domain.User;
import com.n3t.dispatcher.repository.AmbulanceProviderRepository;
import com.n3t.dispatcher.repository.AmbulanceRepository;

import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;

import org.locationtech.jts.geom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AmbulanceService {

    private static final Logger logger = LoggerFactory.getLogger(AmbulanceService.class);

    @Autowired
    private AmbulanceRepository ambulanceRepository;

    @Autowired
    private AmbulanceProviderRepository providerRepository;

    @Autowired
    private GoogleMapService googleMapService;

    public Ambulance registerAmbulance(Long providerId, String carNumber, Double latitude, Double longitude) {
        AmbulanceProvider provider = this.providerRepository.findById(providerId).orElseThrow(
                () -> new NoSuchElementException("Ambulance provider with id " + providerId + " not found"));
        Ambulance ambulance = new Ambulance();
        ambulance.setProvider(provider);
        ambulance.setCarNumber(carNumber);
        ambulance.setAvailable(true);
        Geometry newLocation = convertToPoint(latitude, longitude);
        ambulance.setLocation(newLocation);
        return this.ambulanceRepository.save(ambulance);
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
    public Optional<Ambulance> dispatchAmbulanceToUser(User user) {
        Geometry location = user.getCurrentLocation()
                .orElseThrow(() -> new IllegalArgumentException("why user doesnt have location?"));
        List<Ambulance> ambulances = this.ambulanceRepository.findNearestAvailableAmbulances(location, 10);
        List<GeoLocation> ambLocs = ambulances.stream().map((ambulance) -> {
            return GeoLocation.fromGeometry(ambulance.getLocation());
        }).toList();
        List<RouteInfoWithAmbulance> distanceAndETAs = googleMapService
                .calculateDistanceInMetersAndETAinSecondsFromAllAmbulancesToOnePatient(ambLocs,
                        GeoLocation.fromGeometry(location));
        for (int i = 0; i < distanceAndETAs.size(); i++) {
            distanceAndETAs.get(i).setAmbulance(ambulances.get(i));
        }

        Collections.sort(distanceAndETAs);
        // problem is how to know if the ambulance is still available after we call the
        // API?

        // lock the database, at that one
        // to check if the ambulance is available
        // if yes then dispatch it
        // unlock
        for (RouteInfoWithAmbulance routeInfoWithAmbulance : distanceAndETAs) {
            try {
                Ambulance chosenAmb = reserveAmbulance(routeInfoWithAmbulance.getAmbulance().getId());
                return Optional.of(chosenAmb);
                // create a transit between user and ambulance
                // we dispatch the Ambulance then.
            } catch (PessimisticLockException e) {
                logger.info("seems like sb booked that ambulance, switch to the next closest available amb");
            }
        }
        return Optional.empty();
    }

    @Transactional
    private Ambulance reserveAmbulance(long ambulanceId) {
        // Ambulance amb = entityManager.find(Ambulance.class, ambulanceId,
        // LockModeType.PESSIMISTIC_WRITE);
        Ambulance amb = ambulanceRepository.getAndLockAmbulance(ambulanceId);
        amb.setAvailable(false);
        // create a user transit object!
        amb = ambulanceRepository.save(amb);
        // entityManager.persist(amb);
        // entityManager.flush();
        return amb;
    }

    public List<Ambulance> getNearestAvailableAmbulances(GeoLocation geoLocation, Integer numOfSuggestions) {
        return this.ambulanceRepository.findNearestAvailableAmbulances(geoLocation.toPoint(), numOfSuggestions);
    }

    public List<Ambulance> getNearestAvailableAmbulances(Double currentLatitude, Double currentLongitude,
            Integer numOfSuggestions) {
        Geometry currentLocation = convertToPoint(currentLatitude, currentLongitude);
        return this.ambulanceRepository.findNearestAvailableAmbulances(currentLocation, numOfSuggestions);
    }

    public List<Ambulance> getAmbulancesByProvider(Long providerId) {
        AmbulanceProvider provider = this.providerRepository.findById(providerId).orElseThrow(
                () -> new NoSuchElementException("Ambulance provider with id " + providerId + " not found"));
        return this.ambulanceRepository.findByProvider(provider);
    }

    public void updateAmbulanceLupdateAmbulanceLocationocation(Long ambulanceId, Double latitude, Double longitude) {
        Ambulance ambulance = this.ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new NoSuchElementException("Ambulance with id " + ambulanceId + " not found"));
        Geometry newLocation = convertToPoint(latitude, longitude);
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

    private Point convertToPoint(Double latitude, Double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(latitude, longitude);
        return geometryFactory.createPoint(coordinate);
    }

}
