package com.example.vbolaps.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {@Index(columnList = "lap_id, seq")})
public class Sample {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Lap lap;
    
    private int seq;           // sequential index in lap
    private double time;       // vbo 'time' if present
    private double lat;
    private double lon;
    private double velocityKmh;
    private double heading;
    private double height;
    private Double vertVel;
    private Double tsample;
    private double samplePeriod;
}
