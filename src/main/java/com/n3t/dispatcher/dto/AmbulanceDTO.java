package com.n3t.dispatcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.n3t.dispatcher.domain.Ambulance;
import com.n3t.dispatcher.domain.GeoLocation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic = true)
public class AmbulanceDTO {
    @JsonProperty("userId")
    private long ambulanceId;

    @JsonProperty("driverName")
    private String driverName;

    @JsonProperty("licensePlate")
    private String licensePlate;

    @JsonProperty("latitude")
    private double lat;
    @JsonProperty("longitude")
    private double lng;

    public static AmbulanceDTO fromAmbulance(Ambulance ambulance) {
        GeoLocation geoLoc = GeoLocation.fromGeometry(ambulance.getLocation());
        return AmbulanceDTO.builder().ambulanceId(ambulance.getId()).driverName("thang")
                .licensePlate(ambulance.getCarNumber()).lat(geoLoc.latitude).lng(geoLoc.longitude).build();
    }
}
