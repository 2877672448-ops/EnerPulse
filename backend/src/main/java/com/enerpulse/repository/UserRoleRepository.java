package com.enerpulse.repository;

import com.enerpulse.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {
    List<UserRole> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
