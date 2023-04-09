package com.n3t.dispatcher;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "provider")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AmbulanceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "provider_name")
    private String providerName;

}
