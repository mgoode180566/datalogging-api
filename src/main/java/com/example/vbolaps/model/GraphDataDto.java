package com.example.vbolaps.model;

import java.util.List;

public record GraphDataDto(Long id, String name, String unit, List<Double> points ) {}
