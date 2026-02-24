package org.example.backend.service;

import org.example.backend.dto.AffiliatePartnerDTO;
import org.example.backend.model.entity.AffiliatePartner;
import org.example.backend.repository.AffiliatePartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminAffiliateService {

    @Autowired
    private org.example.backend.repository.UserRepository userRepository;

    @Autowired
    private org.example.backend.repository.CommissionHistoryRepository commissionHistoryRepository;

    @Autowired
    private AffiliatePartnerRepository affiliatePartnerRepository;

    // ... existing getPartners ...

    public org.example.backend.dto.AffiliatePartnerDTO createPartner(String email, String source) {
        org.example.backend.model.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (affiliatePartnerRepository.findByUserUserId(user.getUserId()).isPresent()) {
            throw new IllegalStateException("User is already an affiliate partner");
        }

        AffiliatePartner partner = new AffiliatePartner();
        partner.setUser(user);
        partner.setSource(source);
        partner.setStatus(AffiliatePartner.PartnerStatus.ACTIVE);
        partner.setTotalOrders(0);
        partner.setTotalRevenue(java.math.BigDecimal.ZERO);
        partner.setTotalCommission(java.math.BigDecimal.ZERO);

        partner = affiliatePartnerRepository.save(partner);
        return convertToDTO(partner);
    }

    public Page<org.example.backend.dto.CommissionHistoryDTO> getCommissionHistory(Pageable pageable) {
        if (pageable == null)
            throw new IllegalArgumentException("Pageable cannot be null");
        return commissionHistoryRepository.findAll(pageable).map(this::convertToHistoryDTO);
    }

    private org.example.backend.dto.CommissionHistoryDTO convertToHistoryDTO(
            org.example.backend.model.entity.CommissionHistory history) {
        return org.example.backend.dto.CommissionHistoryDTO.builder()
                .id(history.getId())
                .partnerId(history.getPartner().getId())
                .partnerName(history.getPartner().getUser().getFullName())
                .amount(history.getAmount())
                .type(history.getType().name())
                .description(history.getDescription())
                .createdAt(history.getCreatedAt())
                .build();
    }

    public Page<AffiliatePartnerDTO> getPartners(String status, String search, Pageable pageable) {
        if (pageable == null)
            throw new IllegalArgumentException("Pageable cannot be null");

        AffiliatePartner.PartnerStatus partnerStatus = null;
        if (status != null && !status.equals("ALL")) {
            partnerStatus = AffiliatePartner.PartnerStatus.valueOf(status);
        }

        return affiliatePartnerRepository.searchPartners(partnerStatus, search, pageable)
                .map(this::convertToDTO);
    }

    private AffiliatePartnerDTO convertToDTO(AffiliatePartner partner) {
        return AffiliatePartnerDTO.builder()
                .id(partner.getId())
                .userId(partner.getUser().getUserId())
                .name(partner.getUser().getFullName())
                .email(partner.getUser().getEmail())
                .avatar(partner.getUser().getUserProfile() != null ? partner.getUser().getUserProfile().getAvatarUrl()
                        : null)
                .source(partner.getSource())
                .orders(partner.getTotalOrders())
                .revenue(partner.getTotalRevenue())
                .commission(partner.getTotalCommission())
                .status(partner.getStatus().name())
                .createdAt(partner.getCreatedAt())
                .build();
    }

    // Add other methods (approve, reject, etc.) as needed
    public void approvePartner(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null");
        AffiliatePartner partner = affiliatePartnerRepository.findById(id).orElseThrow();
        partner.setStatus(AffiliatePartner.PartnerStatus.ACTIVE);
        affiliatePartnerRepository.save(partner);
    }

    public void rejectPartner(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null");
        AffiliatePartner partner = affiliatePartnerRepository.findById(id).orElseThrow();
        partner.setStatus(AffiliatePartner.PartnerStatus.BANNED);
        affiliatePartnerRepository.save(partner);
    }

    public org.example.backend.dto.AffiliateOverviewDTO getOverview() {
        try {
            long totalPartners = affiliatePartnerRepository.count();
            long pendingPartners = affiliatePartnerRepository.countByStatus(AffiliatePartner.PartnerStatus.PENDING);
            java.math.BigDecimal totalRevenue = affiliatePartnerRepository.sumTotalRevenue();
            java.math.BigDecimal totalCommission = affiliatePartnerRepository.sumTotalCommission();

            return org.example.backend.dto.AffiliateOverviewDTO.builder()
                    .totalPartners(totalPartners)
                    .pendingPartners(pendingPartners)
                    .totalAffiliateRevenue(totalRevenue != null ? totalRevenue : java.math.BigDecimal.ZERO)
                    .pendingCommission(totalCommission != null ? totalCommission : java.math.BigDecimal.ZERO)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return org.example.backend.dto.AffiliateOverviewDTO.builder()
                    .totalPartners(0)
                    .pendingPartners(0)
                    .totalAffiliateRevenue(java.math.BigDecimal.ZERO)
                    .pendingCommission(java.math.BigDecimal.ZERO)
                    .build();
        }
    }
}
