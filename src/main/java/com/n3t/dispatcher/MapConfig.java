package com.n3t.dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// import com.google.maps.GeoApiContext;
import com.n3t.dispatcher.service.RoutesClient;

@Configuration
public class MapConfig {
    // @Bean
    // public GeoApiContext getAPIContext(){
    //     return new GeoApiContext.Builder().apiKey("AIzaSyBSBO8X1EDGqmTz7Rz-wbqXrAIx39HWfsk").build();
    // }
    @Value("${application.google-map.apiKey}")
    private String apiKey;
    @Bean
    public RoutesClient getGoogleRoutesClient(){

        return new RoutesClient(apiKey);
    }
}
