package com.helphub.backend.persistence.entity;

import com.helphub.backend.common.enums.SupportNeedUnit;
import com.helphub.backend.common.enums.SupportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "support_needs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportNeed extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "support_request_id", nullable = false)
    private SupportRequest supportRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_location_id")
    private SupportLocation supportLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "support_type", nullable = false, length = 20)
    private SupportType supportType;

    @Column(name = "need_name", nullable = false, length = 100)
    private String needName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SupportNeedUnit unit;

    @Column(name = "required_quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal requiredQuantity;

    @Builder.Default
    @Column(name = "received_quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal receivedQuantity = BigDecimal.ZERO;

    @PrePersist
    public void prePersistSupportNeed() {
        if (this.receivedQuantity == null) {
            this.receivedQuantity = BigDecimal.ZERO;
        }
    }
}