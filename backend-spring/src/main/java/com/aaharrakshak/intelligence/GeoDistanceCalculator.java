package com.aaharrakshak.intelligence;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class GeoDistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0088;

    public double distanceKm(BigDecimal leftLatitude, BigDecimal leftLongitude, BigDecimal rightLatitude, BigDecimal rightLongitude) {
        double lat1 = Math.toRadians(leftLatitude.doubleValue());
        double lon1 = Math.toRadians(leftLongitude.doubleValue());
        double lat2 = Math.toRadians(rightLatitude.doubleValue());
        double lon2 = Math.toRadians(rightLongitude.doubleValue());
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
