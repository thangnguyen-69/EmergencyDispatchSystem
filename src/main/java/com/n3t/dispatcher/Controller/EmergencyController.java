package com.n3t.dispatcher.Controller;

import java.util.Optional;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import com.n3t.dispatcher.domain.Ambulance;
import com.n3t.dispatcher.domain.GeoLocation;
import com.n3t.dispatcher.domain.User;
import com.n3t.dispatcher.dto.AmbulanceDTO;
import com.n3t.dispatcher.dto.UserLocationDTO;
import com.n3t.dispatcher.service.AmbulanceService;
import com.n3t.dispatcher.service.UserService;

@RestController
@RequestMapping("/emergency")
public class EmergencyController {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyController.class);

    @Autowired
    private AmbulanceService ambulanceService;
    private UserService userService;

    @RequestMapping(value = "call", method = RequestMethod.GET)
    @ResponseBody
    public AmbulanceDTO status(@RequestBody UserLocationDTO requestBody) throws IllegalArgumentException, RestClientException {
        User user = userService.findUserById(requestBody.getUserId()).orElseThrow();
        Optional<Ambulance> chosenAmbulance = ambulanceService.dispatchAmbulanceToUser(user, new GeoLocation(requestBody.getLat(), requestBody.getLng()));

        if (chosenAmbulance.isEmpty()) {
            // https://www.baeldung.com/exception-handling-for-rest-with-spring
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "all our ambulances are busy!");
        } else {
            return AmbulanceDTO.fromAmbulance(chosenAmbulance.get());
        }
    }

}