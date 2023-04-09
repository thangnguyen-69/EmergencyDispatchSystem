package com.n3t.dispatcher;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
public class Location {
  public Geometry wktToGeometry(String wellKnownText) 
  throws ParseException {
 
    return new WKTReader().read(wellKnownText);
}
}
