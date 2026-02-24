package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.ECardStatsDTO;
import org.example.backend.dto.ECardTemplateDTO;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.entity.ECardTemplate;
import org.example.backend.repository.ECardTemplateRepository;
import org.example.backend.service.CloudinaryService;
import org.example.backend.service.ECardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ECardServiceImpl implements ECardService {

    private final ECardTemplateRepository eCardTemplateRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public Page<ECardTemplateDTO> getTemplates(String keyword, String category, String status, Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty())
            keyword = null;
        if (category != null && category.trim().isEmpty())
            category = null;
        if (status != null && status.trim().isEmpty())
            status = null;

        return eCardTemplateRepository.searchTemplates(keyword, category, status, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public ECardTemplateDTO createTemplate(String name, String category, Boolean isPremium, String canvasDataJson,
            MultipartFile imageFile) {
        String imageUrl = "";
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = cloudinaryService.uploadImage(imageFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }

        ECardTemplate template = ECardTemplate.builder()
                .name(name)
                .category(category)
                .isPremium(isPremium != null ? isPremium : false)
                .canvasDataJson(canvasDataJson != null ? canvasDataJson : "{}")
                .thumbnailUrl(imageUrl)
                .isActive(true)
                .usageCount(0)
                .build();

        return mapToDTO(eCardTemplateRepository.save(template));
    }

    @Override
    @Transactional
    public ECardTemplateDTO updateTemplate(Long id, String name, String category, Boolean isPremium,
            String canvasDataJson, MultipartFile imageFile) {
        ECardTemplate template = eCardTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));

        template.setName(name);
        if (category != null)
            template.setCategory(category);
        if (isPremium != null)
            template.setIsPremium(isPremium);
        if (canvasDataJson != null)
            template.setCanvasDataJson(canvasDataJson);

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                template.setThumbnailUrl(imageUrl);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }

        return mapToDTO(eCardTemplateRepository.save(template));
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        ECardTemplate template = eCardTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));
        eCardTemplateRepository.delete(template);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        ECardTemplate template = eCardTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));
        template.setIsActive(!template.getIsActive());
        eCardTemplateRepository.save(template);
    }

    @Override
    @Transactional(readOnly = true)
    public ECardStatsDTO getStats() {
        long total = eCardTemplateRepository.countTotalTemplates();
        long totalUsage = eCardTemplateRepository.countTotalUsage();

        // Find most popular (simple implementation)
        Page<ECardTemplate> popularPage = eCardTemplateRepository
                .findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "usageCount")));
        ECardTemplateDTO popular = null;
        if (popularPage.hasContent()) {
            popular = mapToDTO(popularPage.getContent().get(0));
        }

        return ECardStatsDTO.builder()
                .totalTemplates(total)
                .totalUsage(totalUsage)
                .popularTemplate(popular)
                .build();
    }

    private ECardTemplateDTO mapToDTO(ECardTemplate entity) {
        return ECardTemplateDTO.builder()
                .id(entity.getTemplateId())
                .name(entity.getName())
                .thumbnailUrl(entity.getThumbnailUrl())
                .category(entity.getCategory())
                .isPremium(entity.getIsPremium())
                .isActive(entity.getIsActive())
                .usageCount(entity.getUsageCount())
                .createdAt(entity.getCreatedAt())
                .canvasDataJson(entity.getCanvasDataJson())
                .build();
    }

    @Override
    public String uploadUserCard(MultipartFile file) {
        try {
            return cloudinaryService.uploadImage(file, "user_ecards");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload user card", e);
        }
    }
}
