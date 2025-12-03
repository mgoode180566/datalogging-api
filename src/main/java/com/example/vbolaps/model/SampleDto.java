package com.example.vbolaps.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

//public record SampleDto(
//	Long id,
//	int seq,
//	Double time,
//	Double lat,
//	Double lng,
//	Double velocityKmh,
//	Double samplePeriod,
//	Double elapsedLapTime,
//	List<DataChannel> dataChannels)
//{}


public record SampleDto(
	Long id,
	int seq,
	LinkedHashMap<String, Double> values)
{}