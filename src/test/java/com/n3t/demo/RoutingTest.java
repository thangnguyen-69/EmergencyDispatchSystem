package com.n3t.demo;

import com.n3t.dispatcher.DemoApplication;
import com.n3t.dispatcher.domain.Ambulance;
import com.n3t.dispatcher.domain.AmbulanceProvider;
import com.n3t.dispatcher.domain.GeoLocation;
import com.n3t.dispatcher.domain.User;
import com.n3t.dispatcher.repository.UserRepository;
import com.n3t.dispatcher.service.AmbulanceService;
import com.n3t.dispatcher.service.GoogleMapService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = DemoApplication.class)
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.DOCKER)
@ActiveProfiles("test")
class RoutingTest {
    @Autowired 
    private UserRepository userRepository;
    @BeforeEach
    public void init(){
        AmbulanceProvider provider = this.ambulanceService.registerProvider("toan thang", "thangvip5432@gmail.com","0231230123");
        Ambulance amb1 = this.ambulanceService.registerAmbulance(provider.getId(), "29C-12345", 10.404864, 107.114446);
        Ambulance amb2 = this.ambulanceService.registerAmbulance(provider.getId(), "29C-12346", 10.405163, 107.115757);
        Ambulance amb3 = this.ambulanceService.registerAmbulance(provider.getId(), "29C-12347", 10.407064, 107.113640);
        Ambulance amb4 = this.ambulanceService.registerAmbulance(provider.getId(), "29C-12348", 10.04, 127.012);
    }
    @Autowired
    private GoogleMapService googleMapService;
    @Autowired
    private AmbulanceService ambulanceService;

    @Test
    void shouldBeAbleToCalculateETA() {
        List<Long> haha = this.googleMapService.calculateETAFromAllAmbulancesToOnePatientinSeconds(
                Arrays.asList(new GeoLocation(10.404768, 107.114562),new GeoLocation(10.405163, 107.115757)).stream(), new GeoLocation(10.407064, 107.113640));
        Long a = this.googleMapService.calculateETAinSeconds(new GeoLocation(10.407064, 107.113640),
                new GeoLocation(10.404768, 107.114562));
        System.out.println("time is " + a);
        System.out.println("multiple time is " + haha);
    }


    // i dont really know why i throw here
    @Test
    void userCanBookAnAmbulance() throws Exception{
        User a = User.builder().userName("haha").build();
        a = userRepository.save(a);
        // sanity test, should not have any problem
        this.ambulanceService.dispatchAmbulanceToUser(a, new GeoLocation(10.404768, 107.114562));
    }

}


