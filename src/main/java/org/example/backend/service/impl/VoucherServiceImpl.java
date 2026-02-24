package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.CreateVoucherRequest;
import org.example.backend.dto.VoucherDTO;
import org.example.backend.dto.VoucherStatsDTO;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.entity.Voucher;
import org.example.backend.repository.VoucherRepository;
import org.example.backend.service.VoucherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final org.example.backend.repository.ShopRepository shopRepository;
    private final org.example.backend.repository.UserRepository userRepository;
    private final org.example.backend.repository.UserVoucherRepository userVoucherRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> getVouchers(String keyword, String type, String status, LocalDate date, Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty())
            keyword = null;
        if (type != null && type.trim().isEmpty())
            type = null;
        if (status != null && status.trim().isEmpty())
            status = null;

        Page<Voucher> vouchers = voucherRepository.searchVouchers(keyword, type, status, date, pageable);
        return vouchers.map(this::mapToDTO);
    }

    @Override
    @Transactional
    public VoucherDTO createVoucher(CreateVoucherRequest request) {
        if (voucherRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Code already exists");
        }

        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderValue(request.getMinOrderValue())
                .usageLimit(request.getUsageLimit())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(true)
                .build();

        return mapToDTO(voucherRepository.save(voucher));
    }

    @Override
    @Transactional
    public VoucherDTO updateVoucher(Long id, CreateVoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        // Code update check if needed? Usually code is immutable, but let's allow it if
        // unique
        if (!voucher.getCode().equals(request.getCode()) && voucherRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Code already exists");
        }

        voucher.setCode(request.getCode());
        voucher.setName(request.getName());
        voucher.setDescription(request.getDescription());
        voucher.setType(request.getType());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setMinOrderValue(request.getMinOrderValue());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());

        return mapToDTO(voucherRepository.save(voucher));
    }

    @Override
    @Transactional
    public void deleteVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));
        voucherRepository.delete(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherDTO getVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));
        return mapToDTO(voucher);
    }

    @Override
    @Transactional
    public void togglePause(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));
        voucher.setIsActive(!voucher.getIsActive());
        voucherRepository.save(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> getVouchersByOwner(Long userId, String keyword, String type, String status,
            Pageable pageable) {
        // Find Shop ID
        var shopOpt = shopRepository.findByOwner_UserId(userId);
        if (shopOpt.isEmpty()) {
            return Page.empty(pageable);
        }
        Long shopId = shopOpt.get().getShopId();

        // We need a repository method to find by shopId + filters.
        return voucherRepository.searchShopVouchers(shopId, keyword, type, status, pageable).map(this::mapToDTO);
    }

    @Override
    @Transactional
    public VoucherDTO createShopVoucher(Long userId, CreateVoucherRequest request) {
        Long shopId = getShopIdByOwner(userId);

        if (voucherRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }

        Voucher voucher = Voucher.builder()
                .shopId(shopId)
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType()) // Should be restricted?
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderValue(request.getMinOrderValue())
                .usageLimit(request.getUsageLimit())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(true)
                .build();

        return mapToDTO(voucherRepository.save(voucher));
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherDTO getShopVoucher(Long userId, Long id) {
        Long shopId = getShopIdByOwner(userId);
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        if (!shopId.equals(voucher.getShopId())) {
            throw new RuntimeException("Unauthorized");
        }
        return mapToDTO(voucher);
    }

    @Override
    @Transactional
    public VoucherDTO updateShopVoucher(Long userId, Long id, CreateVoucherRequest request) {
        Long shopId = getShopIdByOwner(userId);
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        if (!shopId.equals(voucher.getShopId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (!voucher.getCode().equals(request.getCode()) && voucherRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }

        voucher.setCode(request.getCode());
        voucher.setName(request.getName());
        voucher.setDescription(request.getDescription());
        voucher.setType(request.getType());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setMinOrderValue(request.getMinOrderValue());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());

        return mapToDTO(voucherRepository.save(voucher));
    }

    @Override
    @Transactional
    public void deleteShopVoucher(Long userId, Long id) {
        Long shopId = getShopIdByOwner(userId);
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        if (!shopId.equals(voucher.getShopId())) {
            throw new RuntimeException("Unauthorized");
        }
        voucherRepository.delete(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<VoucherDTO> getShopActiveVouchers(Long shopId) {
        return voucherRepository.findActiveVouchersByShopId(shopId, LocalDateTime.now())
                .stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> getPublicVouchers(Pageable pageable) {
        return getPublicVouchers(null, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> getPublicVouchers(String username, String type, Pageable pageable) {
        Page<Voucher> vouchers = voucherRepository.searchVouchers(null, type, "RUNNING", null, pageable);

        final java.util.Set<Long> userSavedVoucherIds = new java.util.HashSet<>();
        if (username != null) {
            userRepository.findByEmail(username).ifPresent(user -> {
                userSavedVoucherIds.addAll(userVoucherRepository.findVoucherIdsByUserId(user.getUserId()));
            });
        }

        return vouchers.map(v -> {
            VoucherDTO dto = mapToDTO(v);
            dto.setIsSaved(userSavedVoucherIds.contains(v.getId()));
            return dto;
        });
    }

    @Override
    @Transactional
    public void saveVoucher(String email, Long voucherId) {
        org.example.backend.model.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        if (!voucher.getIsActive() || voucher.getEndDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Voucher is not active or expired");
        }

        if (userVoucherRepository.existsByUserUserIdAndVoucherId(user.getUserId(), voucherId)) {
            throw new RuntimeException("Voucher already saved");
        }

        org.example.backend.model.entity.UserVoucher userVoucher = org.example.backend.model.entity.UserVoucher
                .builder()
                .user(user)
                .voucher(voucher)
                .isUsed(false)
                .build();

        userVoucherRepository.save(userVoucher);
    }

    private Long getShopIdByOwner(Long userId) {
        // Need to inject ShopRepository or UserRepo.
        // For simplicity, let's inject ShopRepository.
        // I will add the field at the top of class in another Edit step or just assume
        // it is there?
        // I should add it first.
        // Wait, I can't add field easily here in one go without replacing class header.
        // Hack: I will assume shopRepository is available as I will add it.
        return shopRepository.findByOwner_UserId(userId)
                .map(org.example.backend.model.entity.Shop::getShopId)
                .orElseThrow(() -> new RuntimeException("Shop not found for user"));
    }

    @Override
    public VoucherStatsDTO getVoucherStats() {
        long running = voucherRepository.countRunningVouchers();
        long totalUsage = voucherRepository.countTotalUsage();
        long expiringSoon = voucherRepository.countExpiringSoon(LocalDateTime.now().plusDays(7));

        // Mock budget used for now
        String budgetUsed = "0đ";

        return VoucherStatsDTO.builder()
                .runningVouchers(running)
                .totalUsage(totalUsage)
                .expiringSoon(expiringSoon)
                .budgetUsed(budgetUsed)
                .build();
    }

    private VoucherDTO mapToDTO(Voucher voucher) {
        String status = calculateStatus(voucher);
        String condition = formatCondition(voucher);
        String colorClass = getColorClass(status);

        return VoucherDTO.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .name(voucher.getName())
                .description(voucher.getDescription())
                .type(voucher.getType())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .minOrderValue(voucher.getMinOrderValue())
                .usageLimit(voucher.getUsageLimit())
                .usageCount(voucher.getUsageCount())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .isActive(voucher.getIsActive())
                .status(status)
                .conditionDescription(condition)
                .colorClass(colorClass)
                .build();
    }

    private String calculateStatus(Voucher v) {
        LocalDateTime now = LocalDateTime.now();
        if (!v.getIsActive())
            return "paused";
        if (now.isBefore(v.getStartDate()))
            return "upcoming";
        if (now.isAfter(v.getEndDate()))
            return "ended";
        return "running";
    }

    private String getColorClass(String status) {
        return switch (status) {
            case "running" -> "bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400"; // Or red/emerald
                                                                                                  // depending on type
            case "upcoming" -> "bg-amber-100 dark:bg-amber-900/30 text-amber-600 dark:text-amber-400";
            case "ended" -> "bg-gray-100 dark:bg-gray-900/30 text-gray-600 dark:text-gray-400";
            case "paused" -> "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600 dark:text-yellow-400";
            default -> "";
        };
    }

    private String formatCondition(Voucher v) {
        StringBuilder sb = new StringBuilder();
        if (v.getMinOrderValue() != null) {
            sb.append("Min: ").append(formatCurrency(v.getMinOrderValue()));
        }
        if (v.getType().equalsIgnoreCase("STOREFRONT")) {
            if (sb.length() > 0)
                sb.append(" • ");
            sb.append("Shop cụ thể");
        } else if (v.getType().equalsIgnoreCase("SHIPPING")) {
            if (sb.length() > 0)
                sb.append(" • ");
            sb.append("Vận chuyển");
        }
        return sb.toString();
    }

    private String formatCurrency(BigDecimal value) {
        // Simple formatter
        if (value == null)
            return "0đ";
        if (value.compareTo(new BigDecimal(1000)) >= 0) {
            return (value.longValue() / 1000) + "k";
        }
        return value.longValue() + "đ";
    }
}
