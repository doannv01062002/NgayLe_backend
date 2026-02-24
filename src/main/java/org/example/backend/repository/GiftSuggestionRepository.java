package org.example.backend.repository;

import org.example.backend.model.entity.GiftSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftSuggestionRepository extends JpaRepository<GiftSuggestion, Long> {
    List<GiftSuggestion> findByHoliday_HolidayId(Long holidayId);
}
