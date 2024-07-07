package com.dreamsol.repositories;

import com.dreamsol.entites.Plant;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long>, JpaSpecificationExecutor<Plant> {

        @Query("SELECT p FROM Plant p WHERE " +
                        "(:status IS NULL OR p.status = :status) AND " +
                        "(:unitId IS NULL OR p.unitId = :unitId) AND " +
                        "(:plantName IS NULL OR p.plantName = :plantName)")
        Page<Plant> findByStatusAndUnitIdAndPlantNameIgnoreCase(@Param("status") Boolean status,
                        @Param("unitId") Long unitId, @Param("plantName") String plantName,
                        Pageable pageable);

        @Query("SELECT p FROM Plant p WHERE " +
                        "(:status IS NULL OR p.status = :status) AND " +
                        "(:unitId IS NULL OR p.unitId = :unitId) AND " +
                        "(:plantName IS NULL OR p.plantName = :plantName)")
        List<Plant> findByStatusAndUnitIdAndPlantNameIgnoreCase(@Param("status") Boolean status,
                        @Param("unitId") Long unitId, @Param("plantName") String plantName);

        Page<Plant> findByStatusAndUnitId(Pageable pageable, boolean status, Long unitId);

        Page<Plant> findByUnitId(Pageable pageable, Long unitId);

        Optional<Plant> findByPlantNameContainingIgnoreCase(String vehicleNumber);

        Optional<Plant> findByPlantNameIgnoreCase(String plantName);
}
