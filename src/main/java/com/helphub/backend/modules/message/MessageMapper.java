package com.helphub.backend.modules.message;

import com.helphub.backend.modules.message.dto.response.MessageMediaResponse;
import com.helphub.backend.modules.message.dto.response.MessageResponse;
import com.helphub.backend.persistence.entity.Media;
import com.helphub.backend.persistence.entity.Message;
import com.helphub.backend.persistence.entity.MessageMedia;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageMapper {

    public MessageResponse toResponse(Message message, List<MessageMedia> messageMediaList) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFullName())
                .senderAvatarUrl(message.getSender().getAvatarUrl())
                .content(message.getContent())
                .media(toMediaResponses(messageMediaList))
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .editedAt(message.getEditedAt())
                .build();
    }

    private List<MessageMediaResponse> toMediaResponses(List<MessageMedia> messageMediaList) {
        return messageMediaList.stream()
                .map(messageMedia -> toMediaResponse(messageMedia.getMedia()))
                .toList();
    }

    private MessageMediaResponse toMediaResponse(Media media) {
        return MessageMediaResponse.builder()
                .id(media.getId())
                .fileName(media.getFileName())
                .fileUrl(media.getFileUrl())
                .fileType(media.getFileType().name())
                .mimeType(media.getMimeType())
                .fileSize(media.getFileSize())
                .altText(media.getAltText())
                .build();
    }
}