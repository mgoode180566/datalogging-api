package com.example.vbolaps.model;

import java.util.List;

public record LapDto(
		Long id,
		int lapNumber,
		double lapTimeSeconds,
		List<SampleDto> samples
	)
{}

