package com.helphub.backend.modules.conversation;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.conversation.dto.request.AddConversationMemberRequest;
import com.helphub.backend.modules.conversation.dto.request.CreateGroupConversationRequest;
import com.helphub.backend.modules.conversation.dto.request.CreatePrivateConversationRequest;
import com.helphub.backend.modules.conversation.dto.response.ConversationDetailResponse;
import com.helphub.backend.modules.conversation.dto.response.ConversationSummaryResponse;
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
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
@Validated
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping("/private")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> createPrivateConversation(
            @Valid @RequestBody CreatePrivateConversationRequest request) {

        ConversationDetailResponse response = conversationService.createPrivateConversation(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ConversationDetailResponse>builder()
                        .success(true)
                        .message("Private conversation created successfully")
                        .data(response)
                        .build());
    }

    @PostMapping("/group")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> createGroupConversation(
            @Valid @RequestBody CreateGroupConversationRequest request) {

        ConversationDetailResponse response = conversationService.createGroupConversation(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ConversationDetailResponse>builder()
                        .success(true)
                        .message("Group conversation created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ConversationSummaryResponse>>> getMyConversations() {
        List<ConversationSummaryResponse> response = conversationService.getMyConversations();

        return ResponseEntity.ok(ApiResponse.<List<ConversationSummaryResponse>>builder()
                .success(true)
                .message("Conversations fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> getConversationById(
            @PathVariable @NotNull UUID conversationId) {

        ConversationDetailResponse response = conversationService.getConversationById(conversationId);

        return ResponseEntity.ok(ApiResponse.<ConversationDetailResponse>builder()
                .success(true)
                .message("Conversation fetched successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{conversationId}/members")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> addMember(
            @PathVariable @NotNull UUID conversationId,
            @Valid @RequestBody AddConversationMemberRequest request) {

        ConversationDetailResponse response = conversationService.addMember(conversationId, request);

        return ResponseEntity.ok(ApiResponse.<ConversationDetailResponse>builder()
                .success(true)
                .message("Member added successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{conversationId}/members/me")
    public ResponseEntity<ApiResponse<Object>> leaveConversation(
            @PathVariable @NotNull UUID conversationId) {

        conversationService.leaveConversation(conversationId);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Left conversation successfully")
                .data(null)
                .build());
    }
}