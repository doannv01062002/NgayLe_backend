package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.ConversationResponse;
import org.example.backend.dto.MessageResponse;
import org.example.backend.security.CustomUserDetails;
import org.example.backend.service.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ApiChatController {

    private final ChatService chatService;
    private final org.example.backend.service.CloudinaryService cloudinaryService;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getUserConversations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.getUserConversations(userDetails.getUserId()));
    }

    @GetMapping("/conversations/shop")
    public ResponseEntity<List<ConversationResponse>> getShopConversations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.getShopConversations(userDetails.getUserId()));
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                chatService.getMessages(conversationId, PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, Object> payload) {
        Long conversationId = Long.valueOf(payload.get("conversationId").toString());
        String content = payload.get("content") != null ? payload.get("content").toString() : "";
        String mediaUrl = payload.get("mediaUrl") != null ? payload.get("mediaUrl").toString() : null;
        return ResponseEntity.ok(chatService.sendMessage(conversationId, content, mediaUrl, userDetails.getUserId()));
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            String url = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{messageId}/react")
    public ResponseEntity<MessageResponse> reactToMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, String> payload) {
        org.example.backend.model.enums.ReactionType type = org.example.backend.model.enums.ReactionType
                .valueOf(payload.get("type"));
        return ResponseEntity.ok(chatService.reactToMessage(messageId, type, userDetails.getUserId()));
    }

    @PostMapping("/start")
    public ResponseEntity<ConversationResponse> startConversation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, Long> payload) {
        Long shopId = payload.get("shopId");
        return ResponseEntity.ok(chatService.startConversation(userDetails.getUserId(), shopId));
    }

    @PostMapping("/start/admin")
    public ResponseEntity<ConversationResponse> startAdminConversation(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.startAdminConversation(userDetails.getUserId()));
    }

    @PutMapping("/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatService.markAsRead(conversationId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}
