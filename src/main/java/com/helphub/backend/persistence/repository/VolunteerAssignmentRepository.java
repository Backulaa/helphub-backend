package com.helphub.backend.persistence.repository;

import com.helphub.backend.common.enums.VolunteerAssignmentStatus;
import com.helphub.backend.persistence.entity.SupportRequest;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.entity.VolunteerAssignment;
import com.helphub.backend.persistence.entity.VolunteerAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerAssignmentRepository extends JpaRepository<VolunteerAssignment, VolunteerAssignmentId> {

    boolean existsBySupportRequestAndVolunteer(SupportRequest supportRequest, User volunteer);

    List<VolunteerAssignment> findAllByVolunteerOrderByAssignedAtDesc(User volunteer);

    List<VolunteerAssignment> findAllBySupportRequestOrderByAssignedAtDesc(SupportRequest supportRequest);

    List<VolunteerAssignment> findAllBySupportRequestAndStatusOrderByAssignedAtDesc(SupportRequest supportRequest,
            VolunteerAssignmentStatus status);

    long countBySupportRequestAndStatus(SupportRequest supportRequest, VolunteerAssignmentStatus status);
}