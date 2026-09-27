package com.enerpulse.repository;

import com.enerpulse.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    @Query("select p from Permission p join RolePermission rp on p.id = rp.permissionId where rp.roleId in :roleIds")
    List<Permission> findByRoleIds(List<Long> roleIds);
}
