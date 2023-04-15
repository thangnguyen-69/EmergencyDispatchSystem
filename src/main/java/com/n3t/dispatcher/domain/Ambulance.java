package com.n3t.dispatcher.domain;

import org.locationtech.jts.geom.Geometry;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.dialect.PostgreSQLDialect;

@Entity
@Table(name = "ambulance")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ambulance {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    // @OneToOne
    // @JoinColumn(name = "provider_id")
    // // @OneToOne(targetEntity = AmbulanceProvider.class, mappedBy = "provider", fetch = FetchType.EAGER, cascade = CascadeType.ALL)

    @Column(name = "provider_id", columnDefinition = "TEXT", nullable = false, updatable = false)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String providerId;

    @Column(name = "car_number", columnDefinition = "TEXT")
    @Getter
    @Setter
    private String carNumber;

    @Column(name = "available", columnDefinition = "BOOLEAN DEFAULT true", nullable = false)
    @Getter
    @Setter
    private boolean available;

    @Column(name="location", columnDefinition = "geography", nullable = false)
    @Getter
    @Setter
    private Geometry location;

    

}
