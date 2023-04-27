package com.n3t.dispatcher.domain;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import com.google.type.LatLng;
import com.google.maps.routing.v2.Location;
import com.google.maps.routing.v2.Waypoint;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GeoLocation {
    public Double latitude;
    public Double longitude;
    public Point convertToPoint() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(latitude, longitude);
        return geometryFactory.createPoint(coordinate);
    }
    public Waypoint toWaypoint() {
        return Waypoint.newBuilder()
                .setLocation(Location.newBuilder().setLatLng(LatLng.newBuilder().setLatitude(latitude).setLongitude(longitude)))
                .build();
    }

}
