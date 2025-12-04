package com.example.vbolaps.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {@Index(columnList = "lap_id")})
public class GraphData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	private Lap lap;
	
	private String name;
	private String unit;
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String json;
	
	@Transient
	private List<Double> points = new ArrayList<>();
	
	@PostLoad
	private void loadPoints() {
		try {
			// Very lightweight parsing of JSON array of numbers
			json = (json == null) ? "[]" : json.trim();
			
			if (json.startsWith("[") && json.endsWith("]")) {
				String inner = json.substring(1, json.length() - 1).trim();
				
				if (!inner.isEmpty()) {
					String[] tokens = inner.split(",");
					points = new ArrayList<>(tokens.length);
					
					for (String t : tokens) {
						points.add(Double.parseDouble(t.trim()));
					}
				}
			}
		} catch (Exception e) {
			points = new ArrayList<>();
		}
	}
}
