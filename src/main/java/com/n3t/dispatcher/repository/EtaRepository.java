package com.n3t.dispatcher.repository;

import com.n3t.dispatcher.domain.ETA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface EtaRepository
        extends
        PagingAndSortingRepository<ETA, Long>,
        JpaSpecificationExecutor<ETA>,
        Serializable,
        JpaRepository<ETA, Long> {

    Optional<ETA> findByEmergencyId(Long emergencyId);

}

