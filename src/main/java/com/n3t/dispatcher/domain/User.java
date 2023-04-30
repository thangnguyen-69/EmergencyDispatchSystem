package com.n3t.dispatcher.domain;

import java.io.Serializable;
import java.util.Optional;

import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ambulance_user")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private long id;

    @Column(name = "user_name", columnDefinition = "TEXT")
    @Getter
    @Setter
    @NonNull
    private String userName;

    @Column(name = "location", columnDefinition = "geography", nullable = true)
    @Getter
    @Setter
    private Optional<Geometry> currentLocation;
}
