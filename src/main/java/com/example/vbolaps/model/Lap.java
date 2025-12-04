package com.example.vbolaps.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity @Getter @Setter @NoArgsConstructor
@Table(indexes = {@Index(columnList = "session_id, number")})
public class Lap {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Session session;

    private int number; // 1-based
    private double lapTimeSeconds;

    //@OneToMany(mappedBy = "lap", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Sample> samples = new ArrayList<>();
    
    @OneToMany(mappedBy = "lap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GraphData> graphs = new ArrayList<>();
}