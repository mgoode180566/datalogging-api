package com.example.vbolaps.mapper;

import com.example.vbolaps.dto.SessionItemDto;
import com.example.vbolaps.model.Lap;
import com.example.vbolaps.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionItemMapper {
	
	private static final Logger log = LoggerFactory.getLogger(SessionItemMapper.class.getName());
	
	public static SessionItemDto toDto(Session session ) {
		return new SessionItemDto(session.getId(),
								session.getCircuit(),
								session.getDriver(),
								session.getVehicle(),
								session.getDate(),
								session.getLaps().size(),
								session.getLaps().stream().mapToDouble(Lap::getLapTimeSeconds).min().orElse(Double.NaN));
	}
}
