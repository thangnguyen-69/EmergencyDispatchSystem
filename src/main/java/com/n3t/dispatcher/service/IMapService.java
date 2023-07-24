package com.n3t.dispatcher.service;

import java.util.List;

import org.springframework.data.util.Pair;

import com.n3t.dispatcher.domain.GeoLocation;
import com.n3t.dispatcher.domain.RouteInfoWithAmbulance;

public interface IMapService {
    public Long calculateETAinSeconds(GeoLocation srcLocation, GeoLocation dstLocation);

    public List<Long> calculateETAFromAllAmbulancesToOnePatientinSeconds(List<GeoLocation> ambulancesLocation,
                                                                         GeoLocation patientLocation);

    public RouteInfoWithAmbulance calculateDistanceInMetersETAinSeconds(GeoLocation srcLocation, GeoLocation dstLocation);

    public List<RouteInfoWithAmbulance> calculateDistanceInMetersAndETAinSecondsFromAllAmbulancesToOnePatient(
            List<GeoLocation> ambulancesLocation, GeoLocation patientLocation);
}
