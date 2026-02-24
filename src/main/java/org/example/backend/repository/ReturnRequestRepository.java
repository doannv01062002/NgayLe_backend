package org.example.backend.repository;

import org.example.backend.model.entity.ReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    Page<ReturnRequest> findByShop_ShopId(Long shopId, Pageable pageable);

    Page<ReturnRequest> findByUser_UserId(Long userId, Pageable pageable);
}
