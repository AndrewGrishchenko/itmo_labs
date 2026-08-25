package com.andrew.lab2.repository.studs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.andrew.lab2.entity.enums.ComplaintStatus;
import com.andrew.lab2.entity.studs.Complaint;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Page<Complaint> findByStatus(
        ComplaintStatus status,
        Pageable pageable
    );

    Page<Complaint> findByVideoAuthorId(
        Long videoAuthorId,
        Pageable pageable
    );

    Page<Complaint> findByRightsholderId(
        Long rightsholderId,
        Pageable pageable
    );

    @Modifying
    @Query("DELETE FROM Complaint c WHERE c.videoId = :videoId")
    void deleteByVideoId(@Param("videoId") Long videoId);
}
