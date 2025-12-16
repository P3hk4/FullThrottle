package com.fullthrottle.filter;

import com.fullthrottle.security.JwtTokenProvider;
import com.fullthrottle.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, // Входящий HTTP-запрос
            @NonNull HttpServletResponse response, // Исходящий HTTP-ответ
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);
            
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                String email = jwtTokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userService.loadUserByUsername(email);
                
                setAuthenticationInContext(request, userDetails);
            }
        } catch (Exception ex) {
            log.error("Failed to authenticate JWT token", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void setAuthenticationInContext(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
        
        authentication.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}