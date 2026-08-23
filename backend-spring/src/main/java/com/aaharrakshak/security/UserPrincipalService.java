package com.aaharrakshak.security;

import com.aaharrakshak.user.RoleName;
import com.aaharrakshak.user.User;
import com.aaharrakshak.user.UserRepository;
import com.aaharrakshak.user.UserRoleRepository;
import com.aaharrakshak.user.UserStatus;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPrincipalService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public UserPrincipalService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticatedUser loadUserByUsername(String username) {
        String normalized = username.trim();
        User user = (normalized.contains("@")
                ? userRepository.findByEmailIgnoreCase(normalized)
                : userRepository.findByMobileNumber(normalized))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toAuthenticatedUser(user);
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser loadActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toAuthenticatedUser(user);
    }

    @Transactional(readOnly = true)
    public Set<RoleName> rolesFor(User user) {
        return userRoleRepository.findByUserId(user.getId()).stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toUnmodifiableSet());
    }

    private AuthenticatedUser toAuthenticatedUser(User user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UsernameNotFoundException("User account is not active");
        }
        Set<RoleName> roles = rolesFor(user);
        return new AuthenticatedUser(
                user,
                roles,
                roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toSet()));
    }
}
