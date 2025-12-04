package com.example.vbolaps.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
//    private double time;       // vbo 'time' if present
//    private int satellites;
//    private double lat;
//    private double lon;
//    private double velocityKmh;
//    private double heading;
//    private double height;
//    private double vertVel;
//    private double tsample;
//    private double samplePeriod;
//    private double aviFileIndex;
//    private double aviSyncTime;
//    private double solutionType;
    
    @OneToMany(
      mappedBy = "sample",
      cascade = CascadeType.ALL,
      orphanRemoval = true
    )
    @JsonManagedReference
    private List<DataChannel> dataChannels = new ArrayList<>();
}
