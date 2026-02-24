package org.example.backend.repository;

import org.example.backend.model.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository
        extends JpaRepository<Report, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Report> {
    long countByTargetTypeAndStatus(Report.TargetType targetType, Report.ReportStatus status);
}
