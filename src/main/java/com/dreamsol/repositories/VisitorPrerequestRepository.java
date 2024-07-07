package com.dreamsol.repositories;

import com.dreamsol.entites.VisitorPrerequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitorPrerequestRepository extends JpaRepository<VisitorPrerequest,Long>
{
    Optional<VisitorPrerequest> findByOtp(String otp);
    @Query("SELECT v FROM VisitorPrerequest v WHERE " +
            "(:meetingStatus IS NULL OR v.meetingStatus = :meetingStatus) AND "+
            "(:meetingPurposeId IS NULL OR v.meetingPurpose.id = :meetingPurposeId) AND "+
            "(:fromDate IS NULL OR :toDate IS NULL OR (v.createdAt >= :fromDate AND v.createdAt <= :toDate))")
    List<VisitorPrerequest> findByFilters(@Param("meetingStatus") String meetingStatus,
                       @Param("meetingPurposeId") Long meetingPurposeId,
                       @Param("fromDate") LocalDateTime fromDate,
                       @Param("toDate") LocalDateTime toDate);
    @Query("SELECT v FROM VisitorPrerequest v WHERE " +
            "(:unitId IS NULL OR v.unitId = :unitId) AND " +
            "(:status IS NULL OR v.status = :status) AND " +
            "(:meetingPurposeId IS NULL OR v.meetingPurpose.id = :meetingPurposeId) AND " +
            "(:meetingStatus IS NULL OR v.meetingStatus = :meetingStatus) AND " +
            "(:fromDate IS NULL OR :toDate IS NULL OR (v.createdAt >= :fromDate AND v.createdAt <= :toDate))")
    List<VisitorPrerequest> findByFilters(@Param("unitId") Long unitId,
                                          @Param("status") Boolean status,
                                          @Param("meetingPurposeId") Long meetingPurposeId,
                                          @Param("meetingStatus") String meetingStatus,
                                          @Param("fromDate") LocalDateTime fromDate,
                                          @Param("toDate") LocalDateTime toDate,Pageable pageable);

    @Query("SELECT v FROM VisitorPrerequest v WHERE " +
            "(:unitId IS NULL OR v.unitId = :unitId) AND " +
            "(:status IS NULL OR v.status = :status) AND " +
            "(:meetingPurposeId IS NULL OR v.meetingPurpose.id = :meetingPurposeId) AND " +
            "(:meetingStatus IS NULL OR v.meetingStatus = :meetingStatus) AND " +
            "(:fromDate IS NULL OR :toDate IS NULL OR (v.createdAt >= :fromDate AND v.createdAt <= :toDate))")
    List<VisitorPrerequest> findByFilters(@Param("unitId") Long unitId,
                                          @Param("status") Boolean status,
                                          @Param("meetingPurposeId") Long meetingPurposeId,
                                          @Param("meetingStatus") String meetingStatus,
                                          @Param("fromDate") LocalDateTime fromDate,
                                          @Param("toDate") LocalDateTime toDate);

}
