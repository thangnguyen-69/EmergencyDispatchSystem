package com.n3t.dispatcher.service;

import java.util.Optional;

import com.n3t.dispatcher.repository.EmergencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.n3t.dispatcher.domain.Emergency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Service
@AllArgsConstructor
@Getter
@Setter
public class EmergencyService {


    @Autowired
    private EmergencyRepository emergencyRepository;

    public Optional<Emergency> findEmergencyOfCurrentUser(long userId) {
        return emergencyRepository.findEmergencyByUserId(userId);
    }


}
