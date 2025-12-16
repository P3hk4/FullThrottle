package com.fullthrottle.security;

import com.fullthrottle.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Используем email как username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Аккаунт не просрочен
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked(); 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}