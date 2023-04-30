package com.n3t.dispatcher.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * UserTransit
 */
@Entity
@Table
public class UserTransit {
    @Id
    private long id;
    
    @OneToOne
    private User user;

    @OneToOne
    private Ambulance ambulance;
    
    // public static Enum Status {

    // }
}