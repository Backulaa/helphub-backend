package com.helphub.backend.security.model;

import com.helphub.backend.persistence.entity.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.lang.NonNull;

import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Getter
public class CustomUserDetails implements UserDetails {
    private final @NonNull UUID id;
    private final String email;
    private final String password;
    private final boolean isActive;
    private final String role;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = Objects.requireNonNull(user.getId(), "User id must not be null");
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.isActive = Boolean.TRUE.equals(user.getIsActive());
        this.role = user.getRole().name();
        this.authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    @NonNull
    public UUID getUserId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public boolean hasRole(String roleName) {
        return this.role.equalsIgnoreCase(roleName);
    }
}
