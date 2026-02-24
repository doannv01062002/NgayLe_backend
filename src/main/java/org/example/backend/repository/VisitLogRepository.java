package org.example.backend.repository;

import org.example.backend.model.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {
    long countByVisitTimeBetween(LocalDateTime start, LocalDateTime end);
}
