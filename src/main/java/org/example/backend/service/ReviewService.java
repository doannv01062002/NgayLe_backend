package org.example.backend.service;

import org.example.backend.dto.ReviewDTO;
import org.example.backend.model.entity.Booking;
import org.example.backend.model.entity.Review;
import org.example.backend.model.entity.User;
import org.example.backend.repository.BookingRepository;
import org.example.backend.repository.ReviewRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ReviewDTO createReview(ReviewDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        User reviewer = userRepository.findById(dto.getReviewerId())
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));
        User reviewee = userRepository.findById(dto.getRevieweeId())
                .orElseThrow(() -> new RuntimeException("Reviewee not found"));

        Review review = Review.builder()
                .booking(booking)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        review = reviewRepository.save(review);
        return mapToDTO(review);
    }

    public List<ReviewDTO> getReviewsDataForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return reviewRepository.findByReviewee(user).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ReviewDTO mapToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setBookingId(review.getBooking().getBookingId());
        dto.setReviewerId(review.getReviewer().getUserId());
        dto.setReviewerName(review.getReviewer().getFullName());
        dto.setRevieweeId(review.getReviewee().getUserId());
        dto.setRevieweeName(review.getReviewee().getFullName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
