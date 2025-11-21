package com.example.vbolaps.service;

import com.example.vbolaps.utils.VBoxConverter;

import java.util.*;

public class LapDetector {
    private static double sqr(double x){ return x*x; }

    /**
     * Simple proximity-based lap detection: whenever the point comes within a threshold
     * of the start/finish coordinate, we mark a new lap (on rising edge).
     */
    public static List<Integer> detectLaps(List<Map<String, Double>> points, double[] startLatLon) {
        
        double lat0 = startLatLon[0];
        double lon0 = startLatLon[1];
        
        final double thresholdMeters = 20.0;  // detection radius
        boolean inside = false;
        int lap = 0;
        
        List<Integer> laps = new ArrayList<>(points.size());
        
        for (int i = 0; i < points.size(); i++) {
            
            Map<String, Double> v = points.get(i);
            
            double lat = VBoxConverter.convertRawLatitude(
              v.getOrDefault("latitude", Double.NaN)
            );
            double lon = VBoxConverter.convertRawLongitude(
              v.getOrDefault("longitude", Double.NaN)
            );
            
            boolean cur = haversine(lat, lon, lat0, lon0) < thresholdMeters;
            
            if (cur && !inside) {
                lap++;
            }
            
            inside = cur;
            laps.add(lap);
        }
        
        return laps;
    }
    
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000; // Earth radius in meters
        
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dphi = Math.toRadians(lat2 - lat1);
        double dlambda = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dphi / 2) * Math.sin(dphi / 2) +
          Math.cos(phi1) * Math.cos(phi2) *
            Math.sin(dlambda / 2) * Math.sin(dlambda / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;   // distance in meters
    }
    
    /** Fallback: use first point as start line */
    public static List<Integer> detectLapsFirstPoint(List<Map<String, Double>> points) {
        if (points.isEmpty()) return List.of();
        double lat0 = points.get(0).getOrDefault("lat", 0.0);
        double lon0 = points.get(0).getOrDefault("long", 0.0);
        return detectLaps(points, new double[]{lat0, lon0});
    }
}
