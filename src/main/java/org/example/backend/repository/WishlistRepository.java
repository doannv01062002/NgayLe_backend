package org.example.backend.repository;

import org.example.backend.model.entity.Wishlist;
import org.example.backend.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Page<Wishlist> findByUser(User user, Pageable pageable);

    Optional<Wishlist> findByUserAndProduct_ProductId(User user, Long productId);

    boolean existsByUserAndProduct_ProductId(User user, Long productId);
}
