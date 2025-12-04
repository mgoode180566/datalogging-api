package com.example.vbolaps.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SampleMapper {
	public static SampleDto toDto(Sample sample) {
		
//		values.put("lat", sample.getLat());
//		values.put("lng", sample.getLon());
//		values.put("time", sample.getTime());
//		values.put("samplePeriod", sample.getSamplePeriod());
//		values.put("velocityKmh", sample.getVelocityKmh());
//		values.put("elapsedLapTime", sample.getSeq() * sample.getSamplePeriod());
//		values.put("aviSyncTime", sample.getAviSyncTime());
		
//		for(DataChannel dataChannel : sample.getDataChannels()) {
//			values.put(dataChannel.getName(), dataChannel.getChannelValue());
//		}
		
		SampleDto sampleDto = new SampleDto(sample.getId(), sample.getSeq(), sample.getDataChannels());
		return sampleDto;
	}
}