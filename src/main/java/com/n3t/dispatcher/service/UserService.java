package com.n3t.dispatcher.service;

import java.util.Optional;

import com.n3t.dispatcher.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.n3t.dispatcher.domain.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
