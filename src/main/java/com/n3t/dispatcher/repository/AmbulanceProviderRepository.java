package com.n3t.dispatcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("providerRepository")
public interface AmbulanceProviderRepository 
        extends 
        PagingAndSortingRepository<AmbulanceProvider, Long>,
        JpaSpecificationExecutor<AmbulanceProvider>,
        Serializable {

}
