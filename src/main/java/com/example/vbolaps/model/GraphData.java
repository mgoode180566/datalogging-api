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
	
	@Lob
	@Column(columnDefinition = "TEXT")
	private String json;
	
	@Transient
	private List<Double> points = new ArrayList<>();
}
