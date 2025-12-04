package com.example.vbolaps.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataChannel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
  private String name;
	private Double cValue;
	
	@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "sample_id")
	@JsonBackReference
	private Sample sample;
}
