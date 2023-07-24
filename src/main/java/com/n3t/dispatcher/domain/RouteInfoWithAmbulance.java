package com.n3t.dispatcher.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class RouteInfoWithAmbulance implements Comparable<RouteInfoWithAmbulance> {
    private Ambulance ambulance;
    private long eta;
    private int distance;

    @Override
    public int compareTo(RouteInfoWithAmbulance arg0) {
        if (this.eta < arg0.eta) {
            return -1;
        } else if (this.eta == arg0.eta) {
            return 0;
        }
        return 1;
    }
}
