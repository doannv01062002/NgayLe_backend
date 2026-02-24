package org.example.backend.repository;

import org.example.backend.model.entity.SupportArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportArticleRepository extends JpaRepository<SupportArticle, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT a FROM SupportArticle a WHERE (:status IS NULL OR a.status = :status) AND (:category IS NULL OR a.category = :category) AND (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SupportArticle> searchArticles(
            @org.springframework.data.repository.query.Param("status") SupportArticle.ArticleStatus status,
            @org.springframework.data.repository.query.Param("category") String category,
            @org.springframework.data.repository.query.Param("search") String search,
            Pageable pageable);

    long countByStatus(SupportArticle.ArticleStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(a.viewCount), 0) FROM SupportArticle a")
    long sumTotalViews();
}
