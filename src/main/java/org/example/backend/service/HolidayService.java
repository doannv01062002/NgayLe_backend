package org.example.backend.service;

import org.example.backend.dto.HolidayDTO;
import org.example.backend.model.entity.Holiday;
import org.example.backend.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    public List<HolidayDTO> getAllHolidays() {
        return holidayRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public HolidayDTO getHolidayById(Long id) {
        return holidayRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));
    }

    @Transactional
    public HolidayDTO createHoliday(HolidayDTO dto) {
        Holiday holiday = Holiday.builder()
                .name(dto.getName())
                .slug(dto.getSlug())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .themeConfigJson(dto.getThemeConfigJson())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .effectType(dto.getEffectType())
                .build();
        return mapToDTO(holidayRepository.save(holiday));
    }

    @Transactional
    public HolidayDTO updateHoliday(Long id, HolidayDTO dto) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));

        holiday.setName(dto.getName());
        holiday.setSlug(dto.getSlug());
        holiday.setStartDate(dto.getStartDate());
        holiday.setEndDate(dto.getEndDate());
        holiday.setThemeConfigJson(dto.getThemeConfigJson());
        if (dto.getIsActive() != null)
            holiday.setIsActive(dto.getIsActive());
        if (dto.getEffectType() != null)
            holiday.setEffectType(dto.getEffectType());

        return mapToDTO(holidayRepository.save(holiday));
    }

    @Transactional
    public void deleteHoliday(Long id) {
        holidayRepository.deleteById(id);
    }

    // Get the current active holiday for effects
    public HolidayDTO getActiveHolidayEffect() {
        // Simple logic: Find first active holiday where current date is within range
        // Or just find any active holiday marked as 'isActive' manually if we trust
        // admin to manage one at a time.
        // Let's rely on date range + active flag.
        LocalDate today = LocalDate.now();
        List<Holiday> activeHolidays = holidayRepository.findAll().stream()
                .filter(h -> Boolean.TRUE.equals(h.getIsActive()))
                .filter(h -> isDateInRange(today, h.getStartDate(), h.getEndDate()))
                .collect(Collectors.toList());

        if (activeHolidays.isEmpty()) {
            // Fallback: Check if there is ANY active holiday marked forced active
            // (start/end null usually implies always active, or we can just pick one)
            // For now, return null or a DTO with effect = NONE
            return null;
        }

        // Return the first match
        return mapToDTO(activeHolidays.get(0));
    }

    private boolean isDateInRange(LocalDate date, LocalDate start, LocalDate end) {
        if (start == null || end == null)
            return true; // If dates are null, assume always active? Or handle differently.
        return !date.isBefore(start) && !date.isAfter(end);
    }

    private HolidayDTO mapToDTO(Holiday holiday) {
        HolidayDTO dto = new HolidayDTO();
        dto.setHolidayId(holiday.getHolidayId());
        dto.setName(holiday.getName());
        dto.setSlug(holiday.getSlug());
        dto.setStartDate(holiday.getStartDate());
        dto.setEndDate(holiday.getEndDate());
        dto.setThemeConfigJson(holiday.getThemeConfigJson());
        dto.setIsActive(holiday.getIsActive());
        dto.setEffectType(holiday.getEffectType());
        return dto;
    }
}
