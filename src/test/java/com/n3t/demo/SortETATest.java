package com.n3t.demo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.n3t.dispatcher.domain.RouteInfoWithAmbulance;

public class SortETATest {

    @Test
    public void testSort() {
        RouteInfoWithAmbulance a = RouteInfoWithAmbulance.builder().distance(30).eta(10).build();
        RouteInfoWithAmbulance b = RouteInfoWithAmbulance.builder().distance(30).eta(30).build();
        RouteInfoWithAmbulance c = RouteInfoWithAmbulance.builder().distance(30).eta(20).build();
        List<RouteInfoWithAmbulance> routeList = Arrays.asList(a, b, c);
        Collections.sort(routeList);

        System.out.println("hmm" + routeList);
    }

    @Test
    public void testForEach() {
        int result = -1;
        Stream<Integer> stream = Arrays.asList(2, 3, 4, 5, 6).stream();
        stream.forEach(routeInfoWithAmbulance -> {
            // check if this is the right element
            System.out.println(routeInfoWithAmbulance);
            if (routeInfoWithAmbulance == 3) {
                return; // end the loop early and return the result
            }
        });
    }
}
