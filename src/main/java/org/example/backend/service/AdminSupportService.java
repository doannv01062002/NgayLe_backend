package org.example.backend.service;

import org.example.backend.dto.SupportArticleDTO;
import org.example.backend.model.entity.SupportArticle;
import org.example.backend.repository.SupportArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminSupportService {

    @Autowired
    private SupportArticleRepository supportArticleRepository;

    public Page<SupportArticleDTO> getArticles(String status, String category, String search, Pageable pageable) {
        if (pageable == null)
            throw new IllegalArgumentException("Pageable cannot be null");

        SupportArticle.ArticleStatus articleStatus = null;
        if (status != null && !status.equals("ALL")) {
            articleStatus = SupportArticle.ArticleStatus.valueOf(status);
        }

        String requestCategory = (category != null && !category.isEmpty() && !category.equals("ALL")) ? category : null;
        String requestSearch = (search != null && !search.isEmpty()) ? search : null;

        return supportArticleRepository.searchArticles(articleStatus, requestCategory, requestSearch, pageable)
                .map(this::convertToDTO);
    }

    public SupportArticleDTO updateArticle(Long id, SupportArticleDTO dto) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null");
        SupportArticle article = supportArticleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory());
        article.setCampaign(dto.getCampaign());
        if (dto.getStatus() != null) {
            article.setStatus(SupportArticle.ArticleStatus.valueOf(dto.getStatus()));
        }
        article.setUpdatedBy("Admin");

        article = supportArticleRepository.save(article);
        return convertToDTO(article);
    }

    public SupportArticleDTO createArticle(SupportArticleDTO dto) {
        SupportArticle article = new SupportArticle();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory());
        article.setCampaign(dto.getCampaign());
        article.setStatus(SupportArticle.ArticleStatus.valueOf(dto.getStatus()));
        article.setUpdatedBy("Admin");

        article = supportArticleRepository.save(article);
        return convertToDTO(article);
    }

    public void deleteArticle(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        supportArticleRepository.deleteById(id);
    }

    private SupportArticleDTO convertToDTO(SupportArticle article) {
        return SupportArticleDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .category(article.getCategory())
                .campaign(article.getCampaign())
                .status(article.getStatus().name())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .updatedBy(article.getUpdatedBy())
                .build();
    }

    public org.example.backend.dto.SupportOverviewDTO getOverview() {
        try {
            long totalArticles = supportArticleRepository.count();
            long pendingArticles = supportArticleRepository.countByStatus(SupportArticle.ArticleStatus.DRAFT);
            long totalViews = supportArticleRepository.sumTotalViews();

            return org.example.backend.dto.SupportOverviewDTO.builder()
                    .totalArticles(totalArticles)
                    .pendingArticles(pendingArticles)
                    .totalViews(totalViews)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return org.example.backend.dto.SupportOverviewDTO.builder()
                    .totalArticles(0)
                    .pendingArticles(0)
                    .totalViews(0)
                    .build();
        }
    }
}
