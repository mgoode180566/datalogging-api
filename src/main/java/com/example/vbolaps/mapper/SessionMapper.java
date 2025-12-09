package com.example.vbolaps.mapper;

import com.example.vbolaps.dto.SessionDto;
import com.example.vbolaps.model.Lap;
import com.example.vbolaps.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class SessionMapper {
	
	private static final Logger log = LoggerFactory.getLogger(SessionMapper.class.getName());
	
	public static SessionDto toDto(Session session ) {
		
		List<String> channels = Arrays.stream(session.getChannels().split(",")).map(t -> t.trim()).toList();
		
		return new SessionDto(session.getId(),
								session.getCircuit(),
								session.getDriver(),
								session.getVehicle(),
								session.getDate(),
								session.getLaps().size(),
								session.getLaps().stream().mapToDouble(Lap::getLapTimeSeconds).min().orElse(Double.NaN),
								channels,
								session.getLaps().stream().map(LapMapper::toDto).toList());
	}
}
