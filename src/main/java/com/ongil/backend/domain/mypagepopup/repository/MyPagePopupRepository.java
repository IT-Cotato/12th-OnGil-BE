package com.ongil.backend.domain.mypagepopup.repository;

import com.ongil.backend.domain.mypagepopup.entity.MyPagePopup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MyPagePopupRepository extends JpaRepository<MyPagePopup, Long> {

    @Query("SELECT p FROM MyPagePopup p WHERE p.isActive = true " +
            "AND (p.startDate IS NULL OR p.startDate <= :now) " +
            "AND (p.endDate IS NULL OR p.endDate >= :now) " +
            "AND (p.targetUserType = 'ALL' OR p.targetUserType = :userType) " +
            "ORDER BY p.priority DESC, p.createdAt DESC")
    List<MyPagePopup> findActivePopups(@Param("now") LocalDateTime now, @Param("userType") String userType);
}
