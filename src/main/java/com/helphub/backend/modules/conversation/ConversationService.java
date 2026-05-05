package com.helphub.backend.modules.conversation;

import com.helphub.backend.modules.conversation.dto.request.AddConversationMemberRequest;
import com.helphub.backend.modules.conversation.dto.request.CreateGroupConversationRequest;
import com.helphub.backend.modules.conversation.dto.request.CreatePrivateConversationRequest;
import com.helphub.backend.modules.conversation.dto.response.ConversationDetailResponse;
import com.helphub.backend.modules.conversation.dto.response.ConversationSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    ConversationDetailResponse createPrivateConversation(CreatePrivateConversationRequest request);

    ConversationDetailResponse createGroupConversation(CreateGroupConversationRequest request);

    List<ConversationSummaryResponse> getMyConversations();

    ConversationDetailResponse getConversationById(UUID conversationId);

    ConversationDetailResponse addMember(UUID conversationId, AddConversationMemberRequest request);

    void leaveConversation(UUID conversationId);
}