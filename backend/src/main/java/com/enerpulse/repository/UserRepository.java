package com.enerpulse.repository;

import com.enerpulse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTenantIdAndUsername(Long tenantId, String username);
    Optional<User> findByUsername(String username);
}
