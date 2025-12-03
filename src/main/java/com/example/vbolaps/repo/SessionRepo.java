package com.example.vbolaps.repo;
import com.example.vbolaps.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepo extends JpaRepository<Session, Long> {}