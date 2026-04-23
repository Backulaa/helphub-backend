package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.Media;
import com.helphub.backend.persistence.entity.Post;
import com.helphub.backend.persistence.entity.PostMedia;
import com.helphub.backend.persistence.entity.PostMediaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostMediaRepository extends JpaRepository<PostMedia, PostMediaId> {

    List<PostMedia> findAllByPostOrderByDisplayOrderAscCreatedAtAsc(Post post);

    boolean existsByPostAndMedia(Post post, Media media);

    @Modifying
    @Transactional
    void deleteByPostAndMedia(Post post, Media media);
}