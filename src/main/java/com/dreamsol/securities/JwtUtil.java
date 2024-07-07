package com.dreamsol.securities;

import com.dreamsol.entites.User;
import com.dreamsol.services.impl.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.expiration}")
    private Long jwtExpirationInMs;
    private static final String SECRET_KEY = "fhsdgfhgdhfggfsgdfghdgfhdsgfhgsdhfgshdgfsgfshfhskjjgkhlkhhskhhjdnvjdjghdghdjbdhadhjhhgeueyueyuienvxnvbjfbfh";
    private static final Key key = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS512.getJcaName());

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new LinkedHashMap<>();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        User user = userDetailsImpl.getUser();
        claims.put("Id", user.getId());
        claims.put("Name", user.getName());
        claims.put("Email", user.getEmail());
        claims.put("Mobile No.", user.getMobile());
        claims.put("Status", user.isStatus());
        claims.put("Created By",user.getCreatedBy());
        claims.put("Updated By",user.getUpdatedBy());
        /*
         * claims.put("Roles",
         * List.of(user.getRoles().stream().map(Role::getRoleType).toArray()));
         * claims.put("Permissions",List.of(user.getPermissions().stream().map(
         * Permission::getPermissionType).toArray()));
         */
        String subject = userDetails.getUsername();
        return doGenerateToken(claims, subject);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String usernameFromToken = getUsernameFromToken(token);
        final String usernameFromUserDetails = userDetails.getUsername();
        return (usernameFromToken.equals(usernameFromUserDetails) && !isTokenExpired(token));
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            return "NA";
        }
    }

    public TokenPayload getUserDetails(UserDetails userDetails) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        User user = userDetailsImpl.getUser();
        return TokenPayload.builder()
                .userid(user.getId())
                .unitId(user.getUnitId())
                .name(user.getName())
                .username(user.getEmail())
                .mobile(user.getMobile())
                .email(user.getEmail())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .status(user.isStatus())
                .build();
    }
}
