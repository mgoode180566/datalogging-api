package com.example.vbolaps.model;

import java.util.List;
import java.util.Map;

public record LapDto(
		Long id,
		int lapNumber,
		double lapTimeSeconds,
		List<GraphDataDto> graphDataDtos
	)
{}

