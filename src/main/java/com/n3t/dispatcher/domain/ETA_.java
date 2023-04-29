package com.n3t.dispatcher.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ETA.class)
public class ETA_ {

    public static volatile SingularAttribute<ETA, Integer> count;
    public static volatile SingularAttribute<ETA, String> id;
    public static volatile SingularAttribute<ETA, ETA.Status> status;
}
