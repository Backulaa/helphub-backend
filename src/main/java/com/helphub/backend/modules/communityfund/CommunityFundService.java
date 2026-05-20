package com.helphub.backend.modules.communityfund;

import com.helphub.backend.modules.communityfund.dto.request.AddCommunityFundMemberRequest;
import com.helphub.backend.modules.communityfund.dto.request.CreateCommunityFundRequest;
import com.helphub.backend.modules.communityfund.dto.request.UpdateCommunityFundMemberRoleRequest;
import com.helphub.backend.modules.communityfund.dto.request.UpdateCommunityFundRequest;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundDetailResponse;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundMemberResponse;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface CommunityFundService {

    CommunityFundDetailResponse createCommunityFund(UUID creatorId, CreateCommunityFundRequest request);

    List<CommunityFundSummaryResponse> getAllCommunityFunds(Boolean activeOnly);

    List<CommunityFundSummaryResponse> getMyCommunityFunds(UUID userId);

    CommunityFundDetailResponse getCommunityFundById(UUID fundId);

    CommunityFundDetailResponse updateCommunityFund(
            UUID currentUserId,
            UUID fundId,
            UpdateCommunityFundRequest request);

    CommunityFundMemberResponse addMember(
            UUID currentUserId,
            UUID fundId,
            AddCommunityFundMemberRequest request);

    List<CommunityFundMemberResponse> getMembers(UUID currentUserId, UUID fundId);

    CommunityFundMemberResponse updateMemberRole(
            UUID currentUserId,
            UUID fundId,
            UUID userId,
            UpdateCommunityFundMemberRoleRequest request);

    void removeMember(UUID currentUserId, UUID fundId, UUID userId);
}