package com.n3t.demo;

import com.n3t.dispatcher.DemoApplication;
import com.n3t.dispatcher.domain.Ambulance;
import com.n3t.dispatcher.domain.AmbulanceProvider;
import com.n3t.dispatcher.service.AmbulanceService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = DemoApplication.class)
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.DOCKER)
@ActiveProfiles("test")
class DispatcherApplicationTests {

    @Autowired
    private AmbulanceService ambulanceService;

    @Test
    @Transactional
    void shouldBeAbleToGetUpdatedStatusOfAmbulance() {
        AmbulanceProvider provider = this.ambulanceService.registerProvider("toan thang", "thangvip5432@gmail.com",
                "0231230123");
        Ambulance amb1 = this.ambulanceService.registerAmbulance(provider.getId(), "29C-12345", 10.404864, 107.114446);
        Ambulance amb2 = this.ambulanceService.registerAmbulance(provider.getId(), "29C-12346", 10.405163, 107.115757);
        Ambulance amb3 = this.ambulanceService.registerAmbulance(provider.getId(), "29C-12347", 10.407064, 107.113640);
        Ambulance amb4 = this.ambulanceService.registerAmbulance(provider.getId(), "29C-12348", 10.04, 127.012);
        Ambulance amb5 = this.ambulanceService.registerAmbulance(provider.getId(), "29C-1234w8", 40, 40);
        Ambulance amb6 = this.ambulanceService.registerAmbulance(provider.getId(), "29C-123s48", 50, 50);

        // this.ambulanceService.updateAmbulanceLocation(amb1.getId(),
        // this.ambulanceService.updateAmbulanceLocation(amb2.getId(),
        // this.ambulanceService.updateAmbulanceLocation(amb3.getId(),
        // this.ambulanceService.updateAmbulanceLocation(amb4.getId(),
        List<Ambulance> listAmb = this.ambulanceService.getNearestAvailableAmbulances(10.404768, 107.114562, 3);
        // this will actually failed, because session will be closed after the line
        // above, so we need to add @Transactional to keep the sesion open throughout
        // the fucking function.
        System.out.println("provider:     " + listAmb.get(0).getProvider());
        System.out.println("amb:     " + listAmb.get(0).getLocation().getSRID()+listAmb.get(0).getLocation().getCoordinate());
    }

    // @Autowired
    // private GoogleMapService googleMapService;

    // @Test
    // void shouldBeAbleToCalculateETA() {
    //     List<Long> haha = this.googleMapService.calculateETAFromAllAmbulancesToOnePatientinSeconds(
    //             Arrays.asList(new GeoLocation(10.404768, 107.114562),new GeoLocation(10.405163, 107.115757)), new GeoLocation(10.407064, 107.113640));
    //     Long a = this.googleMapService.calculateETAinSeconds(new GeoLocation(10.407064, 107.113640),
    //             new GeoLocation(10.404768, 107.114562));
    //     System.out.println("time is " + a);
    //     System.out.println("multiple time is " + haha);
    // }
    // @Autowired 
    // private UserRepository userRepository;
    // @Test
    // void userCanBookAnAmbulance(){
    //     User a = User.builder().userName("haha").build();
    //     a = userRepository.save(a);
    //     // sanity test, should not have any problem
    //     this.ambulanceService.dispatchAmbulanceToUser(a, new GeoLocation(10, 105));
    // }

    @Test
    void shouldBeAbleToAddStations() {

    }

    @Test
    void shouldfindNearest6AvailableStationsGivenUserLocation() {
    }

    @Test
    void shouldShowInfoOfDispatcherToCaller() {
    }

    @Test
    void shouldShowInfoOfCallerToDispatcher() {
    }

    @Test
    void shouldShowAvailableHospitalsToAmbulanceCrew() {
    }

}

// user press on our app
// the app will publish emergency signal to our emergency platform,including
// lat,lon.
// Dispatching algorithm find around 15+ nearest station which is subscribed.
// Then calculate ETA for each station, then choose the nearest station with the
// shortest ETA.
// the ambulance will press confirm, set out to the patient location, and
// navigate with grasshopper api,(have api for emergency, allow to go on the
// opposite direction of traffic),while updating to user the
// location of ambulance, and the ETA

// In the meantime, the user(if wanted) can converse with a dispatcher
// responsible for that station, or if the station receive a request, then that
// dispatcher will call the user(we dont need in app call right). 3rd party,
// 115,... Choose 1 station to connect the call to, will have corresponding
// number for the dispatcher: 115, or 3rd party.
// The dispatcher will ask for more information about the emergency, and update
// the info for the station to prepare any extra device which they may need,
// like bloodbank.
// paramedic arrive at the location, assess the situation, and decide to take
// the patient to hospitals. System will show near + not full hospital, maybe
// with speciality if the paramedic insist.
// crew will choose a hospital, call the hospital for sitrep ( or fill in status
// form to send, idk), and navigate to hospital for help.
// the hospital will receive the patient, and update the status to the system,
// and the user.
