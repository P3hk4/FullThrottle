package com.fullthrottle.controllers;

import com.fullthrottle.DTO.AuthRequest;
import com.fullthrottle.DTO.AuthResponse;
import com.fullthrottle.security.JwtTokenProvider;
import com.fullthrottle.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        // 1. Аутентификация
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(),
                    authRequest.getPassword()
                )
        );

        // 2. Установка аутентификации в контекст
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Генерация JWT токена
        String token = jwtTokenProvider.generateToken((UserDetailsImpl)authentication.getPrincipal());

        // 4. Получение данных пользователя
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 5. Формирование ответа
        return ResponseEntity.ok(new AuthResponse(
                token,
                userDetails.getUser().getId(),
                userDetails.getUsername(),
                userDetails.getUser().getEmail(),
                userDetails.getUser().getRole().name()
        ));
    }
}