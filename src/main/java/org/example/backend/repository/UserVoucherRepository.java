package org.example.backend.repository;

import org.example.backend.model.entity.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {
    Optional<UserVoucher> findByUserUserIdAndVoucherId(Long userId, Long voucherId);

    boolean existsByUserUserIdAndVoucherId(Long userId, Long voucherId);

    @org.springframework.data.jpa.repository.Query("SELECT uv.voucher.id FROM UserVoucher uv WHERE uv.user.userId = :userId")
    java.util.List<Long> findVoucherIdsByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}
