package com.dreamsol.repositories;

import com.dreamsol.entites.Purpose;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurposeRepository extends JpaRepository<Purpose, Long> {
        @Query("SELECT p FROM Purpose p WHERE " +
                "(:status IS NULL OR p.status = :status) AND " +
                "(:unitId IS NULL OR p.unitId = :unitId) AND " +
                "(:purposeName IS NULL OR p.purposeFor = :purposeName)")
        Page<Purpose> findByStatusAndUnitIdAndPurposeNameIgnoreCase(@Param("status") Boolean status,
                                                                    @Param("unitId") Long unitId, @Param("purposeName") String purposeName,
                                                                    Pageable pageable);

        @Query("SELECT p FROM Purpose p WHERE " +
                "(:status IS NULL OR p.status = :status) AND " +
                "(:unitId IS NULL OR p.unitId = :unitId) AND " +
                "(:purposeName IS NULL OR p.purposeFor = :purposeName)")
        List<Purpose> findByStatusAndUnitIdAndPurposeNameIgnoreCase(@Param("status") Boolean status,
                                                                    @Param("unitId") Long unitId, @Param("purposeName") String purposeName);
/*
        Page<Purpose> findByPurposeForAndUnitIdAndStatus(Pageable pageable, String purposeFor, Long unitId,
                                                         boolean status);

        Page<Purpose> findByPurposeForAndUnitId(Pageable pageable, String purposeFor, Long unitId);

        Optional<Purpose> findByPurposeForContainingIgnoreCase(String purposeFor);

        @Query("SELECT new com.dreamsol.dtos.responseDtos.PurposeCountDto(p.purposeFor, COUNT(p)) " +
                "FROM Purpose p WHERE p.createdAt BETWEEN :fromDate AND :toDate GROUP BY p.purposeFor")
        List<PurposeCountDto> findPurposeCountByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);*/
}