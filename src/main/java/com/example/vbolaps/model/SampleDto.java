package com.example.vbolaps.model;

public record SampleDto(
		Long id,
		int seq,
		Double time,
		Double lat,
		Double lng,
		Double velocityKmh,
		Double samplePeriod,
		Double elapsedLapTime )
{}
