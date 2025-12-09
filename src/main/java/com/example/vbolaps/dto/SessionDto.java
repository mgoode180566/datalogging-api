package com.example.vbolaps.dto;

import com.example.vbolaps.model.Lap;

import java.time.LocalDate;
import java.util.List;

public record SessionDto(Long id, String circuit, String driver, String vehicle, LocalDate date, int lapCount, Double fastestLapTimeSeconds, List<String> channelNames, List<LapDto> laps){}
