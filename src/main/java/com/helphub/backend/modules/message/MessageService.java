package com.helphub.backend.modules.message;

import com.helphub.backend.modules.message.dto.request.SendMessageRequest;
import com.helphub.backend.modules.message.dto.request.UpdateMessageRequest;
import com.helphub.backend.modules.message.dto.response.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponse sendMessage(UUID conversationId, SendMessageRequest request);

    List<MessageResponse> getMessages(UUID conversationId);

    MessageResponse updateMessage(UUID messageId, UpdateMessageRequest request);

    void markAsRead(UUID conversationId, UUID messageId);
}