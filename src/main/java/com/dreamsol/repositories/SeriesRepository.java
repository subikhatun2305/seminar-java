package com.dreamsol.repositories;

import com.dreamsol.entites.Series;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
        @Query("SELECT p FROM Series p WHERE " +
                        "(:status IS NULL OR p.status = :status) AND " +
                        "(:unitId IS NULL OR p.unitId = :unitId) AND " +
                        "(:seriesName IS NULL OR p.seriesFor = :seriesName)")
        Page<Series> findByStatusAndUnitIdAndseriesName(@Param("status") Boolean status,
                        @Param("unitId") Long unitId, @Param("seriesName") String seriesName,
                        Pageable pageable);

        @Query("SELECT p FROM Series p WHERE " +
                        "(:status IS NULL OR p.status = :status) AND " +
                        "(:unitId IS NULL OR p.unitId = :unitId) AND " +
                        "(:seriesName IS NULL OR p.seriesFor = :seriesName)")
        List<Series> findByStatusAndUnitIdAndseriesName(@Param("status") Boolean status,
                        @Param("unitId") Long unitId, @Param("seriesName") String seriesName);

        Page<Series> findBySeriesForAndUnitId(Pageable pageable, String seriesFor, Long unitId);

        boolean existsBySeriesFor(String seriesFor);

        Optional<List<Series>> findBySeriesForIgnoreCaseAndSubPrefixIgnoreCase(String seriesFor, String subPrefix);

        boolean existsByPrefix(String prefix);
}
