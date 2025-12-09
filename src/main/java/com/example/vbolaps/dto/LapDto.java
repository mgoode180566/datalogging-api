package com.example.vbolaps.dto;

import java.util.List;

public record LapDto(
		Long id,
		int lapNumber,
		double lapTimeSeconds,
		List<GraphDataDto> graphs
	)
{}

