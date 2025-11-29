package com.example.vbolaps.model;

public class SampleMapper {
	public static SampleDto toDto(Sample sample) {
		return new SampleDto(sample.getId(), sample.getSeq(), sample.getTime(), sample.getLat(), sample.getLon(), sample.getVelocityKmh(), sample.getSamplePeriod(), sample.getSeq() * sample.getSamplePeriod());
	}
}
