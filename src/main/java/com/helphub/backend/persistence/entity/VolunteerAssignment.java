package com.helphub.backend.persistence.entity;

import com.helphub.backend.common.enums.VolunteerAssignmentStatus;
import com.helphub.backend.common.util.DateTimeUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "volunteer_assignments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerAssignment {

    @EmbeddedId
    private VolunteerAssignmentId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("supportRequestId")
    @JoinColumn(name = "support_request_id", nullable = false)
    private SupportRequest supportRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("volunteerId")
    @JoinColumn(name = "volunteer_id", nullable = false)
    private User volunteer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VolunteerAssignmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "rejection_reason", length = 200)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = VolunteerAssignmentStatus.ACCEPTED;
        }

        if (this.assignedAt == null) {
            this.assignedAt = DateTimeUtils.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = DateTimeUtils.now();
    }
}