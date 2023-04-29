package com.n3t.dispatcher.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "emergency")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Emergency implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @OneToMany(mappedBy = "emergency", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Set<ETA> etas;

    @Column(name = "ambulance_location", columnDefinition = "geography", nullable = false)
    @Getter
    @Setter
    private Geometry ambulanceLocation;

    @Column(name = "patient_location", columnDefinition = "geography", nullable = false)
    @Getter
    @Setter
    private Geometry patientLocation;

    @Column(name = "hospital_location", columnDefinition = "geography", nullable = false)
    @Getter
    @Setter
    private Geometry hospitalLocation;

    @Column(name = "patient_distance", columnDefinition = "DECIMAL")
    @Getter
    @Setter
    private Double patientDistance;

    @Column(name = "hospital_distance", columnDefinition = "DECIMAL")
    @Getter
    @Setter
    private Double hospitalDistance;

    @Column(name = "status", columnDefinition = "TEXT DEFAULT 'DRAFT'", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Status status;

    public enum Status {
        DRAFT,
        FAILED,
        EMERGENCY_TOO_LONG,
        PICK_UP_PATIENT_SUCCESS,
        TO_HOSPITAL_SUCCESS
    }
}
