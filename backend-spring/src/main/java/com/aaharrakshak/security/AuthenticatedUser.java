package com.aaharrakshak.security;

import com.aaharrakshak.user.RoleName;
import com.aaharrakshak.user.User;
import java.util.Collection;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticatedUser implements UserDetails {

    private final User user;
    private final Set<RoleName> roles;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(User user, Set<RoleName> roles, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.roles = roles;
        this.authorities = authorities;
    }

    public User getUser() {
        return user;
    }

    public Set<RoleName> getRoles() {
        return roles;
    }

    public Long getUserId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail() != null ? user.getEmail() : user.getMobileNumber();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
}

