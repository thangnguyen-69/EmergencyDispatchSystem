package com.n3t.dispatcher.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.n3t.dispatcher.domain.ETA;
import com.n3t.dispatcher.repository.EtaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.google.maps.routing.v2.RouteMatrixElement;
import com.google.protobuf.Duration;
import com.n3t.dispatcher.domain.GeoLocation;
import com.n3t.dispatcher.domain.RouteInfoWithAmbulance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Getter
@Setter
public class GoogleMapService implements IMapService {

    private RoutesClient client;

    @Autowired
    private EtaRepository etaRepository;

    @Override
    public Long calculateETAinSeconds(GeoLocation srcLocation, GeoLocation dstLocation) {
        return calculateETAFromAllAmbulancesToOnePatientinSeconds(Arrays.asList(srcLocation), dstLocation).get(0);
    }

    @Override
    public List<Long> calculateETAFromAllAmbulancesToOnePatientinSeconds(List<GeoLocation> ambulancesLocation,
            GeoLocation patientLocation) {
        ArrayList<Long> ETAs = new ArrayList<Long>();
        Iterator<RouteMatrixElement> a = client.computeRouteMatrix(ambulancesLocation, patientLocation);
        while(a.hasNext()){
            Duration eta= a.next().getDuration();
            ETAs.add(eta.getSeconds());
        }
        return ETAs;
    }
    @Override
    public RouteInfoWithAmbulance calculateDistanceInMetersETAinSeconds(GeoLocation srcLocation, GeoLocation dstLocation) {
        return calculateDistanceInMetersAndETAinSecondsFromAllAmbulancesToOnePatient(Arrays.asList(srcLocation), dstLocation).get(0);
    }

    // cost API per call, so use wisely
    @Override
    public List<RouteInfoWithAmbulance> calculateDistanceInMetersAndETAinSecondsFromAllAmbulancesToOnePatient(List<GeoLocation> ambulancesLocation,
            GeoLocation patientLocation) {
        ArrayList<RouteInfoWithAmbulance> DistanceAndETAs = new ArrayList<RouteInfoWithAmbulance>();
        Iterator<RouteMatrixElement> a = client.computeRouteMatrix(ambulancesLocation, patientLocation);
        while(a.hasNext()){
            RouteMatrixElement tmp = a.next();
            Long eta= tmp.getDuration().getSeconds();
            int distanceInMeters= tmp.getDistanceMeters();
            DistanceAndETAs.add(RouteInfoWithAmbulance.builder().distance(distanceInMeters).eta(eta).build());
        }
        return DistanceAndETAs;
    }

    @Transactional(readOnly = true)
    public List<ETA> query(Specification<ETA> specification) {
        return this.etaRepository.findAll(specification);
    }

}
