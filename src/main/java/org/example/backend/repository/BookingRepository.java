package org.example.backend.repository;

import org.example.backend.model.entity.Booking;
import org.example.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByHirer(User hirer);

    List<Booking> findByWorker(User worker);

    List<Booking> findByStatus(Booking.Status status);
}
