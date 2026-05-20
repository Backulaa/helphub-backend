package com.helphub.backend.modules.communityfund;

import com.helphub.backend.modules.communityfund.dto.response.CommunityFundDetailResponse;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundMemberResponse;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundSummaryResponse;
import com.helphub.backend.persistence.entity.CommunityFund;
import com.helphub.backend.persistence.entity.CommunityFundMember;
import com.helphub.backend.persistence.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommunityFundMapper {

    public CommunityFundSummaryResponse toSummaryResponse(CommunityFund fund) {
        User creator = fund.getCreatedBy();

        return CommunityFundSummaryResponse.builder()
                .id(fund.getId())
                .name(fund.getName())
                .totalBalance(fund.getTotalBalance())
                .isActive(fund.getIsActive())
                .createdBy(creator.getId())
                .createdByName(creator.getFullName())
                .createdAt(fund.getCreatedAt())
                .build();
    }

    public CommunityFundDetailResponse toDetailResponse(CommunityFund fund) {
        User creator = fund.getCreatedBy();

        return CommunityFundDetailResponse.builder()
                .id(fund.getId())
                .name(fund.getName())
                .description(fund.getDescription())
                .totalBalance(fund.getTotalBalance())
                .isActive(fund.getIsActive())
                .createdBy(creator.getId())
                .createdByName(creator.getFullName())
                .createdAt(fund.getCreatedAt())
                .updatedAt(fund.getUpdatedAt())
                .build();
    }

    public CommunityFundMemberResponse toMemberResponse(CommunityFundMember member) {
        User user = member.getUser();

        return CommunityFundMemberResponse.builder()
                .fundId(member.getFund().getId())
                .userId(user.getId())
                .userName(user.getFullName())
                .userEmail(user.getEmail())
                .role(member.getRole())
                .isActive(member.getIsActive())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}