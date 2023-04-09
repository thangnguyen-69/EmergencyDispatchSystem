package com.n3t.dispatcher;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("provider")
public interface AmbulanceProviderRepository extends JpaRepository<AmbulanceProvider, Long> {
}
