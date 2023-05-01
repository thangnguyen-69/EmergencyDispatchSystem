package com.n3t.dispatcher.repository;

import com.n3t.dispatcher.domain.AmbulanceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("providerRepository")
public interface AmbulanceProviderRepository
        extends
        JpaSpecificationExecutor<AmbulanceProvider>,
        Serializable,
        JpaRepository<AmbulanceProvider, Long> {

}
