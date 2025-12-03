package com.example.vbolaps.repo;
import com.example.vbolaps.model.Lap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LapRepo extends JpaRepository<Lap, Long> {
    List<Lap> findBySessionIdOrderByNumber(Long sessionId);
}
