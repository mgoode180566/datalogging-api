package com.example.vbolaps.repo;
import com.example.vbolaps.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SampleRepo extends JpaRepository<Sample, Long> {
    List<Sample> findByLapIdOrderBySeq(Long lapId);
}
