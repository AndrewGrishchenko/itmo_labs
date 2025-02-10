package com.andrew.lab4.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.andrew.lab4.model.Point;
import com.andrew.lab4.model.User;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Point p WHERE p.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
