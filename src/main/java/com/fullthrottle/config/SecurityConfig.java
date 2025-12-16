package com.fullthrottle.config;

import com.fullthrottle.exception.JwtAuthEntryPoint;
import com.fullthrottle.filter.JwtAuthenticationFilter;
import com.fullthrottle.security.JwtTokenProvider;
import com.fullthrottle.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtAuthEntryPoint authEntryPoint, UserService userService, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.authEntryPoint = authEntryPoint;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                                           UserService userService) {
        return new JwtAuthenticationFilter(jwtTokenProvider, userService);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/**").permitAll()
                                .requestMatchers("/api/auth/login").permitAll()
                ).addFilterBefore(jwtAuthenticationFilter(
                                jwtTokenProvider,
                                userService),
                        UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/auth/login")  // Указываем кастомную страницу входа
                        .loginProcessingUrl("/auth/login")  // URL для обработки формы входа
                        .defaultSuccessUrl("/tasks", true)  // Перенаправление после успешного входа
                        .failureUrl("/auth/login?error=true")  // Перенаправление при ошибке
                        .usernameParameter("email")  // Параметр для email
                        .passwordParameter("password")  // Параметр для пароля
                        .permitAll()
                ).build();

        
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}