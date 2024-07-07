package com.dreamsol.repositories;

import com.dreamsol.entites.Unit;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    @Query("SELECT u FROM Unit u WHERE (:status IS NULL OR u.status = :status)")
    Page<Unit> findByStatus(@Param("status") Boolean status, Pageable pageable);

    @Query("SELECT u FROM Unit u WHERE (:status IS NULL OR u.status = :status)")
    List<Unit> findByStatus(@Param("status") Boolean status);

    Optional<Unit> findByUnitNameIgnoreCaseOrUnitIp(String unitName, String unitIp);

    Optional<Unit> findByUnitNameIgnoreCaseAndUnitIp(String unitName, String unitIp);

}
