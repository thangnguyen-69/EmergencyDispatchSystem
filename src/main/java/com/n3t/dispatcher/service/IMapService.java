package com.n3t.dispatcher.service;

import java.util.List;
import com.n3t.dispatcher.domain.GeoLocation;

public interface IMapService {
    public Long calculateETAinSeconds(GeoLocation srcLocation,GeoLocation dstLocation);
    public List<Long> calculateETAFromAllAmbulancesToOnePatientinSeconds(List<GeoLocation> ambulancesLocation,GeoLocation patientLocation);
}
