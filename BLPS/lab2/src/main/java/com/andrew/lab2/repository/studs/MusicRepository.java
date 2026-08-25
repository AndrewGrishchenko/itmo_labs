package com.andrew.lab2.repository.studs;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.andrew.lab2.entity.studs.Music;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    Page<Music> findAll(Pageable pageable);
    
    Optional<Music> findByName(String name);

    @Query("select m.name from Music m where m.rightsholderId = :id")
    List<String> findNamesByRightsholderId(@Param("id") Long id);
}
