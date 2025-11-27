package com.example.vbolaps.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionMapper {
	
	private static final Logger log = LoggerFactory.getLogger(SessionMapper.class.getName());
	
	public static SessionDto toDto( Session session ) {
		log.info(session.toString());
		return new SessionDto(session.getId(),
								session.getCircuit(),
								session.getDriver(),
								session.getVehicle(),
								session.getDate(),
								session.getLaps().size(),
								session.getLaps().stream().mapToDouble(Lap::getLapTimeSeconds).min().orElse(Double.NaN));
	}
}
