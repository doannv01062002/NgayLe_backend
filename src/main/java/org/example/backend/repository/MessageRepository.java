package org.example.backend.repository;

import org.example.backend.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByConversation_ConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

    // Find unread count for a conversation and user (user is receiver, so sender is
    // NOT user)
    // Actually simpler: if I am user X, I want to count messages where conversation
    // = C AND sender != X AND isRead = false.
    Long countByConversation_ConversationIdAndSender_UserIdNotAndIsReadFalse(Long conversationId, Long userId);

    List<Message> findByConversation_ConversationIdAndSender_UserIdNotAndIsReadFalse(Long conversationId, Long userId);

    // Count messages from specific sender that are unread (For Admin view: count
    // messages SENT by User)
    Long countByConversation_ConversationIdAndSender_UserIdAndIsReadFalse(Long conversationId, Long senderId);
}
