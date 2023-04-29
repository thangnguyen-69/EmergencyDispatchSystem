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
        List<Long> ETAs = new ArrayList<Long>();
        Iterator<RouteMatrixElement> a = client.computeRouteMatrix(ambulancesLocation, patientLocation);
        while(a.hasNext()){
            Duration eta= a.next().getDuration();
            ETAs.add(eta.getSeconds());
        }
        return ETAs;
    }

    @Transactional(readOnly = true)
    public List<ETA> query(Specification<ETA> specification) {
        return this.etaRepository.findAll(specification);
    }

}
