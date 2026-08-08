package com.andrew.lab1.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.andrew.lab1.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findAll(Pageable pageable);
    Optional<Video> findById(Long id);
    Video getById(Long id);
    Optional<Video> findByTitle(String title);
}
