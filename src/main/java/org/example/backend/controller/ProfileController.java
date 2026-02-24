package org.example.backend.controller;

import org.example.backend.dto.UserProfileDTO;
import org.example.backend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    @Autowired
    private org.example.backend.service.CloudinaryService cloudinaryService;

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> updateProfile(@PathVariable Long userId, @RequestBody UserProfileDTO dto) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, dto));
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<String> uploadAvatar(@PathVariable Long userId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            String url = cloudinaryService.uploadImage(file);
            UserProfileDTO dto = new UserProfileDTO();
            dto.setAvatarUrl(url);
            userProfileService.updateProfile(userId, dto);
            return ResponseEntity.ok(url);
        } catch (java.io.IOException e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}
