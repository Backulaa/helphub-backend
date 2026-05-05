package com.helphub.backend.modules.conversation;

import com.helphub.backend.modules.conversation.dto.response.ConversationDetailResponse;
import com.helphub.backend.modules.conversation.dto.response.ConversationMemberResponse;
import com.helphub.backend.modules.conversation.dto.response.ConversationSummaryResponse;
import com.helphub.backend.persistence.entity.Conversation;
import com.helphub.backend.persistence.entity.ConversationMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConversationMapper {

    public ConversationSummaryResponse toSummaryResponse(Conversation conversation) {
        return ConversationSummaryResponse.builder()
                .id(conversation.getId())
                .type(conversation.getType())
                .createdBy(conversation.getCreatedBy().getId())
                .members(toMemberResponses(conversation.getMembers()))
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    public ConversationDetailResponse toDetailResponse(Conversation conversation) {
        return ConversationDetailResponse.builder()
                .id(conversation.getId())
                .type(conversation.getType())
                .createdBy(conversation.getCreatedBy().getId())
                .members(toMemberResponses(conversation.getMembers()))
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    public ConversationMemberResponse toMemberResponse(ConversationMember member) {
        return ConversationMemberResponse.builder()
                .userId(member.getUser().getId())
                .fullName(member.getUser().getFullName())
                .email(member.getUser().getEmail())
                .avatarUrl(member.getUser().getAvatarUrl())
                .joinedAt(member.getJoinedAt())
                .build();
    }

    private List<ConversationMemberResponse> toMemberResponses(List<ConversationMember> members) {
        return members.stream()
                .map(this::toMemberResponse)
                .toList();
    }
}