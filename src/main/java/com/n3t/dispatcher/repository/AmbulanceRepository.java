package com.n3t.dispatcher.repository;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AmbulanceRepository 
        extends 
        PagingAndSortingRepository<Ambulance, Long>,
        JpaSpecificationExecutor<Ambulance>,
        Serializable {

    @Query("SELECT * FROM ambulance a WHERE a.isAvailable = true ORDER BY ST_Distance(a.location, :point) LIMIT :numOfSuggestion")
    public List<Ambulance> findNearestAvailableAmbulances(@Param("point") Geometry point, @Param("numOfSuggestion") int numOfSuggestion);

    @Modifying
    @Query("INSERT INTO ambulance (id, location) VALUES (:id, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography)", nativeQuery = true)
    void updateAmbulanceLocation(@Param("id") Long ambulanceId, @Param("latitude") Double latitude, @Param("longitude") Double longitude);

    List<Ambulance> findByProvider(AmbulanceProvider provider);
    
}

