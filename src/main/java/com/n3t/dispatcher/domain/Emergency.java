package com.n3t.dispatcher.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.io.Serializable;

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

    @Getter
    @Setter
    private long etaToTarget;

    @OneToOne
    // @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    private Ambulance ambulance;

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
    private int distanceFromAmbulanceToPatient;


    @Column(name = "hospital_distance", columnDefinition = "DECIMAL")
    @Getter
    @Setter
    private int distanceFromAmbulanceToHospital;

    @Column(name = "status", columnDefinition = "TEXT DEFAULT 'DRAFT'", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Status status;

    public enum Status {
        ENROUTE_TO_PATIENT,
        FAILED,
        EMERGENCY_TOO_LONG,
        PICK_UP_PATIENT_SUCCESS,
        TO_HOSPITAL_SUCCESS
    }
}
