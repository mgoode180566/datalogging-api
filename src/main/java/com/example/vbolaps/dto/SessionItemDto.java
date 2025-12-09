package com.example.vbolaps.dto;

import java.time.LocalDate;

public record SessionItemDto(Long id, String circuit, String driver, String vehicle, LocalDate date, int lapCount, Double fastestLapTimeSeconds){}
