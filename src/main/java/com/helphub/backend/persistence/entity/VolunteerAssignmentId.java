package com.helphub.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VolunteerAssignmentId implements Serializable {

    @Column(name = "support_request_id")
    private UUID supportRequestId;

    @Column(name = "volunteer_id")
    private UUID volunteerId;
}