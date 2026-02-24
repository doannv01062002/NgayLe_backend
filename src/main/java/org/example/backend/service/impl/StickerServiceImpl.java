package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.entity.Sticker;
import org.example.backend.repository.StickerRepository;
import org.example.backend.service.CloudinaryService;
import org.example.backend.service.StickerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StickerServiceImpl implements StickerService {

    private final StickerRepository stickerRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<Sticker> getAllStickers() {
        return stickerRepository.findAll();
    }

    @Override
    @Transactional
    public Sticker createSticker(String name, String category, MultipartFile image) {
        try {
            String url = cloudinaryService.uploadImage(image, "stickers"); // Upload to 'stickers' folder
            Sticker sticker = Sticker.builder()
                    .name(name)
                    .category(category)
                    .url(url)
                    .createdAt(LocalDateTime.now())
                    .build();
            return stickerRepository.save(sticker);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload sticker image", e);
        }
    }

    @Override
    @Transactional
    public void deleteSticker(Long id) {
        stickerRepository.deleteById(id);
    }
}
