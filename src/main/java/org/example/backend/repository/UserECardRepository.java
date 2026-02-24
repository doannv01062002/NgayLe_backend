package org.example.backend.repository;

import org.example.backend.model.entity.UserECard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserECardRepository extends JpaRepository<UserECard, Long> {
    Optional<UserECard> findByViewToken(String viewToken);
}
