package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.Post;
import com.helphub.backend.persistence.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostCommentRepository extends JpaRepository<PostComment, UUID> {

    List<PostComment> findAllByPostOrderByCreatedAtAsc(Post post);

    List<PostComment> findAllByPostAndParentCommentIsNullOrderByCreatedAtAsc(Post post);

    List<PostComment> findAllByParentCommentOrderByCreatedAtAsc(PostComment parentComment);
}