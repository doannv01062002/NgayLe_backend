package org.example.backend.controller;

import org.example.backend.dto.HolidayDTO;
import org.example.backend.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping
    public ResponseEntity<List<HolidayDTO>> getAllHolidays() {
        return ResponseEntity.ok(holidayService.getAllHolidays());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HolidayDTO> getHolidayById(@PathVariable Long id) {
        return ResponseEntity.ok(holidayService.getHolidayById(id));
    }

    @PostMapping
    public ResponseEntity<HolidayDTO> createHoliday(@RequestBody HolidayDTO dto) {
        return ResponseEntity.ok(holidayService.createHoliday(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HolidayDTO> updateHoliday(@PathVariable Long id, @RequestBody HolidayDTO dto) {
        return ResponseEntity.ok(holidayService.updateHoliday(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active-effect")
    public ResponseEntity<HolidayDTO> getActiveHolidayEffect() {
        HolidayDTO dto = holidayService.getActiveHolidayEffect();
        if (dto == null) {
            dto = new HolidayDTO();
            dto.setEffectType("NONE"); // Default
        }
        return ResponseEntity.ok(dto);
    }
}
