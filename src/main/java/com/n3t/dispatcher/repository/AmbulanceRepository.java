package com.n3t.dispatcher.repository;

import com.n3t.dispatcher.domain.Ambulance;
import com.n3t.dispatcher.domain.AmbulanceProvider;

import jakarta.persistence.LockModeType;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface AmbulanceRepository
        extends JpaSpecificationExecutor<Ambulance>, Serializable, JpaRepository<Ambulance, Long> {

    @Query(value = "SELECT * FROM public.ambulance WHERE is_available = true ORDER BY ST_Distance(location, :point) LIMIT :numOfSuggestions", nativeQuery = true)
    public List<Ambulance> findNearestAvailableAmbulances(@Param("point") Geometry point,
                                                          @Param("numOfSuggestions") int numOfSuggestions);

    @Modifying
    @Query(value = "INSERT INTO ambulance (id, location) VALUES (:id, ST_Point(:latitude, :longitude,4326)::geography)", nativeQuery = true)
    void updateAmbulanceLocation(@Param("id") Long ambulanceId, @Param("latitude") Double latitude,
                                 @Param("longitude") Double longitude);

    List<Ambulance> findByProvider(AmbulanceProvider provider);

    //     https://stackoverflow.com/questions/57923047/spring-jpa-locking-concept
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // ok so these must be native psotgres query, because jpql query doesnt look like this and it will throw cannot parse error
    @Query(value = "SELECT a FROM Ambulance a WHERE a.id = :id", nativeQuery = false)
    public Ambulance getAndLockAmbulance(@Param("id") long ambulanceId);
}
