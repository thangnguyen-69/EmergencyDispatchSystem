package com.n3t.dispatcher.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.n3t.dispatcher.domain.ETA;
import com.n3t.dispatcher.repository.EtaRepository;
import com.n3t.dispatcher.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.google.maps.routing.v2.RouteMatrixElement;
import com.google.protobuf.Duration;
import com.n3t.dispatcher.domain.GeoLocation;
import com.n3t.dispatcher.domain.RouteInfoWithAmbulance;
import com.n3t.dispatcher.domain.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Getter
@Setter
public class UserService {


    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
    }



}
