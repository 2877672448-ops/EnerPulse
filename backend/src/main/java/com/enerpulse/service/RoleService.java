package com.enerpulse.service;

import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.entity.Role;
import com.enerpulse.entity.RolePermission;
import com.enerpulse.entity.Permission;
import com.enerpulse.repository.PermissionRepository;
import com.enerpulse.repository.RolePermissionRepository;
import com.enerpulse.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<Role> list() {
        return roleRepository.findByTenantId(DEFAULT_TENANT_ID);
    }

    public Role create(Role role) {
        role.setTenantId(DEFAULT_TENANT_ID);
        return roleRepository.save(role);
    }

    public Role update(Long id, Role role) {
        Role existing = get(id);
        existing.setName(role.getName());
        existing.setCode(role.getCode());
        existing.setDescription(role.getDescription());
        existing.setStatus(role.getStatus());
        return roleRepository.save(existing);
    }

    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    @Transactional
    public void bindPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionRepository.deleteByRoleId(roleId);
        if (permissionIds != null) {
            for (Long pid : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(pid);
                rolePermissionRepository.save(rp);
            }
        }
    }

    public Role get(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "角色不存在"));
    }
    public Map<String, Object> getRolePermissions(Long roleId) {
        List<Permission> all = permissionRepository.findAll();
        List<Long> checkedIds = rolePermissionRepository.findByRoleId(roleId)
                .stream().map(RolePermission::getPermissionId).toList();
        return Map.of(
            "permissions", all,
            "checkedIds", checkedIds
        );
    }

}