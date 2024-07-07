package com.dreamsol.repositories;

import com.dreamsol.entites.VehicleEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleEntryRepository extends JpaRepository<VehicleEntry, Long> {

    @Query("SELECT ve FROM VehicleEntry ve " +
            "JOIN ve.plant p " +
            "JOIN ve.purpose ps " +
            "WHERE (:status IS NULL OR ve.status = :status) " +
            "AND (:unitId IS NULL OR ve.unitId = :unitId) " +
            "AND (:plantId IS NULL OR p.id = :plantId) " +
            "AND (:purposeId IS NULL OR ps.id = :purposeId) " +
            "AND (:fromDate IS NULL OR ve.createdAt >= :fromDate) " +
            "AND (:toDate IS NULL OR ve.createdAt <= :toDate)")
    Page<VehicleEntry> findByParameters(
            @Param("status") Boolean status,
            @Param("unitId") Long unitId,
            @Param("plantId") Long plantId,
            @Param("purposeId") Long purposeId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);


    @Query("SELECT COUNT(v) FROM VehicleEntry v WHERE v.status = true")
    Long countTotalEntries();

    @Query("SELECT COUNT(v) FROM VehicleEntry v WHERE v.status = true AND v.purpose.status = true")
    Long countInEntries();

    @Query("SELECT COUNT(v) FROM VehicleEntry v WHERE v.status = true AND v.purpose.status = false")
    Long countOutEntries();

//    @Query("SELECT v FROM VehicleEntry v WHERE " +
//            "(?1 IS NULL OR v.status = ?1) AND " +
//            "(?2 IS NULL OR v.unitId = ?2) AND " +
//            "(?3 IS NULL OR v.plantId = ?3) AND " +
//            "(?4 IS NULL OR v.purposeId = ?4) AND " +
//            "(?5 IS NULL OR v.entryDate >= ?5) AND " +
//            "(?6 IS NULL OR v.entryDate <= ?6)")
//    List<VehicleEntry> findByFilterCount(Boolean status, Long unitId, Long plantId, Long purposeId, LocalDateTime fromDate, LocalDateTime toDate);

    @Query("SELECT ve FROM VehicleEntry ve " +
            "JOIN ve.plant p " +
            "JOIN ve.purpose ps " +
            "WHERE (:status IS NULL OR ve.status = :status) " +
            "AND (:unitId IS NULL OR ve.unitId = :unitId) " +
            "AND (:plantId IS NULL OR p.id = :plantId) " +
            "AND (:purposeId IS NULL OR ps.id = :purposeId) " +
            "AND (:fromDate IS NULL OR ve.createdAt >= :fromDate) " +
            "AND (:toDate IS NULL OR ve.createdAt <= :toDate)")
    List<VehicleEntry> findByParameters(
            @Param("status") Boolean status,
            @Param("unitId") Long unitId,
            @Param("plantId") Long plantId,
            @Param("purposeId") Long purposeId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

}