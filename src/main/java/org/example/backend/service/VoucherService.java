package org.example.backend.service;

import org.example.backend.dto.CreateVoucherRequest;
import org.example.backend.dto.VoucherDTO;
import org.example.backend.dto.VoucherStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface VoucherService {
    Page<VoucherDTO> getVouchers(String keyword, String type, String status, LocalDate date, Pageable pageable);

    VoucherDTO createVoucher(CreateVoucherRequest request);

    VoucherDTO updateVoucher(Long id, CreateVoucherRequest request);

    void deleteVoucher(Long id);

    VoucherDTO getVoucher(Long id);

    VoucherStatsDTO getVoucherStats();

    void togglePause(Long id);

    // Shop specific
    Page<VoucherDTO> getVouchersByOwner(Long userId, String keyword, String type, String status, Pageable pageable);

    VoucherDTO createShopVoucher(Long userId, CreateVoucherRequest request);

    VoucherDTO getShopVoucher(Long userId, Long id);

    VoucherDTO updateShopVoucher(Long userId, Long id, CreateVoucherRequest request);

    void deleteShopVoucher(Long userId, Long id);

    // Public
    java.util.List<VoucherDTO> getShopActiveVouchers(Long shopId);

    Page<VoucherDTO> getPublicVouchers(Pageable pageable);

    Page<VoucherDTO> getPublicVouchers(String username, String type, Pageable pageable);

    void saveVoucher(String email, Long voucherId);
}
