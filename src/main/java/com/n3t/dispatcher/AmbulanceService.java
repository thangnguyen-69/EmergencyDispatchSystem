package com.n3t.dispatcher;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AmbulanceService {
    private final AmbulanceRepository repo;
    private final AmbulanceProviderRepository providerRepo;
    public List<Ambulance> getNearestAvailableAmbulance(Double currentLat,Double currentLon,int numOfSuggestion) {
        // repo to find nearest numOfSuggestion available ambulance;
        return this.repo.findNearestAvailableAmbulances(this.convertToPoint(currentLat, currentLon),numOfSuggestion);
    }

    public void updateAmbulanceLocation(Long id, Double lat, Double lon) {
        Ambulance ambulance = this.repo.findById(id).get();
        ambulance.setLocation(this.convertToPoint(lat, lon));
        this.repo.save(ambulance);    
    }
    public Ambulance registerAmbulance(Long providerId, String carNumber) {
        AmbulanceProvider provider = this.providerRepo.findById(providerId).get();
        Ambulance ambulance = new Ambulance();
        // ambulance.setProvider(provider);
        ambulance.setCarNumber(carNumber);
        ambulance.setAvailable(true);
        Ambulance savedAmbulance = this.repo.save(ambulance);
        return savedAmbulance;
    }
    public AmbulanceProvider registerProvider(String name, String email, String phone) {
        AmbulanceProvider provider = new AmbulanceProvider();
        provider.setProviderName(name);
        provider.setEmail(email);
        provider.setContactNumber(phone);
        AmbulanceProvider savedProvider = this.providerRepo.save(provider);
        return savedProvider;
    }
    public void updateAmbulanceStatus(Long id, boolean isAvailable) {
        Ambulance ambulance = this.repo.findById(id).get();
        ambulance.setAvailable(isAvailable);
        this.repo.save(ambulance);
    }
    
    private Point convertToPoint(Double lat,Double lon) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);
        Coordinate coordinate = new Coordinate(lat, lon);
        Point point = geometryFactory.createPoint(coordinate);

        return point;
    }
}
