package com.n3t.dispatcher.repository;

import com.n3t.dispatcher.domain.AmbulanceProvider;
import com.n3t.dispatcher.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("userRepository")
public interface UserRepository
        extends
        JpaSpecificationExecutor<User>,
        Serializable,
        JpaRepository<User, Long> {
}
