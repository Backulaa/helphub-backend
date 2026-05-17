package com.helphub.backend.modules.supportneed;

import com.helphub.backend.modules.supportneed.dto.request.CreateSupportNeedContributionRequest;
import com.helphub.backend.modules.supportneed.dto.request.CreateSupportNeedRequest;
import com.helphub.backend.modules.supportneed.dto.request.UpdateSupportNeedRequest;
import com.helphub.backend.modules.supportneed.dto.response.SupportNeedContributionResponse;
import com.helphub.backend.modules.supportneed.dto.response.SupportNeedResponse;

import java.util.List;
import java.util.UUID;

public interface SupportNeedService {

    SupportNeedResponse createSupportNeed(UUID requesterId, UUID supportRequestId, CreateSupportNeedRequest request);

    List<SupportNeedResponse> getSupportNeedsBySupportRequest(UUID supportRequestId);

    SupportNeedResponse updateSupportNeed(UUID requesterId, UUID supportNeedId, UpdateSupportNeedRequest request);

    void deleteSupportNeed(UUID requesterId, UUID supportNeedId);

    SupportNeedContributionResponse contributeToSupportNeed(
            UUID contributorId,
            UUID supportNeedId,
            CreateSupportNeedContributionRequest request);

    List<SupportNeedContributionResponse> getContributionsBySupportNeed(UUID supportNeedId);
}