package org.example.backend.service;

import org.example.backend.dto.UserProfileDTO;
import org.example.backend.model.entity.User;
import org.example.backend.model.entity.UserProfile;
import org.example.backend.repository.UserProfileRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserRepository userRepository;

    public UserProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserProfile profile = userProfileRepository.findByUser(user).orElse(new UserProfile());

        UserProfileDTO dto = new UserProfileDTO();
        if (profile.getProfileId() != null) {
            dto.setProfileId(profile.getProfileId());
        }
        dto.setUserId(userId);
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole().name());
        dto.setAvatarUrl(profile.getAvatarUrl());
        dto.setBio(profile.getBio());
        dto.setTitle(profile.getTitle());
        dto.setSkills(profile.getSkills());
        dto.setExperienceYears(profile.getExperienceYears());
        dto.setHourlyRate(profile.getHourlyRate());
        dto.setNickname(profile.getNickname());
        dto.setNationality(profile.getNationality());
        dto.setGender(profile.getGender());
        dto.setDateOfBirth(profile.getDateOfBirth());
        return dto;
    }

    @Transactional
    public UserProfileDTO updateProfile(Long userId, UserProfileDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserProfile profile = userProfileRepository.findByUser(user).orElse(new UserProfile());

        if (profile.getUser() == null) {
            profile.setUser(user);
        }

        if (dto.getAvatarUrl() != null)
            profile.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getBio() != null)
            profile.setBio(dto.getBio());
        if (dto.getTitle() != null)
            profile.setTitle(dto.getTitle());
        if (dto.getSkills() != null)
            profile.setSkills(dto.getSkills());
        if (dto.getExperienceYears() != null)
            profile.setExperienceYears(dto.getExperienceYears());
        if (dto.getHourlyRate() != null)
            profile.setHourlyRate(dto.getHourlyRate());
        if (dto.getNickname() != null)
            profile.setNickname(dto.getNickname());
        if (dto.getNationality() != null)
            profile.setNationality(dto.getNationality());
        if (dto.getGender() != null)
            profile.setGender(dto.getGender());
        if (dto.getDateOfBirth() != null)
            profile.setDateOfBirth(dto.getDateOfBirth());

        // Update User info
        if (dto.getFullName() != null)
            user.setFullName(dto.getFullName());
        if (dto.getEmail() != null)
            user.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null)
            user.setPhoneNumber(dto.getPhoneNumber());
        userRepository.save(user);

        userProfileRepository.save(profile);
        return getProfile(userId);
    }
}
