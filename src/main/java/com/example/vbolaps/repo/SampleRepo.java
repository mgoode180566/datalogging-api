package com.example.vbolaps.repo;
import com.example.vbolaps.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SampleRepo extends JpaRepository<Sample, Long> {
    List<Sample> findByLapIdOrderBySeq(Long lapId);
}
