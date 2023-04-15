package com.n3t.dispatcher.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ambulance_user")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

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
}
