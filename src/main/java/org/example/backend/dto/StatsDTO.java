package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsDTO {
    private long total;
    private long pending; // For products/shops: pending approval. For users: could be unverified?
    private long active;
    private long banned; // or suspended/rejected
    // For users, maybe 'new today' is useful, but let's stick to the visual
    // structure for now
    // The visual shows: Total, Pending, Reported, Removed/Banned.

    private long reported; // For products (Vi phạm báo cáo)
}
