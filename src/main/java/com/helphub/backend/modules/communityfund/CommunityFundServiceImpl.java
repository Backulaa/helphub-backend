package com.helphub.backend.modules.communityfund;

import com.helphub.backend.common.enums.CommunityFundMemberRole;
import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.communityfund.dto.request.AddCommunityFundMemberRequest;
import com.helphub.backend.modules.communityfund.dto.request.CreateCommunityFundRequest;
import com.helphub.backend.modules.communityfund.dto.request.UpdateCommunityFundMemberRoleRequest;
import com.helphub.backend.modules.communityfund.dto.request.UpdateCommunityFundRequest;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundDetailResponse;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundMemberResponse;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundSummaryResponse;
import com.helphub.backend.persistence.entity.CommunityFund;
import com.helphub.backend.persistence.entity.CommunityFundMember;
import com.helphub.backend.persistence.entity.CommunityFundMemberId;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.CommunityFundMemberRepository;
import com.helphub.backend.persistence.repository.CommunityFundRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityFundServiceImpl implements CommunityFundService {

    private final CommunityFundRepository communityFundRepository;
    private final CommunityFundMemberRepository communityFundMemberRepository;
    private final UserRepository userRepository;
    private final CommunityFundMapper communityFundMapper;

    @Override
    @Transactional
    public CommunityFundDetailResponse createCommunityFund(UUID creatorId, CreateCommunityFundRequest request) {
        User creator = getUserById(creatorId);
        validateCanCreateFund(creator);

        CommunityFund fund = CommunityFund.builder()
                .name(normalizeRequired(request.getName(), "Name is required"))
                .description(normalizeNullable(request.getDescription()))
                .totalBalance(BigDecimal.ZERO)
                .createdBy(creator)
                .isActive(true)
                .build();

        CommunityFund savedFund = communityFundRepository.save(Objects.requireNonNull(fund));

        CommunityFundMember manager = CommunityFundMember.builder()
                .id(new CommunityFundMemberId(savedFund.getId(), creator.getId()))
                .fund(savedFund)
                .user(creator)
                .role(CommunityFundMemberRole.MANAGER)
                .isActive(true)
                .build();

        communityFundMemberRepository.save(Objects.requireNonNull(manager));

        return communityFundMapper.toDetailResponse(savedFund);
    }

    @Override
    public List<CommunityFundSummaryResponse> getAllCommunityFunds(Boolean activeOnly) {
        List<CommunityFund> funds = Boolean.TRUE.equals(activeOnly)
                ? communityFundRepository.findAllByIsActiveTrueOrderByCreatedAtDesc()
                : communityFundRepository.findAllByOrderByCreatedAtDesc();

        return funds.stream()
                .map(communityFundMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public List<CommunityFundSummaryResponse> getMyCommunityFunds(UUID userId) {
        User user = getUserById(userId);

        return communityFundMemberRepository.findAllByUserAndIsActiveTrueOrderByJoinedAtDesc(user)
                .stream()
                .map(CommunityFundMember::getFund)
                .map(communityFundMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public CommunityFundDetailResponse getCommunityFundById(UUID fundId) {
        CommunityFund fund = getCommunityFundByIdOrThrow(fundId);
        return communityFundMapper.toDetailResponse(fund);
    }

    @Override
    @Transactional
    public CommunityFundDetailResponse updateCommunityFund(
            UUID currentUserId,
            UUID fundId,
            UpdateCommunityFundRequest request) {

        User currentUser = getUserById(currentUserId);
        CommunityFund fund = getCommunityFundByIdOrThrow(fundId);

        validateCanManageFund(currentUser, fund);

        fund.setName(normalizeRequired(request.getName(), "Name is required"));
        fund.setDescription(normalizeNullable(request.getDescription()));

        if (request.getIsActive() != null) {
            fund.setIsActive(request.getIsActive());
        }

        CommunityFund savedFund = communityFundRepository.save(fund);
        return communityFundMapper.toDetailResponse(savedFund);
    }

    @Override
    @Transactional
    public CommunityFundMemberResponse addMember(
            UUID currentUserId,
            UUID fundId,
            AddCommunityFundMemberRequest request) {

        User currentUser = getUserById(currentUserId);
        CommunityFund fund = getCommunityFundByIdOrThrow(fundId);

        validateCanManageFund(currentUser, fund);

        User targetUser = getUserById(request.getUserId());

        CommunityFundMemberId id = new CommunityFundMemberId(fund.getId(), targetUser.getId());

        CommunityFundMember member = communityFundMemberRepository.findById(id)
                .orElse(null);

        if (member != null && Boolean.TRUE.equals(member.getIsActive())) {
            throw new BadRequestException("User is already an active member of this fund");
        }

        if (member == null) {
            member = CommunityFundMember.builder()
                    .id(id)
                    .fund(fund)
                    .user(targetUser)
                    .role(Objects.requireNonNull(request.getRole()))
                    .isActive(true)
                    .build();
        } else {
            member.setRole(Objects.requireNonNull(request.getRole()));
            member.setIsActive(true);
        }

        CommunityFundMember savedMember = communityFundMemberRepository.save(Objects.requireNonNull(member));
        return communityFundMapper.toMemberResponse(savedMember);
    }

    @Override
    public List<CommunityFundMemberResponse> getMembers(UUID currentUserId, UUID fundId) {
        User currentUser = getUserById(currentUserId);
        CommunityFund fund = getCommunityFundByIdOrThrow(fundId);

        validateCanViewMembers(currentUser, fund);

        return communityFundMemberRepository.findAllByFundOrderByJoinedAtDesc(fund)
                .stream()
                .map(communityFundMapper::toMemberResponse)
                .toList();
    }

    @Override
    @Transactional
    public CommunityFundMemberResponse updateMemberRole(
            UUID currentUserId,
            UUID fundId,
            UUID userId,
            UpdateCommunityFundMemberRoleRequest request) {

        User currentUser = getUserById(currentUserId);
        CommunityFund fund = getCommunityFundByIdOrThrow(fundId);

        validateCanManageFund(currentUser, fund);

        CommunityFundMember member = getMemberByIdOrThrow(fundId, userId);

        if (!Boolean.TRUE.equals(member.getIsActive())) {
            throw new BadRequestException("Cannot update role of inactive member");
        }

        member.setRole(Objects.requireNonNull(request.getRole()));

        CommunityFundMember savedMember = communityFundMemberRepository.save(member);
        return communityFundMapper.toMemberResponse(savedMember);
    }

    @Override
    @Transactional
    public void removeMember(UUID currentUserId, UUID fundId, UUID userId) {
        User currentUser = getUserById(currentUserId);
        CommunityFund fund = getCommunityFundByIdOrThrow(fundId);

        validateCanManageFund(currentUser, fund);

        CommunityFundMember member = getMemberByIdOrThrow(fundId, userId);

        if (!Boolean.TRUE.equals(member.getIsActive())) {
            throw new BadRequestException("Member is already inactive");
        }

        if (member.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You cannot remove yourself from fund managers");
        }

        member.setIsActive(false);
        communityFundMemberRepository.save(member);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private CommunityFund getCommunityFundByIdOrThrow(UUID fundId) {
        return communityFundRepository.findById(Objects.requireNonNull(fundId))
                .orElseThrow(() -> new ResourceNotFoundException("Community fund not found with id: " + fundId));
    }

    private CommunityFundMember getMemberByIdOrThrow(UUID fundId, UUID userId) {
        CommunityFundMemberId id = new CommunityFundMemberId(fundId, userId);

        return communityFundMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community fund member not found"));
    }

    private void validateCanCreateFund(User user) {
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.COLLABORATOR) {
            throw new ForbiddenException("Only admin or collaborator can create community fund");
        }
    }

    private void validateCanManageFund(User user, CommunityFund fund) {
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        boolean isManager = communityFundMemberRepository.existsByFundAndUserAndRoleAndIsActiveTrue(
                fund,
                user,
                CommunityFundMemberRole.MANAGER);

        if (!isManager) {
            throw new ForbiddenException("Only admin or fund manager can manage this community fund");
        }
    }

    private void validateCanViewMembers(User user, CommunityFund fund) {
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        boolean isMember = communityFundMemberRepository.existsByFundAndUserAndIsActiveTrue(fund, user);

        if (!isMember) {
            throw new ForbiddenException("Only admin or fund member can view members");
        }
    }

    private String normalizeRequired(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BadRequestException(message);
        }

        return value.trim();
    }

    private String normalizeNullable(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }
}