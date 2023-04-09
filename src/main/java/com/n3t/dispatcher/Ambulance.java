package com.n3t.dispatcher;

import org.locationtech.jts.geom.Geometry;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.dialect.PostgreSQLDialect;
// import org.hibernate.dialect.PgJdb;
@Entity
@Table(name = "ambulance")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ambulance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @OneToOne
    // @JoinColumn(name = "provider_id")
    // // @OneToOne(targetEntity = AmbulanceProvider.class, mappedBy = "provider", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    
    @Column(name = "provider_id")
    private String provider;
    // private AmbulanceProvider provider;


    @Column(name = "car_number")
    private String carNumber;

    @Column(name = "is_available")
    private boolean isAvailable;


    @Column(columnDefinition = "geography" ,name="location")
    private Geometry location;

    

}
