package org.example.backend.repository;

import org.example.backend.model.entity.Shop;
import org.example.backend.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>, JpaSpecificationExecutor<Shop> {
    Optional<Shop> findByOwner(User owner);

    Optional<Shop> findByOwner_UserId(Long ownerId);

    boolean existsByShopName(String shopName);

    Optional<Shop> findByShopSlug(String shopSlug);

    Page<Shop> findByStatus(Shop.ShopStatus status, Pageable pageable);
}
