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
public class SupportService {

    @Autowired
    private SupportArticleRepository supportArticleRepository;

    public Page<SupportArticleDTO> getPublicArticles(String category, String search, Pageable pageable) {
        String requestCategory = (category != null && !category.isEmpty() && !category.equals("ALL")) ? category : null;
        String requestSearch = (search != null && !search.isEmpty()) ? search : null;

        return supportArticleRepository
                .searchArticles(SupportArticle.ArticleStatus.PUBLISHED, requestCategory, requestSearch, pageable)
                .map(this::convertToDTO);
    }

    public SupportArticleDTO getArticleDetail(Long id) {
        SupportArticle article = supportArticleRepository.findById(id)
                .filter(a -> a.getStatus() == SupportArticle.ArticleStatus.PUBLISHED)
                .orElseThrow(() -> new IllegalArgumentException("Article not found or not published"));

        // Increment View Count
        article.setViewCount(article.getViewCount() + 1);
        supportArticleRepository.save(article);

        return convertToDTO(article);
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
}
