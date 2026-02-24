package org.example.backend.service;

import org.example.backend.dto.AdminUserDTO;
import org.example.backend.model.entity.User;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    public Page<AdminUserDTO> getUsers(String search, String roleStr, String statusStr, Pageable pageable) {
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Start with excluding ADMIN role
            predicates.add(criteriaBuilder.notEqual(root.get("role"), User.Role.ADMIN));

            if (search != null && !search.isEmpty()) {
                String searchLower = "%" + search.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")),
                        searchLower);
                Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchLower);
                predicates.add(criteriaBuilder.or(namePredicate, emailPredicate));
            }

            if (roleStr != null && !roleStr.isEmpty() && !roleStr.equalsIgnoreCase("ALL")) {
                predicates.add(criteriaBuilder.equal(root.get("role"), User.Role.valueOf(roleStr)));
            }

            if (statusStr != null && !statusStr.isEmpty() && !statusStr.equalsIgnoreCase("ALL")) {
                predicates.add(criteriaBuilder.equal(root.get("status"), User.Status.valueOf(statusStr)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    public void updateUserStatus(Long userId, String statusStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("Cannot update status of an ADMIN user");
        }

        user.setStatus(User.Status.valueOf(statusStr));
        userRepository.save(user);
    }

    @Autowired
    private org.example.backend.repository.ReportRepository reportRepository;

    public org.example.backend.dto.StatsDTO getUserStats() {
        org.example.backend.dto.StatsDTO stats = new org.example.backend.dto.StatsDTO();

        Specification<User> notAdmin = (root, query, cb) -> cb.notEqual(root.get("role"), User.Role.ADMIN);

        // Total (excluding admins)
        stats.setTotal(userRepository.count(notAdmin));

        // Active
        stats.setActive(userRepository
                .count(notAdmin.and((root, query, cb) -> cb.equal(root.get("status"), User.Status.ACTIVE))));

        // Start 'Pending' as 'LOCKED' for reuse of DTO field
        stats.setPending(userRepository
                .count(notAdmin.and((root, query, cb) -> cb.equal(root.get("status"), User.Status.LOCKED))));

        // Banned
        stats.setBanned(userRepository
                .count(notAdmin.and((root, query, cb) -> cb.equal(root.get("status"), User.Status.BANNED))));

        // Reported Users
        stats.setReported(reportRepository.countByTargetTypeAndStatus(
                org.example.backend.model.entity.Report.TargetType.USER,
                org.example.backend.model.entity.Report.ReportStatus.PENDING));

        return stats;
    }

    private AdminUserDTO convertToDTO(User user) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole().name());
        dto.setStatus(user.getStatus().name());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }
}
