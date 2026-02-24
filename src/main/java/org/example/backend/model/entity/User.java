package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@lombok.ToString(exclude = { "userProfile", "addresses", "orders", "productReviews", "cart", "wishlistItems",
        "sentECards" })
@lombok.EqualsAndHashCode(exclude = { "userProfile", "addresses", "orders", "productReviews", "cart", "wishlistItems",
        "sentECards" })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "social_provider")
    private String socialProvider;

    @Column(name = "social_provider_id")
    private String socialProviderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Relationships
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private java.util.List<Address> addresses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private java.util.List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private java.util.List<ProductReview> productReviews;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private java.util.List<Wishlist> wishlistItems;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private java.util.List<UserECard> sentECards;

    public enum Role {
        CUSTOMER, SELLER, ADMIN, SUPPORT, WORKER, HIRER
    }

    public enum Status {
        ACTIVE, LOCKED, BANNED
    }
}
