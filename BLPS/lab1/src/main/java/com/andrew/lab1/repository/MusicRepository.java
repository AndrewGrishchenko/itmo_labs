package com.andrew.lab1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.andrew.lab1.entity.Music;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    Page<Music> findAll(Pageable pageable);
    
    Optional<Music> findByName(String name);

    @Query("select m.name from Music m where m.rightsholder.id = :id")
    List<String> findNamesByRightsholderId(@Param("id") Long id);
}
