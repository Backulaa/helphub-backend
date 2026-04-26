package com.helphub.backend.persistence.repository;

import com.helphub.backend.common.enums.PostReactionType;
import com.helphub.backend.persistence.entity.Post;
import com.helphub.backend.persistence.entity.PostReaction;
import com.helphub.backend.persistence.entity.PostReactionId;
import com.helphub.backend.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, PostReactionId> {

    Optional<PostReaction> findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);

    long countByPost(Post post);

    long countByPostAndType(Post post, PostReactionType type);

    List<PostReaction> findAllByPost(Post post);

    void deleteByPostAndUser(Post post, User user);
}