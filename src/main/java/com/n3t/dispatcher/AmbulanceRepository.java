package com.n3t.dispatcher;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("ambulance")
public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {

    @Query(value="SELECT * FROM public.ambulance WHERE is_available = true ORDER BY ST_Distance(location, :point) LIMIT :numOfSuggestion", nativeQuery = true)
    public List<Ambulance> findNearestAvailableAmbulances(Geometry point,int numOfSuggestion);

    // @Transactional
    @Modifying
    @Query(value = "insert into public.ambulance(id, location) values (:id, ST_SetSRID(ST_Point( :lat, :lon ), 4326)\\:\\:geography)", nativeQuery = true)
    void updateStatus(final Long id, final Double lat, final Double lon);

}
