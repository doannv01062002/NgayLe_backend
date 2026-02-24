package org.example.backend.service;

import org.example.backend.model.entity.AffiliatePartner;
import org.example.backend.model.entity.User;
import org.example.backend.repository.AffiliatePartnerRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class AffiliateService {

    @Autowired
    private AffiliatePartnerRepository affiliatePartnerRepository;

    @Autowired
    private UserRepository userRepository;

    public void register(String email, String source) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (affiliatePartnerRepository.findByUserUserId(user.getUserId()).isPresent()) {
            throw new IllegalArgumentException("User is already an affiliate partner or pending approval.");
        }

        AffiliatePartner partner = AffiliatePartner.builder()
                .user(user)
                .source(source)
                .status(AffiliatePartner.PartnerStatus.PENDING)
                .totalOrders(0)
                .totalRevenue(BigDecimal.ZERO)
                .totalCommission(BigDecimal.ZERO)
                .build();

        affiliatePartnerRepository.save(partner);
    }

    public String getStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return affiliatePartnerRepository.findByUserUserId(user.getUserId())
                .map(p -> p.getStatus().name())
                .orElse("NOT_REGISTERED");
    }
}
