package org.example.backend.repository;

import org.example.backend.model.entity.HomeFeatureProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeFeatureProductRepository extends JpaRepository<HomeFeatureProduct, Long> {
    List<HomeFeatureProduct> findAllBySectionTypeOrderByDisplayOrderAsc(String sectionType);

    Page<HomeFeatureProduct> findAllBySectionTypeOrderByDisplayOrderAsc(String sectionType, Pageable pageable);

    boolean existsByProduct_ProductIdAndSectionType(Long productId, String sectionType);
}
