package com.dreamsol.repositories;

import com.dreamsol.entites.VehicleLicence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleLicenceRepo extends JpaRepository<VehicleLicence, Long> {

    boolean existsByVehicleNumber(String vehicleNumber);

    Optional<VehicleLicence> findByVehicleNumber(String vehicleNumber);

    @Query("SELECT v FROM VehicleLicence v WHERE " +
            "(:status IS NULL OR v.status = :status) AND " +
            "(:unitId IS NULL OR v.unitId = :unitId)")
    Page<VehicleLicence> findByStatusAndUnitId(@Param("status") Boolean status,
                                               @Param("unitId") Long unitId, Pageable pageable);

    @Query("SELECT v FROM VehicleLicence v WHERE " +
            "(:status IS NULL OR v.status = :status) AND " +
            "(:unitId IS NULL OR v.unitId = :unitId)")
    List<VehicleLicence> findByStatusAndUnitId(
            @Param("status") Boolean status,
            @Param("unitId") Long unitId);
}
