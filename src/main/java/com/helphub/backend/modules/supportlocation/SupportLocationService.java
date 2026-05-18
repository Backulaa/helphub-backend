package com.helphub.backend.modules.supportlocation;

import com.helphub.backend.modules.supportlocation.dto.request.CreateSupportLocationRequest;
import com.helphub.backend.modules.supportlocation.dto.request.UpdateSupportLocationRequest;
import com.helphub.backend.modules.supportlocation.dto.request.UpdateSupportLocationStatusRequest;
import com.helphub.backend.modules.supportlocation.dto.response.SupportLocationDetailResponse;
import com.helphub.backend.modules.supportlocation.dto.response.SupportLocationSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface SupportLocationService {

    SupportLocationDetailResponse createSupportLocation(UUID creatorId, CreateSupportLocationRequest request);

    List<SupportLocationSummaryResponse> getAllSupportLocations(Boolean activeOnly);

    List<SupportLocationSummaryResponse> getMyCreatedSupportLocations(UUID creatorId);

    SupportLocationDetailResponse getSupportLocationById(UUID id);

    SupportLocationDetailResponse updateSupportLocation(
            UUID currentUserId,
            UUID supportLocationId,
            UpdateSupportLocationRequest request);

    SupportLocationDetailResponse updateSupportLocationStatus(
            UUID currentUserId,
            UUID supportLocationId,
            UpdateSupportLocationStatusRequest request);
}