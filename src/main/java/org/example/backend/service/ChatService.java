package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.ConversationResponse;
import org.example.backend.dto.MessageResponse;
import org.example.backend.model.entity.*;
import org.example.backend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

        private final ConversationRepository conversationRepository;
        private final MessageRepository messageRepository;
        private final UserRepository userRepository;
        private final ShopRepository shopRepository;
        private final MessageReactionRepository messageReactionRepository;
        private final SimpMessagingTemplate messagingTemplate;

        @Transactional(readOnly = true)
        public List<ConversationResponse> getUserConversations(Long userId) {
                // As a Customer
                List<Conversation> conversations = conversationRepository.findByUser_UserId(userId, Pageable.unpaged())
                                .getContent();
                return conversations.stream().map(c -> {
                        Long unread = messageRepository
                                        .countByConversation_ConversationIdAndSender_UserIdNotAndIsReadFalse(
                                                        c.getConversationId(), userId);
                        String avatar = null;
                        String name = "Unknown";
                        Long partnerId = null;

                        if (c.getShop() != null) {
                                avatar = c.getShop().getLogoUrl();
                                name = c.getShop().getShopName();
                                partnerId = c.getShop().getShopId();
                        } else if ("ADMIN".equals(c.getConversationType())) {
                                name = "Admin Support";
                                avatar = "/admin-avatar.png";
                                partnerId = 0L; // ID 0 for admin
                        }

                        return ConversationResponse.builder()
                                        .id(c.getConversationId())
                                        .name(name)
                                        .avatar(avatar)
                                        .lastMessage(c.getLastMessageContent())
                                        .time(c.getLastMessageTime())
                                        .unread(unread)
                                        .type(c.getConversationType() != null ? c.getConversationType() : "SHOP")
                                        .partnerId(partnerId)
                                        .build();
                }).collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<ConversationResponse> getShopConversations(Long userId) {
                // As a Shop Owner
                Shop shop = shopRepository.findByOwner_UserId(userId)
                                .orElseThrow(() -> new RuntimeException("Shop not found for user"));

                // 1. Shop Conversations (Customers talking to Shop)
                List<Conversation> shopConversations = conversationRepository
                                .findByShop_ShopId(shop.getShopId(), Pageable.unpaged()).getContent();

                // 2. Admin Conversations (Shop Owner talking to Admin)
                List<Conversation> adminConversations = conversationRepository
                                .findByUser_UserId(userId, Pageable.unpaged()).getContent().stream()
                                .filter(c -> "ADMIN".equals(c.getConversationType()))
                                .collect(Collectors.toList());

                // 3. Merge and Deduplicate
                java.util.Map<Long, Conversation> uniqueConversations = new java.util.HashMap<>();
                shopConversations.forEach(c -> uniqueConversations.put(c.getConversationId(), c));
                adminConversations.forEach(c -> uniqueConversations.put(c.getConversationId(), c));

                java.util.List<Conversation> allConversations = new java.util.ArrayList<>(uniqueConversations.values());
                allConversations.sort((c1, c2) -> c2.getLastMessageTime().compareTo(c1.getLastMessageTime()));

                return allConversations.stream().map(c -> {
                        if ("ADMIN".equals(c.getConversationType())) {
                                // Admin Chat Mapping
                                Long unread = messageRepository
                                                .countByConversation_ConversationIdAndSender_UserIdNotAndIsReadFalse(
                                                                c.getConversationId(), userId);
                                return ConversationResponse.builder()
                                                .id(c.getConversationId())
                                                .name("Admin Support")
                                                .avatar("/admin-avatar.png")
                                                .lastMessage(c.getLastMessageContent())
                                                .time(c.getLastMessageTime())
                                                .unread(unread)
                                                .type("SUPPORT") // Frontend likely expects SUPPORT or ADMIN
                                                .partnerId(0L)
                                                .build();
                        } else {
                                // Customer Chat Mapping
                                Long unread = messageRepository
                                                .countByConversation_ConversationIdAndSender_UserIdNotAndIsReadFalse(
                                                                c.getConversationId(), userId);
                                return ConversationResponse.builder()
                                                .id(c.getConversationId())
                                                .name(c.getUser().getFullName())
                                                .avatar(c.getUser().getUserProfile() != null
                                                                ? c.getUser().getUserProfile().getAvatarUrl()
                                                                : null)
                                                .lastMessage(c.getLastMessageContent())
                                                .time(c.getLastMessageTime())
                                                .unread(unread)
                                                .type("USER")
                                                .partnerId(c.getUser().getUserId())
                                                .build();
                        }
                })
                                .sorted((c1, c2) -> {
                                        if (c1.getTime() == null && c2.getTime() == null)
                                                return 0;
                                        if (c1.getTime() == null)
                                                return 1;
                                        if (c2.getTime() == null)
                                                return -1;
                                        return c2.getTime().compareTo(c1.getTime());
                                })
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public Page<MessageResponse> getMessages(Long conversationId, Pageable pageable) {
                return messageRepository.findByConversation_ConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                                .map(this::mapToMessageResponse);
        }

        @Transactional
        public MessageResponse sendMessage(Long conversationId, String content, String mediaUrl, Long senderId) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new RuntimeException("Conversation not found"));

                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Update Conversation with last message content (or [Image] if only image)
                String displayContent = (content == null || content.trim().isEmpty()) ? "[Hình ảnh]" : content;
                conversation.setLastMessageContent(displayContent);
                conversation.setLastMessageTime(LocalDateTime.now());
                conversationRepository.save(conversation);

                // Save Message
                Message message = Message.builder()
                                .conversation(conversation)
                                .sender(sender)
                                .content(content)
                                .mediaUrl(mediaUrl)
                                .isRead(false)
                                .createdAt(LocalDateTime.now())
                                .build();
                message = messageRepository.save(message);

                MessageResponse response = mapToMessageResponse(message);

                // Notify WebSocket
                if ("ADMIN".equals(conversation.getConversationType())) {
                        // Broadcast to Admin Topic and the User (Partner) Topic
                        messagingTemplate.convertAndSend("/topic/admin", response);
                        messagingTemplate.convertAndSend("/topic/user." + conversation.getUser().getUserId(), response);
                } else if (conversation.getShop() != null) {
                        Long recipientId;
                        if (conversation.getUser().getUserId().equals(senderId)) {
                                recipientId = conversation.getShop().getOwner().getUserId();
                        } else {
                                recipientId = conversation.getUser().getUserId();
                        }
                        messagingTemplate.convertAndSend("/topic/user." + recipientId, response);
                        messagingTemplate.convertAndSend("/topic/user." + senderId, response);
                } else {
                        // Fallback or unknown type
                        messagingTemplate.convertAndSend("/topic/user." + senderId, response);
                }

                return response;
        }

        @Transactional
        public MessageResponse reactToMessage(Long messageId, org.example.backend.model.enums.ReactionType type,
                        Long userId) {
                Message message = messageRepository.findById(messageId)
                                .orElseThrow(() -> new RuntimeException("Message not found"));

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Check if reaction exists
                java.util.Optional<MessageReaction> existingReaction = messageReactionRepository
                                .findByMessage_MessageIdAndUser_UserId(messageId, userId);

                if (existingReaction.isPresent()) {
                        MessageReaction reaction = existingReaction.get();
                        if (reaction.getReaction() == type) {
                                // Remove if same type (toggle)
                                messageReactionRepository.delete(reaction);
                                message.getReactions().remove(reaction); // keep memory sync for response mapping
                        } else {
                                // Update type
                                reaction.setReaction(type);
                                messageReactionRepository.save(reaction);
                        }
                } else {
                        // Create new
                        MessageReaction newReaction = MessageReaction.builder()
                                        .message(message)
                                        .user(user)
                                        .reaction(type)
                                        .createdAt(LocalDateTime.now())
                                        .build();
                        messageReactionRepository.save(newReaction);
                        if (message.getReactions() == null) {
                                message.setReactions(new java.util.ArrayList<>());
                        }
                        message.getReactions().add(newReaction);
                }

                // Force refresh of reactions from DB or use the updated list
                // Just fetching updated entity is safer
                // message = messageRepository.findById(messageId).orElseThrow();
                // Actually, we can just return the updated message. Since we modified the DB,
                // let's re-fetch to be sure of state or manually construct.
                // Mapping existing message object is fine if we updated the list correctly, but
                // DB save logic handles persistence.
                // Let's re-fetch to ensure complete state
                Message updatedMessage = messageRepository.findById(messageId).orElseThrow();

                MessageResponse response = mapToMessageResponse(updatedMessage);

                // Broadcast update to conversation
                // Broadcast update to conversation
                Conversation conversation = updatedMessage.getConversation();

                if ("ADMIN".equals(conversation.getConversationType())) {
                        messagingTemplate.convertAndSend("/topic/admin", response);
                        messagingTemplate.convertAndSend("/topic/user." + conversation.getUser().getUserId(), response);
                } else if (conversation.getShop() != null) {
                        Long shopOwnerId = conversation.getShop().getOwner().getUserId();
                        Long customerId = conversation.getUser().getUserId();

                        messagingTemplate.convertAndSend("/topic/user." + shopOwnerId, response);
                        messagingTemplate.convertAndSend("/topic/user." + customerId, response);
                }

                return response;
        }

        @Transactional
        public ConversationResponse startConversation(Long userId, Long shopId) {
                return conversationRepository.findByUser_UserIdAndShop_ShopId(userId, shopId)
                                .map(c -> {
                                        // Return existing
                                        Long unread = messageRepository
                                                        .countByConversation_ConversationIdAndSender_UserIdNotAndIsReadFalse(
                                                                        c.getConversationId(), userId);
                                        return ConversationResponse.builder()
                                                        .id(c.getConversationId())
                                                        .name(c.getShop().getShopName())
                                                        .avatar(c.getShop().getLogoUrl())
                                                        .lastMessage(c.getLastMessageContent())
                                                        .time(c.getLastMessageTime())
                                                        .unread(unread)
                                                        .type("SHOP")
                                                        .partnerId(c.getShop().getShopId())
                                                        .build();
                                })
                                .orElseGet(() -> {
                                        // Create new
                                        User user = userRepository.findById(userId).orElseThrow();
                                        Shop shop = shopRepository.findById(shopId).orElseThrow();

                                        Conversation c = Conversation.builder()
                                                        .user(user)
                                                        .shop(shop)
                                                        .lastMessageContent("Started conversation")
                                                        .lastMessageTime(LocalDateTime.now())
                                                        .createdAt(LocalDateTime.now())
                                                        .build();
                                        c = conversationRepository.save(c);

                                        return ConversationResponse.builder()
                                                        .id(c.getConversationId())
                                                        .name(shop.getShopName())
                                                        .avatar(shop.getLogoUrl())
                                                        .lastMessage(c.getLastMessageContent())
                                                        .time(c.getLastMessageTime())
                                                        .unread(0L)
                                                        .type("SHOP")
                                                        .partnerId(shop.getShopId())
                                                        .build();
                                });

        }

        @Transactional
        public ConversationResponse startAdminConversation(Long userId) {
                return conversationRepository.findByUser_UserId(userId, Pageable.unpaged()).getContent().stream()
                                .filter(c -> "ADMIN".equals(c.getConversationType()))
                                .findFirst()
                                .map(c -> {
                                        Long unread = messageRepository
                                                        .countByConversation_ConversationIdAndSender_UserIdNotAndIsReadFalse(
                                                                        c.getConversationId(), userId);
                                        return ConversationResponse.builder()
                                                        .id(c.getConversationId())
                                                        .name("Admin Support")
                                                        .avatar("/admin-avatar.png")
                                                        .lastMessage(c.getLastMessageContent())
                                                        .time(c.getLastMessageTime())
                                                        .unread(unread)
                                                        .type("SUPPORT")
                                                        .partnerId(0L)
                                                        .build();
                                })
                                .orElseGet(() -> {
                                        User user = userRepository.findById(userId)
                                                        .orElseThrow(() -> new RuntimeException("User not found"));

                                        // Try to find the user's shop to satisfy NOT NULL constraint
                                        // If user is not a shop owner, we might still have an issue,
                                        // but the requirement is for Seller Dashboard.
                                        Shop shop = shopRepository.findByOwner_UserId(userId).orElse(null);

                                        // If shop is null (regular user), we really should have a workaround if the DB
                                        // enforces Not Null.
                                        // But for this task, the user is a Seller.

                                        Conversation c = Conversation.builder()
                                                        .user(user)
                                                        .shop(shop) // Set the user's shop
                                                        .conversationType("ADMIN")
                                                        .lastMessageContent("Started conversation with Admin")
                                                        .lastMessageTime(LocalDateTime.now())
                                                        .createdAt(LocalDateTime.now())
                                                        .build();
                                        c = conversationRepository.save(c);

                                        return ConversationResponse.builder()
                                                        .id(c.getConversationId())
                                                        .name("Admin Support")
                                                        .avatar("/admin-avatar.png")
                                                        .lastMessage(c.getLastMessageContent())
                                                        .time(c.getLastMessageTime())
                                                        .unread(0L)
                                                        .type("SUPPORT")
                                                        .partnerId(0L)
                                                        .build();
                                });
        }

        @Transactional
        public void markAsRead(Long conversationId, Long userId) {
                // Find all unread messages in this conversation where I am the receiver (so
                // sender != me)
                List<Message> unreadMessages = messageRepository
                                .findByConversation_ConversationIdAndSender_UserIdNotAndIsReadFalse(conversationId,
                                                userId);

                if (unreadMessages.isEmpty()) {
                        return;
                }

                unreadMessages.forEach(m -> m.setIsRead(true));
                messageRepository.saveAll(unreadMessages);
        }

        private MessageResponse mapToMessageResponse(Message m) {
                java.util.List<org.example.backend.dto.ReactionDto> reactionDtos = new java.util.ArrayList<>();
                if (m.getReactions() != null) {
                        reactionDtos = m.getReactions().stream()
                                        .map(r -> org.example.backend.dto.ReactionDto.builder()
                                                        .id(r.getId())
                                                        .userId(r.getUser().getUserId())
                                                        .userName(r.getUser().getFullName())
                                                        .type(r.getReaction())
                                                        .build())
                                        .collect(Collectors.toList());
                }

                return MessageResponse.builder()
                                .id(m.getMessageId())
                                .conversationId(m.getConversation().getConversationId())
                                .content(m.getContent())
                                .mediaUrl(m.getMediaUrl())
                                .senderId(m.getSender().getUserId())
                                .senderName(m.getSender().getFullName())
                                .createdAt(m.getCreatedAt())
                                .isRead(m.getIsRead())
                                .reactions(reactionDtos)
                                .build();
        }

        @Transactional(readOnly = true)
        public List<ConversationResponse> getAdminConversations(String keyword) {
                // Find ADMIN conversations
                List<Conversation> conversations = conversationRepository.findAll().stream()
                                .filter(c -> "ADMIN".equals(c.getConversationType()))
                                .filter(c -> {
                                        if (keyword == null || keyword.isEmpty())
                                                return true;
                                        // Filter by user name
                                        return c.getUser().getFullName().toLowerCase().contains(keyword.toLowerCase());
                                })
                                .sorted((c1, c2) -> {
                                        if (c1.getLastMessageTime() == null && c2.getLastMessageTime() == null)
                                                return 0;
                                        if (c1.getLastMessageTime() == null)
                                                return 1;
                                        if (c2.getLastMessageTime() == null)
                                                return -1;
                                        return c2.getLastMessageTime().compareTo(c1.getLastMessageTime());
                                })
                                .collect(Collectors.toList());

                return conversations.stream().map(c -> {
                        // Count messages sent by User (not Admin) that are unread
                        // Assuming Admin ID is not specific, but we check if sender is the User.
                        Long adminUnread = messageRepository
                                        .countByConversation_ConversationIdAndSender_UserIdAndIsReadFalse(
                                                        c.getConversationId(), c.getUser().getUserId());

                        return ConversationResponse.builder()
                                        .id(c.getConversationId())
                                        .name(c.getUser().getFullName())
                                        .avatar(c.getUser().getUserProfile() != null
                                                        ? c.getUser().getUserProfile().getAvatarUrl()
                                                        : null)
                                        .lastMessage(c.getLastMessageContent())
                                        .time(c.getLastMessageTime())
                                        .unread(adminUnread)
                                        .type("ADMIN")
                                        .partnerId(c.getUser().getUserId())
                                        .build();
                }).collect(Collectors.toList());
        }

        @Transactional
        public MessageResponse sendAdminMessage(Long conversationId, String content, String mediaUrl) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new RuntimeException("Conversation not found"));

                // Use a generic Admin User or find one
                // Use a generic Admin User or find one
                User adminUser = getAdminUser();

                // Update Conversation
                String displayContent = (content == null || content.trim().isEmpty()) ? "[Hình ảnh]" : content;
                conversation.setLastMessageContent(displayContent);
                conversation.setLastMessageTime(LocalDateTime.now());
                conversationRepository.save(conversation);

                Message message = Message.builder()
                                .conversation(conversation)
                                .sender(adminUser)
                                .content(content)
                                .mediaUrl(mediaUrl)
                                .isRead(false)
                                .createdAt(LocalDateTime.now())
                                .build();
                message = messageRepository.save(message);

                MessageResponse response = mapToMessageResponse(message);

                // Notify User
                messagingTemplate.convertAndSend("/topic/user." + conversation.getUser().getUserId(), response);
                // Notify Admin (all admins listening on topic admin)
                messagingTemplate.convertAndSend("/topic/admin", response);

                return response;
        }

        @Transactional(readOnly = true)
        public org.example.backend.dto.ChatContextResponse getChatContext(Long conversationId) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new RuntimeException("Conversation not found"));

                User user = conversation.getUser();

                String addressStr = "N/A";
                if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
                        Address addr = user.getAddresses().get(0);
                        addressStr = addr.getAddressLine1() + ", " + addr.getCity();
                }

                org.example.backend.dto.ChatContextResponse.ChatContextResponseBuilder builder = org.example.backend.dto.ChatContextResponse
                                .builder()
                                .userId(user.getUserId())
                                .userName(user.getFullName())
                                .userEmail(user.getEmail())
                                .userPhone(user.getPhoneNumber())
                                .userAddress(addressStr)
                                .userAvatar(user.getUserProfile() != null ? user.getUserProfile().getAvatarUrl() : null)
                                .rank("Thành viên");

                if (conversation.getRelatedOrderId() != null) {
                        builder.orderId(conversation.getRelatedOrderId());
                        builder.orderStatus("Đang xử lý");
                        builder.totalAmount(0.0);
                }

                return builder.build();
        }

        @Transactional
        public void markAsReadByAdmin(Long conversationId) {
                User adminUser = getAdminUser();
                markAsRead(conversationId, adminUser.getUserId());
        }

        @Transactional
        public MessageResponse reactAsAdmin(Long messageId, org.example.backend.model.enums.ReactionType type) {
                User adminUser = getAdminUser();
                return reactToMessage(messageId, type, adminUser.getUserId());
        }

        private User getAdminUser() {
                return userRepository.findByEmail("admin@ngayle.com")
                                .orElseGet(() -> userRepository.findAll().stream()
                                                .filter(u -> "ADMIN".equalsIgnoreCase(u.getRole().toString()))
                                                .findFirst().orElseThrow(() -> new RuntimeException("No admin found")));
        }
}
