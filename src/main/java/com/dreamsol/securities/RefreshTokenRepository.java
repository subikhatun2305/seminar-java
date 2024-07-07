package com.dreamsol.securities;

import com.dreamsol.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    RefreshToken findByUser(User user);

    RefreshToken findByRefreshToken(String refreshToken);
}
