package com.example.vbolaps.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter @NoArgsConstructor
public class Session {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String circuit;
    private String driver;
    private String vehicle;
    private String weather;
    private LocalDate date;
    private Instant createdAt = Instant.now();

    @JsonManagedReference
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lap> laps = new ArrayList<>();
}
