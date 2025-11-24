package com.example.vbolaps.model;

public class SessionMapper {
	public static SessionDto toDto( Session session ) {
		return new SessionDto(session.getId(), session.getCircuit(), session.getDriver(), session.getVehicle(), session.getDate());
	}
}
