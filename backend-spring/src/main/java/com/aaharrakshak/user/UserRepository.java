package com.aaharrakshak.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByMobileNumber(String mobileNumber);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMobileNumber(String mobileNumber);

    @Query("""
            select ur.user
            from UserRole ur
            where ur.role.name = :roleName
            order by ur.user.id
            """)
    List<User> findByRoleName(@Param("roleName") RoleName roleName);
}
