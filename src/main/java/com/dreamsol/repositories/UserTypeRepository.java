package com.dreamsol.repositories;

import com.dreamsol.entites.UserType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserTypeRepository extends JpaRepository<UserType,Long> {

    Optional<UserType> findByIdAndStatusTrue(Long id);

    Optional<UserType> findByUserTypeNameOrUserTypeCode(String userTypeName, String userTypeCode);
    @Query("SELECT u FROM UserType u WHERE " +
            "(:unitId IS NULL OR u.unitId = :unitId) AND " +
            "(:status IS NULL OR u.status = :status)")
    List<UserType> findByFilters(@Param("unitId") Long unitId,
                                 @Param("status") Boolean status,Pageable pageable);

    @Query("SELECT u FROM UserType u WHERE " +
            "(:unitId IS NULL OR u.unitId = :unitId) AND " +
            "(:status IS NULL OR u.status = :status)")
    List<UserType> findByFilters(@Param("unitId") Long unitId,
                                @Param("status") Boolean status);

    Optional<UserType> findByUserTypeName(String usertypeName);
}
