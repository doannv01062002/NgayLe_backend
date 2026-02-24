package org.example.backend.service;

import org.example.backend.model.entity.Sticker;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface StickerService {
    List<Sticker> getAllStickers();

    Sticker createSticker(String name, String category, MultipartFile image);

    void deleteSticker(Long id);
}
