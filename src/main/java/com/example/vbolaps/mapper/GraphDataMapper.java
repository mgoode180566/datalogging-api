package com.example.vbolaps.mapper;

import com.example.vbolaps.dto.GraphDataDto;
import com.example.vbolaps.model.GraphData;

public class GraphDataMapper {
	public static GraphDataDto toDto(GraphData graphData) {
		return new GraphDataDto(graphData.getId(),
			graphData.getName(),
			graphData.getUnit(),
			graphData.getPoints());
	}
}