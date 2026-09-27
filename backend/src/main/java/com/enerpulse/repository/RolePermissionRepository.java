package com.enerpulse.repository;

import com.enerpulse.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermission.RolePermissionId> {
    List<RolePermission> findByRoleId(Long roleId);
    void deleteByRoleId(Long roleId);
}
