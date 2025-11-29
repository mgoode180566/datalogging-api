package com.example.vbolaps.model;

public class LapMapper {
	public static LapDto toDto(Lap l) {
		return new LapDto(
			l.getId(),
			l.getNumber(),
			l.getLapTimeSeconds(),
			l.getSamples().stream()
				.map(SampleMapper::toDto)
				.toList()
		);
	}
}
