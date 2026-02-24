package org.example.backend.repository;

import org.example.backend.model.entity.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    Optional<MessageReaction> findByMessage_MessageIdAndUser_UserId(Long messageId, Long userId);
}
