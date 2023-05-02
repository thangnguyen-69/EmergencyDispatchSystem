package com.n3t.dispatcher.repository;

import com.n3t.dispatcher.domain.ETA;
import com.n3t.dispatcher.domain.Emergency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface EmergencyRepository
        extends
        JpaSpecificationExecutor<ETA>,
        Serializable,
        JpaRepository<Emergency, Long> {

    // Optional<ETA> findByEmergencyId(Long emergencyId);

}

