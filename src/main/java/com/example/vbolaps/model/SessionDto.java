package com.example.vbolaps.model;

import java.time.LocalDate;

public record SessionDto (Long id, String circuit, String driver, String vehicle, LocalDate date){}
