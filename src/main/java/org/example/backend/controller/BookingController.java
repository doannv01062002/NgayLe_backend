package org.example.backend.controller;

import org.example.backend.dto.BookingDTO;
import org.example.backend.model.entity.Booking;
import org.example.backend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO dto) {
        return ResponseEntity.ok(bookingService.createBooking(dto));
    }

    @GetMapping("/hirer/{hirerId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByHirer(@PathVariable Long hirerId) {
        return ResponseEntity.ok(bookingService.getBookingsByHirer(hirerId));
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByWorker(@PathVariable Long workerId) {
        return ResponseEntity.ok(bookingService.getBookingsByWorker(workerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BookingDTO> updateStatus(@PathVariable Long id, @RequestParam Booking.Status status) {
        return ResponseEntity.ok(bookingService.updateStatus(id, status));
    }
}
