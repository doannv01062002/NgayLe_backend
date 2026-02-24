package org.example.backend.repository;

import org.example.backend.model.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Page<Conversation> findByUser_UserId(Long userId, Pageable pageable);

    Page<Conversation> findByShop_ShopId(Long shopId, Pageable pageable);

    Optional<Conversation> findByUser_UserIdAndShop_ShopId(Long userId, Long shopId);
}
