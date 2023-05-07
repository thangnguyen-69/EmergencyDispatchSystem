package com.n3t.dispatcher.domain;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import com.google.type.LatLng;
import com.google.maps.routing.v2.Location;
import com.google.maps.routing.v2.Waypoint;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GeoLocation {
    public double latitude;
    public double longitude;

    public static GeoLocation fromGeometry(Geometry geom) {
        return new GeoLocation(geom.getCoordinate().y, geom.getCoordinate().x);
    }

    public static Point fromLatLngToGeometryPoint(double latitude, double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(longitude, latitude);
        return geometryFactory.createPoint(coordinate);
    }

    public Point toPoint() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(longitude, latitude);
        return geometryFactory.createPoint(coordinate);
    }

    public Waypoint toWaypoint() {
        return Waypoint.newBuilder()
                .setLocation(Location.newBuilder().setLatLng(LatLng.newBuilder().setLatitude(latitude).setLongitude(longitude)))
                .build();
    }
}
