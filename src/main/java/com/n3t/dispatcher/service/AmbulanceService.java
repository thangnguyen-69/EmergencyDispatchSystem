package com.n3t.dispatcher.service;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AmbulanceService {

    private static final Logger logger = LoggerFactory.getLogger(AmbulanceService.class);

    @Autowired
    private final AmbulanceRepository ambulanceRepository;

    @Autowired
    private final AmbulanceProviderRepository providerRepository;

    public Ambulance registerAmbulance(Long providerId, String carNumber) {
        AmbulanceProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider with id " + providerId + " not found"));
        Ambulance ambulance = new Ambulance();
        ambulance.setProvider(provider);
        ambulance.setCarNumber(carNumber);
        return this.ambulanceRepository.save(ambulance);
    }
    

    public void unregisterAmbulance(Long ambulanceId) {
        Ambulance ambulance = ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new AmbulanceNotFoundException("Ambulance with id " + ambulanceId + " not found"));
    
        if (!ambulance.isAvailable()) {
            throw new AmbulanceNotAvailableException("Cannot unregister ambulance " + ambulanceId + " because it is currently dispatched");
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

    public List<Ambulance> getNearestAvailableAmbulances(Double currentLatitude, Double currentLongitude, Integer numOfSuggestions) {
        Geometry currentLocation = convertToPoint(currentLatitude, currentLongitude);
        return this.ambulanceRepository.findNearestAvailableAmbulances(currentLocation, numOfSuggestions);
    }

    public List<Ambulance> getAmbulancesByProvider(Long providerId) {
        AmbulanceProvider provider = this.providerRepository.findById(providerId)
                                            .orElseThrow(() -> new AmbulanceProviderNotFoundException("Ambulance provider with id " + providerId + " not found"));
        return this.ambulanceRepository.findByProvider(provider);
    }    

    public void updateAmbulanceLocation(Long ambulanceId, Double latitude, Double longitude) {
        Ambulance ambulance = this.ambulanceRepository.findById(ambulanceId)
                                .orElseThrow(() -> new AmbulanceNotFoundException("Ambulance with id " + ambulanceId + " not found"));
        Geometry newLocation = convertToPoint(latitude, longitude);
        ambulance.setLocation(newLocation);
        this.ambulanceRepository.save(ambulance);    
    }

    public void updateAmbulanceStatus(Long id, boolean isAvailable) {
        Ambulance ambulance = this.ambulanceRepository.findById(id).get();
        ambulance.setIsAvailable(isAvailable);
        this.ambulanceRepository.save(ambulance);
    }

    public void updateAmbulanceProvider(Long ambulanceId, Long providerId) {
        Ambulance ambulance = this.ambulanceRepository.findById(ambulanceId)
                                .orElseThrow(() -> new AmbulanceNotFoundException("Ambulance with id " + ambulanceId + " not found"));
        AmbulanceProvider provider = providerRepository.findById(providerId).get();
        ambulance.setProvider(provider);
        this.ambulanceRepository.save(ambulance);
    }
    
    private Point convertToPoint(Double latitude, Double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(latitude, longitude);
        return geometryFactory.createPoint(coordinate);
    }
    
}
