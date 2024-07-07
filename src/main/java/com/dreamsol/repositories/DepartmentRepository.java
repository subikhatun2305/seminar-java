package com.dreamsol.repositories;

import com.dreamsol.entites.Department;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

        @Query("SELECT d FROM Department d WHERE " +
                        "(:status IS NULL OR d.status = :status) AND " +
                        "(:unitId IS NULL OR d.unitId = :unitId)")
        Page<Department> findByStatusAndUnitId(@Param("status") Boolean status,
                        @Param("unitId") Long unitId,
                        Pageable pageable);

        @Query("SELECT d FROM Department d WHERE " +
                        "(:status IS NULL OR d.status = :status) AND " +
                        "(:unitId IS NULL OR d.unitId = :unitId)")
        List<Department> findByStatusAndUnitId(@Param("status") Boolean status,
                        @Param("unitId") Long unitId);

        Optional<Department> findByDepartmentCodeIgnoreCase(String departmentCode);
        /*
         * 
         * Optional<Department>
         * findByDepartmentNameIgnoreCaseOrDepartmentCodeIgnoreCase(String
         * departmentName,
         * String departmentCode);
         * 
         * Optional<Department>
         * findByDepartmentNameIgnoreCaseAndDepartmentCodeIgnoreCase(String
         * departmentName,
         * String departmentCode);
         */

}