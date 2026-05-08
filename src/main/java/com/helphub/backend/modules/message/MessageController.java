package com.helphub.backend.modules.message;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.message.dto.request.SendMessageRequest;
import com.helphub.backend.modules.message.dto.request.UpdateMessageRequest;
import com.helphub.backend.modules.message.dto.response.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable @NotNull UUID conversationId,
            @Valid @RequestBody SendMessageRequest request) {

        MessageResponse response = messageService.sendMessage(conversationId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MessageResponse>builder()
                        .success(true)
                        .message("Message sent successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            @PathVariable @NotNull UUID conversationId) {

        List<MessageResponse> response = messageService.getMessages(conversationId);

        return ResponseEntity.ok(ApiResponse.<List<MessageResponse>>builder()
                .success(true)
                .message("Messages fetched successfully")
                .data(response)
                .build());
    }

    @PutMapping("/messages/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessage(
            @PathVariable @NotNull UUID messageId,
            @Valid @RequestBody UpdateMessageRequest request) {

        MessageResponse response = messageService.updateMessage(messageId, request);

        return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                .success(true)
                .message("Message updated successfully")
                .data(response)
                .build());
    }

    @PatchMapping("/conversations/{conversationId}/messages/{messageId}/read")
    public ResponseEntity<ApiResponse<Object>> markAsRead(
            @PathVariable @NotNull UUID conversationId,
            @PathVariable @NotNull UUID messageId) {

        messageService.markAsRead(conversationId, messageId);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Conversation marked as read successfully")
                .data(null)
                .build());
    }
}