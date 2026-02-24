package org.example.backend.dto;

import lombok.Data;
import org.example.backend.model.entity.Booking;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Long bookingId;
    private Long hirerId;
    private String hirerName;
    private Long workerId;
    private String workerName;
    private Long jobId;
    private String jobTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private BigDecimal totalPrice;
    private String notes;
    private Booking.Status status;
    private LocalDateTime createdAt;
}
