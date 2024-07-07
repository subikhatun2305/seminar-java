package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.AuthRequestDto;
import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.services.AuthRequestService;
import com.dreamsol.services.CommonService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController
{
    private final AuthRequestService authRequestService;

    private final CommonService<UserRequestDto,Long> commonService;

    @Autowired
    public AuthenticationController(@Qualifier("userService") CommonService<UserRequestDto,Long> commonService, AuthRequestService authRequestService) {
        this.commonService = commonService;
        this.authRequestService = authRequestService;
    }

    @PostMapping("/authenticate-user")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDto authRequestDto)
    {
        return authRequestService.getToken(authRequestDto.getUsername(),authRequestDto.getPassword());
    }

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDto userRequestDto)
    {
        return commonService.create(userRequestDto);
    }

    @GetMapping("/regenerate-token")
    public ResponseEntity<?> regenerateToken(@RequestParam String refreshToken)
    {
        return authRequestService.createTokenByRefreshToken(refreshToken);
    }
    @GetMapping("/get-user-details")
    public ResponseEntity<?> getUserDetailsFromToken(@RequestParam("token") String token)
    {
        return authRequestService.getUserDetailsFromToken(token);
    }
}
