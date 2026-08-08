package com.andrew.lab1.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.andrew.lab1.entity.Complaint;
import com.andrew.lab1.entity.enums.ComplaintStatus;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Page<Complaint> findByStatus(
        ComplaintStatus status,
        Pageable pageable
    );

    Page<Complaint> findByVideo_AuthorId(
        Long authorId,
        Pageable pageable
    );

    Page<Complaint> findByRightsholderId(
        Long rightsholderId,
        Pageable pageable
    );

    void deleteByVideo_Id(Long id);
}
