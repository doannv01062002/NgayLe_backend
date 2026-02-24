package org.example.backend.repository;

import org.example.backend.model.entity.FlashSaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashSaleProductRepository extends JpaRepository<FlashSaleProduct, Long> {
    List<FlashSaleProduct> findByFlashSaleSession_SessionId(Long sessionId);

    void deleteByFlashSaleSession_SessionId(Long sessionId);
}
