package com.dreamsol.repositories;

import com.dreamsol.entites.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String username);
    Optional<User> findByEmailOrMobile(String email, Long mobile);
    @Query("SELECT u FROM User u WHERE " +
            "(:unitId IS NULL OR u.unitId = :unitId) AND " +
            "(:status IS NULL OR u.status = :status)")
    List<User> findByFilters(@Param("unitId") Long unitId,
                                 @Param("status") Boolean status, Pageable pageable);
    @Query("SELECT u FROM User u WHERE " +
            "(:unitId IS NULL OR u.unitId = :unitId) AND " +
            "(:status IS NULL OR u.status = :status)")
    List<User> findByFilters(@Param("unitId") Long unitId,
                             @Param("status") Boolean status);

}
