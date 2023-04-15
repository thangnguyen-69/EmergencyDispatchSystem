package com.n3t.dispatcher.service;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
public class AmbulanceService {

    @Autowired
    private final AmbulanceRepository ambulanceRepository;

    @Autowired
    private final AmbulanceProviderRepository providerRepository;

    public List<Ambulance> getNearestAvailableAmbulances(Double currentLatitude, Double currentLongitude, Integer numOfSuggestions) {
        Geometry currentLocation = convertToPoint(currentLatitude, currentLongitude);
        return this.ambulanceRepository.findNearestAvailableAmbulances(currentLocation, numOfSuggestions);
    }

    public void updateAmbulanceLocation(Long ambulanceId, Double latitude, Double longitude) {
        Ambulance ambulance = this.ambulanceRepository.findById(ambulanceId)
                                .orElseThrow(() -> new AmbulanceNotFoundException("Ambulance with id " + ambulanceId + " not found"));
        Geometry newLocation = convertToPoint(latitude, longitude);
        ambulance.setLocation(newLocation);
        this.ambulanceRepository.save(ambulance);    
    }

    public Ambulance registerAmbulance(Long providerId, String carNumber) {
        AmbulanceProvider provider = providerRepository.findById(providerId).get();
        Ambulance ambulance = new Ambulance();
        // ambulance.setProvider(provider);
        ambulance.setCarNumber(carNumber);
        return this.ambulanceRepository.save(ambulance);
    }
    
    public AmbulanceProvider registerProvider(String name, String email, String phone) {
        AmbulanceProvider provider = new AmbulanceProvider();
        provider.setProviderName(name);
        provider.setEmail(email);
        provider.setPhone(phone);
        return this.providerRepository.save(provider);
    }
    
    public void updateAmbulanceStatus(Long id, boolean available) {
        Ambulance ambulance = this.ambulanceRepository.findById(id).get();
        ambulance.setAvailable(available);
        this.ambulanceRepository.save(ambulance);
    }

    private Point convertToPoint(Double latitude, Double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(latitude, longitude);
        return geometryFactory.createPoint(coordinate);
    }
    
}
