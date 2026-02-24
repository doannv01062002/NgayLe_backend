package org.example.backend.service;

import org.example.backend.dto.ECardStatsDTO;
import org.example.backend.dto.ECardTemplateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ECardService {
        Page<ECardTemplateDTO> getTemplates(String keyword, String category, String status, Pageable pageable);

        ECardTemplateDTO createTemplate(String name, String category, Boolean isPremium, String canvasDataJson,
                        MultipartFile imageFile);

        ECardTemplateDTO updateTemplate(Long id, String name, String category, Boolean isPremium, String canvasDataJson,
                        MultipartFile imageFile);

        void deleteTemplate(Long id);

        void toggleStatus(Long id);

        ECardStatsDTO getStats();

        String uploadUserCard(MultipartFile file);
}
