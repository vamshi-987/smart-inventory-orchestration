package com.vamshi.stockflow_backend.common.util;

public final class LocationUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private LocationUtils() {
    }

    public static double calculateDistanceKm(
            double userLat,
            double userLng,
            double warehouseLat,
            double warehouseLng
    ) {
        double latDistance = Math.toRadians(warehouseLat - userLat);
        double lngDistance = Math.toRadians(warehouseLng - userLng);

        double userLatRadians = Math.toRadians(userLat);
        double warehouseLatRadians = Math.toRadians(warehouseLat);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(userLatRadians)
                * Math.cos(warehouseLatRadians)
                * Math.sin(lngDistance / 2)
                * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}