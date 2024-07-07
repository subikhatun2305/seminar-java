package com.dreamsol.repositories;

import com.dreamsol.entites.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

        @Query("SELECT v FROM Visitor v  WHERE " +
                        "(:userId IS NULL OR v.user.id = :userId) AND " +
                        "(:purposeId IS NULL OR v.purpose.id = :purposeId) AND " +
                        "(:departmentId IS NULL OR v.department.id = :departmentId) AND " +
                        "(:unitId IS NULL OR v.unitId = :unitId) AND " +
                        "(:status IS NULL OR v.status = :status) AND " +
                        "(:fromDate IS NULL OR :toDate IS NULL OR (v.createdAt >= :fromDate AND v.createdAt <= :toDate))")
        Page<Visitor> findByEmployeeIdAndPurposeIdAndDepartmentIdAndUnitIdAndStatus(
                        @Param("userId") Long userId,
                        @Param("purposeId") Long purposeId,
                        @Param("departmentId") Long departmentId,
                        @Param("unitId") Long unitId,
                        @Param("status") Boolean status,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate,
                        Pageable pageable);

        @Query("SELECT v FROM Visitor v WHERE " +
                        "(:userId IS NULL OR v.user.id = :userId) AND " +
                        "(:purposeId IS NULL OR v.purpose.id = :purposeId) AND " +
                        "(:departmentId IS NULL OR v.department.id = :departmentId) AND " +
                        "(:unitId IS NULL OR v.unitId = :unitId) AND " +
                        "(:status IS NULL OR v.status = :status) AND " +
                        "(:fromDate IS NULL OR :toDate IS NULL OR (v.createdAt >= :fromDate AND v.createdAt <= :toDate))")
        List<Visitor> findByEmployeeIdAndPurposeIdAndDepartmentIdAndUnitIdAndStatus(
                        @Param("userId") Long userId,
                        @Param("purposeId") Long purposeId,
                        @Param("departmentId") Long departmentId,
                        @Param("unitId") Long unitId,
                        @Param("status") Boolean status,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);
                        
      List<Visitor> findByPhoneNumber(Long phoneNumber);
}
