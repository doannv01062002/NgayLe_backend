package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_ecards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserECard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ECardTemplate template;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_email")
    private String recipientEmail;

    // Modified canvas data (user's customization)
    @Column(name = "customized_data_json", columnDefinition = "json")
    private String customizedDataJson;

    // The final rendered image url (snapshot)
    @Column(name = "final_image_url")
    private String finalImageUrl;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_sent")
    @Builder.Default
    private Boolean isSent = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "is_viewed")
    @Builder.Default
    private Boolean isViewed = false;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Unique token for external viewing without login
    @Column(name = "view_token", unique = true)
    private String viewToken;
}
