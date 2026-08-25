package com.andrew.lab2.repository.studs2;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.andrew.lab2.entity.studs2.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findAll(Pageable pageable);
    Optional<Video> findById(Long id);
    Video getById(Long id);
    Optional<Video> findByTitle(String title);
}
