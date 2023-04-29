package com.n3t.dispatcher.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.io.Serializable;


@Entity
@Table(name = "ambulance_user")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "user_name", columnDefinition = "TEXT")
    @Getter
    @Setter
    private String userName;

    @Column(name = "address", columnDefinition = "TEXT")
    @Getter
    @Setter
    private String address;

    @Column(name = "phone_number", columnDefinition = "TEXT")
    @Getter
    @Setter
    private String phoneNumber;

    @Column(name = "location", columnDefinition = "geography", nullable = false)
    @Getter
    @Setter
    private Geometry location;
}
