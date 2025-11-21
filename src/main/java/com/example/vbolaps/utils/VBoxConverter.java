package com.example.vbolaps.utils;

public class VBoxConverter {
	public static double convertRawLatitude( double rawLatitude) {
		return rawLatitude / 60.0;
	}
	
	public static double convertRawLongitude( double rawLongitude ) {
		return -rawLongitude / 60.0;
	}
	
	public static double convertVboTime(double rawTime) {
		int hh = (int) (rawTime / 10000);                   // 11
		int mm = (int) ((rawTime % 10000) / 100);           // 35
		double ss = rawTime % 100;                          // 20.560
		
		return hh * 3600 + mm * 60 + ss;                    // seconds since midnight
	}
	
}
