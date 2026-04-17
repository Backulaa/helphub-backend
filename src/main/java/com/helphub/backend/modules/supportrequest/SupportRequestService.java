package com.helphub.backend.modules.supportrequest;

import com.helphub.backend.common.enums.SupportRequestStatus;
import com.helphub.backend.modules.supportrequest.dto.request.AssignSupportRequestToSupportLocationRequest;
import com.helphub.backend.modules.supportrequest.dto.request.CreateSupportRequestRequest;
import com.helphub.backend.modules.supportrequest.dto.request.RejectSupportRequestRequest;
import com.helphub.backend.modules.supportrequest.dto.request.UpdateSupportRequestRequest;
import com.helphub.backend.modules.supportrequest.dto.response.SupportRequestDetailResponse;
import com.helphub.backend.modules.supportrequest.dto.response.SupportRequestSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface SupportRequestService {

    SupportRequestDetailResponse createSupportRequest(UUID requesterId, CreateSupportRequestRequest request);

    List<SupportRequestSummaryResponse> getAllSupportRequests(SupportRequestStatus status);

    List<SupportRequestSummaryResponse> getMySupportRequests(UUID requesterId);

    SupportRequestDetailResponse getSupportRequestById(UUID id);

    SupportRequestDetailResponse updateMySupportRequest(UUID requesterId, UUID supportRequestId,
            UpdateSupportRequestRequest request);

    SupportRequestDetailResponse approveSupportRequest(UUID reviewerId, UUID supportRequestId);

    SupportRequestDetailResponse rejectSupportRequest(UUID reviewerId, UUID supportRequestId,
            RejectSupportRequestRequest request);

    SupportRequestDetailResponse assignSupportRequestToSupportLocation(
            UUID reviewerId,
            UUID supportRequestId,
            AssignSupportRequestToSupportLocationRequest request);
}