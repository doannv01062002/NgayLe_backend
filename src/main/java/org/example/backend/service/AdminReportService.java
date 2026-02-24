package org.example.backend.service;

import org.example.backend.dto.ReportDTO;
import org.example.backend.model.entity.Report;
import org.example.backend.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminReportService {

    @Autowired
    private ReportRepository reportRepository;

    public Page<ReportDTO> getReports(String targetType, String status, Pageable pageable) {
        Specification<Report> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (targetType != null && !targetType.isEmpty() && !targetType.equalsIgnoreCase("ALL")) {
                predicates.add(criteriaBuilder.equal(root.get("targetType"), Report.TargetType.valueOf(targetType)));
            }

            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
                predicates.add(criteriaBuilder.equal(root.get("status"), Report.ReportStatus.valueOf(status)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return reportRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    public void updateReportStatus(Long reportId, String status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus(Report.ReportStatus.valueOf(status));
        reportRepository.save(report);
    }

    private ReportDTO convertToDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setReportId(report.getReportId());
        if (report.getReporter() != null) {
            dto.setReporterId(report.getReporter().getUserId());
            dto.setReporterName(report.getReporter().getFullName());
            dto.setReporterEmail(report.getReporter().getEmail());
        }
        dto.setTargetType(report.getTargetType().name());
        dto.setTargetId(report.getTargetId());
        dto.setReason(report.getReason());
        dto.setDescription(report.getDescription());
        dto.setStatus(report.getStatus().name());
        dto.setCreatedAt(report.getCreatedAt());
        return dto;
    }

    public org.example.backend.dto.StatsDTO getStats() {
        org.example.backend.dto.StatsDTO stats = new org.example.backend.dto.StatsDTO();
        stats.setTotal(reportRepository.count());
        stats.setPending(
                reportRepository.count((root, query, cb) -> cb.equal(root.get("status"), Report.ReportStatus.PENDING)));
        stats.setActive(reportRepository
                .count((root, query, cb) -> cb.equal(root.get("status"), Report.ReportStatus.RESOLVED)));
        stats.setBanned(reportRepository
                .count((root, query, cb) -> cb.equal(root.get("status"), Report.ReportStatus.REJECTED)));
        // Reported? No, total IS reported. Just leave it or use it for something else.
        return stats;
    }
}
