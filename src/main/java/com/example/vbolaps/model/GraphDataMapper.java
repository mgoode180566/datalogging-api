package com.example.vbolaps.model;

public class GraphDataMapper {
	public static GraphDataDto toDto(GraphData graphData) {
		return new GraphDataDto(graphData.getId(),
			graphData.getName(),
			graphData.getUnit(),
			graphData.getPoints());
	}
}