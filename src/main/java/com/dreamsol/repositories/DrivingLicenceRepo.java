package com.dreamsol.repositories;

import com.dreamsol.entites.DrivingLicence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DrivingLicenceRepo extends JpaRepository<DrivingLicence, Long> {

    Optional<DrivingLicence> findByLicence(String licence);

    Optional<DrivingLicence> findByDriverMobile(Long driverMobile);

    @Query("SELECT d FROM DrivingLicence d WHERE (:status IS NULL OR d.status = :status) AND (:unitId IS NULL OR d.unitId = :unitId)")
    Page<DrivingLicence> findByStatusAndUnitId(@Param("status") Boolean status, @Param("unitId") Long unitId, Pageable pageable);

    @Query("SELECT d FROM DrivingLicence d WHERE " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:unitId IS NULL OR d.unitId = :unitId)")
    List<DrivingLicence> findByStatusAndUnitId(
            @Param("status") Boolean status,
            @Param("unitId") Long unitId);
}
