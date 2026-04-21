package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.Media;
import com.helphub.backend.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> {

    List<Media> findAllByUploadedByOrderByCreatedAtDesc(User uploadedBy);
}