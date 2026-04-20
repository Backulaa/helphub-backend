package com.helphub.backend.persistence.repository;

import com.helphub.backend.common.enums.PostStatus;
import com.helphub.backend.persistence.entity.Post;
import com.helphub.backend.persistence.entity.SupportRequest;
import com.helphub.backend.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findAllByAuthorAndIsActiveTrueOrderByCreatedAtDesc(User author);

    List<Post> findAllByIsActiveTrueAndStatusOrderByCreatedAtDesc(PostStatus status);

    List<Post> findAllBySupportRequestOrderByCreatedAtDesc(SupportRequest supportRequest);
}