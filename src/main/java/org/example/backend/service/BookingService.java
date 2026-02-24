package org.example.backend.service;

import org.example.backend.dto.BookingDTO;
import org.example.backend.model.entity.Booking;
import org.example.backend.model.entity.Job;
import org.example.backend.model.entity.User;
import org.example.backend.repository.BookingRepository;
import org.example.backend.repository.JobRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BookingDTO createBooking(BookingDTO dto) {
        User hirer = userRepository.findById(dto.getHirerId())
                .orElseThrow(() -> new RuntimeException("Hirer not found"));
        User worker = userRepository.findById(dto.getWorkerId())
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        Job job = null;
        if (dto.getJobId() != null) {
            job = jobRepository.findById(dto.getJobId()).orElseThrow(() -> new RuntimeException("Job not found"));
        }

        Booking booking = Booking.builder()
                .hirer(hirer)
                .worker(worker)
                .job(job)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .location(dto.getLocation())
                .totalPrice(dto.getTotalPrice())
                .notes(dto.getNotes())
                .status(Booking.Status.PENDING)
                .build();

        booking = bookingRepository.save(booking);
        return mapToDTO(booking);
    }

    public List<BookingDTO> getBookingsByHirer(Long hirerId) {
        User hirer = userRepository.findById(hirerId).orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findByHirer(hirer).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsByWorker(Long workerId) {
        User worker = userRepository.findById(workerId).orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findByWorker(worker).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public BookingDTO updateStatus(Long bookingId, Booking.Status status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        return mapToDTO(booking);
    }

    public BookingDTO getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToDTO(booking);
    }

    private BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setHirerId(booking.getHirer().getUserId());
        dto.setHirerName(booking.getHirer().getFullName());
        dto.setWorkerId(booking.getWorker().getUserId());
        dto.setWorkerName(booking.getWorker().getFullName());
        if (booking.getJob() != null) {
            dto.setJobId(booking.getJob().getJobId());
            dto.setJobTitle(booking.getJob().getTitle());
        }
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setLocation(booking.getLocation());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setNotes(booking.getNotes());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }
}
