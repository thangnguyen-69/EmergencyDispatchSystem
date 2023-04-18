package com.n3t.dispatcher.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ambulance_provider")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmbulanceProvider implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Set<Ambulance> ambulances;

    @Column(name = "phone", columnDefinition = "TEXT")
    @Getter
    @Setter
    private String phone;

    @Column(name = "email", columnDefinition = "TEXT")
    @Getter
    @Setter
    private String email;

    @Column(name = "provider_name", columnDefinition = "TEXT")
    @Getter
    @Setter
    private String providerName;

}
