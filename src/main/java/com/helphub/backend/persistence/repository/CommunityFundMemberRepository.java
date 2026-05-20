package com.helphub.backend.persistence.repository;

import com.helphub.backend.common.enums.CommunityFundMemberRole;
import com.helphub.backend.persistence.entity.CommunityFund;
import com.helphub.backend.persistence.entity.CommunityFundMember;
import com.helphub.backend.persistence.entity.CommunityFundMemberId;
import com.helphub.backend.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityFundMemberRepository extends JpaRepository<CommunityFundMember, CommunityFundMemberId> {

    List<CommunityFundMember> findAllByFundOrderByJoinedAtDesc(CommunityFund fund);

    List<CommunityFundMember> findAllByUserAndIsActiveTrueOrderByJoinedAtDesc(User user);

    boolean existsByFundAndUserAndIsActiveTrue(CommunityFund fund, User user);

    boolean existsByFundAndUserAndRoleAndIsActiveTrue(
            CommunityFund fund,
            User user,
            CommunityFundMemberRole role);
}