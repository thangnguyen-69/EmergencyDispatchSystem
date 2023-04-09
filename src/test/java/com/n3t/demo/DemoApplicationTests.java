package com.n3t.demo;                                        

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.n3t.dispatcher.Ambulance;
import com.n3t.dispatcher.AmbulanceProvider;
import com.n3t.dispatcher.AmbulanceService;
import com.n3t.dispatcher.DemoApplication;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;


@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = DemoApplication.class
    )
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.DOCKER)
@ActiveProfiles("test")
class DispatcherApplicationTests {

        @Autowired
	private AmbulanceService ambulanceService;
        @Test
        void shouldBeAbleToGetUpdatedStatusOfAmbulance(){
                AmbulanceProvider provider = this.ambulanceService.registerProvider("toan thang", "thangvip5432@gmail.com","0231230123");
                Ambulance amb1 = this.ambulanceService.registerAmbulance(provider.getId(),"29C-12345" );
                Ambulance amb2 = this.ambulanceService.registerAmbulance(provider.getId(),"29C-12346" );
                Ambulance amb3 = this.ambulanceService.registerAmbulance(provider.getId(),"29C-12347" );
                Ambulance amb4 = this.ambulanceService.registerAmbulance(provider.getId(),"29C-12348" );

                this.ambulanceService.updateAmbulanceLocation(amb1.getId(), 10.404864, 107.114446);
                this.ambulanceService.updateAmbulanceLocation(amb2.getId(), 10.405163, 107.115757);
                this.ambulanceService.updateAmbulanceLocation(amb3.getId(), 10.407064, 107.113640);
                this.ambulanceService.updateAmbulanceLocation(amb4.getId(), 10.04, 127.012);
                List<Ambulance> listAmb = this.ambulanceService.getNearestAvailableAmbulance(10.404768, 107.114562,2);
                System.out.println(listAmb);
        }

        @Test
        void shouldBeAbleToAddStations(){}

        @Test
        void shouldfindNearest6AvailableStationsGivenUserLocation(){}

        @Test
        void shouldShowInfoOfDispatcherToCaller(){}

        @Test
        void shouldShowInfoOfCallerToDispatcher(){}

        @Test
        void shouldShowAvailableHospitalsToAmbulanceCrew(){}


}

// user press on our app
// the app will publish emergency signal to our emergency platform,including lat,lon.
        // Dispatching algorithm find around 15+ nearest station which is subscribed. Then calculate ETA for each station, then choose the nearest station with the shortest ETA.
                // the ambulance will press confirm, set out to the patient location, and navigate with grasshopper api,(have api for emergency, allow to go on the opposite direction of traffic),while updating to user the
                // location of ambulance, and the ETA

        // In the meantime, the user(if wanted) can converse with a dispatcher responsible for that station, or if the station receive a request, then that dispatcher will call the user(we dont need in app call right). 3rd party, 115,... Choose 1 station to connect the call to, will have corresponding number for the dispatcher: 115, or 3rd party.
                // The dispatcher will ask for more information about the emergency, and update the info for the station to prepare any extra device which they may need, like bloodbank.
        // paramedic arrive at the location, assess the situation, and decide to take the patient to hospitals.  System will show near + not full hospital, maybe with speciality if the paramedic insist.
                // crew will choose a hospital, call the hospital for sitrep ( or fill in status form to send, idk), and navigate to hospital for help.
                // the hospital will receive the patient, and update the status to the system, and the user.