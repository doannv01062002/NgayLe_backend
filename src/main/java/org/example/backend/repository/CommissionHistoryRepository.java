package org.example.backend.repository;

import org.example.backend.model.entity.CommissionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionHistoryRepository extends JpaRepository<CommissionHistory, Long> {
    Page<CommissionHistory> findByPartnerId(Long partnerId, Pageable pageable);
}
