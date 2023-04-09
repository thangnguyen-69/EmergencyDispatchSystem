package com.n3t.dispatcher;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ambulance_user")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
}
