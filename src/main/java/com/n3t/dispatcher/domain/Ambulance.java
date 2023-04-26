package com.n3t.dispatcher.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.io.Serializable;

@Entity
@Table(name = "ambulance")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ambulance implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", referencedColumnName = "id", updatable = false)
    @Getter
    @Setter
    private AmbulanceProvider provider;

    @Column(name = "provider_id", columnDefinition = "TEXT", nullable = false, updatable = false)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String providerId;

    @Column(name = "car_number", columnDefinition = "TEXT")
    @Getter
    @Setter
    private String carNumber;

    @Column(name = "is_available", columnDefinition = "BOOLEAN DEFAULT true", nullable = false)
    @Getter
    @Setter
    private boolean isAvailable;

    @Column(name = "location", columnDefinition = "geography", nullable = false)
    @Getter
    @Setter
    private Geometry location;

}
