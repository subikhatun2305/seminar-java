package com.dreamsol.services;

import com.dreamsol.dtos.responseDtos.ApiResponse;
import com.dreamsol.dtos.responseDtos.AuthResponseDto;
import com.dreamsol.securities.RefreshToken;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.securities.RefreshTokenRepository;
import com.dreamsol.services.impl.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthRequestService
{
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.validity.refresh-token}")
    private long REFRESH_TOKEN_VALIDITY;
    public ResponseEntity<?> getToken(String username, String password)
    {
        getAuthentication(username,password);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken = createRefreshToken(userDetails);
        AuthResponseDto response = AuthResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    public void getAuthentication(String username,String password)
    {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,password);
        try{
             authenticationManager.authenticate(authentication);
        }catch (BadCredentialsException e)
        {
            throw new BadCredentialsException(" Invalid username or password !");
        }
    }
    public RefreshToken createRefreshToken(UserDetails userDetails)
    {
        try
        {
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
            RefreshToken refreshToken = refreshTokenRepository.findByUser(userDetailsImpl.getUser());
            if(refreshToken != null && isValidRefreshToken(refreshToken))
                return refreshToken;
            if(refreshToken != null)
            {
                refreshToken.setRefreshToken(UUID.randomUUID()+"."+UUID.randomUUID()+"."+UUID.randomUUID());
                refreshToken.setExpiry(System.currentTimeMillis()+REFRESH_TOKEN_VALIDITY);
                refreshTokenRepository.save(refreshToken);
                return refreshToken;
            }
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID()+"."+UUID.randomUUID()+"."+UUID.randomUUID())
                    .expiry(System.currentTimeMillis()+REFRESH_TOKEN_VALIDITY)
                    .user(userDetailsImpl.getUser())
                    .build();
            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }catch (Exception e)
        {
            throw new RuntimeException("Error occurred, Refresh Token not created, Reason: "+e.getMessage());
        }
    }
    public ResponseEntity<?> createTokenByRefreshToken(String refreshToken)
    {
        try
        {
            RefreshToken refreshTokenDB = refreshTokenRepository.findByRefreshToken(refreshToken);
            if (refreshTokenDB == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Refresh token doesn't exist. Please! login again to get new token and refresh token.", false));
            }
            if (isValidRefreshToken(refreshTokenDB))
            {
                UserDetails userDetails = userDetailsService.loadUserByUsername(refreshTokenDB.getUser().getEmail());
                UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
                String newToken = jwtUtil.generateToken(userDetailsImpl);
                AuthResponseDto authResponse = AuthResponseDto.builder()
                        .accessToken(newToken)
                        .refreshToken(refreshToken)
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(authResponse);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Error Occurred while creating new access token, Reason: Refresh token has been expired!", false));
        }catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Error Occurred while creating new access token, Reason: "+e.getMessage(), false));
        }
    }
    public boolean isValidRefreshToken(RefreshToken refreshToken)
    {
        return refreshToken.getExpiry() > System.currentTimeMillis();
    }

    public ResponseEntity<?> getUserDetailsFromToken(String token) {
        try {
            String username;
            if (token.startsWith("Bearer"))
                username = token.substring(7);
            username = jwtUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return ResponseEntity.status(HttpStatus.OK).body(jwtUtil.getUserDetails(userDetails));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching user details from token");
        }
    }

}
