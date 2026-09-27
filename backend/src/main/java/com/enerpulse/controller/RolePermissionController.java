package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.entity.Permission;
import com.enerpulse.entity.Role;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.repository.PermissionRepository;
import com.enerpulse.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RolePermissionController {
    private final RoleService roleService;
    private final PermissionRepository permissionRepository;

    private static final Map<Long, List<Long>> roleGateways = new ConcurrentHashMap<>();

    @GetMapping("/roles")
    public ApiResponse<List<Role>> roles() {
        return ApiResponse.success(roleService.list());
    }

    @PostMapping("/roles")
    @OperationLog(module = "ROLE", action = "CREATE")
    public ApiResponse<Role> createRole(@RequestBody Role role) {
        return ApiResponse.success(roleService.create(role));
    }

    @PutMapping("/roles/{id}")
    @OperationLog(module = "ROLE", action = "UPDATE")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        return ApiResponse.success(roleService.update(id, role));
    }

    @DeleteMapping("/roles/{id}")
    @OperationLog(module = "ROLE", action = "DELETE")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.success();
    }

    @PutMapping("/roles/{id}/permissions")
    @OperationLog(module = "ROLE", action = "BIND_PERMISSIONS")
    public ApiResponse<Void> bindPermissions(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        roleService.bindPermissions(id, body.get("permissionIds"));
        return ApiResponse.success();
    }

    @GetMapping("/permissions")
    public ApiResponse<List<Permission>> permissions() {
        return ApiResponse.success(permissionRepository.findAll());
    }

    @GetMapping("/roles/{id}/permissions")
    public ApiResponse<Map<String, Object>> getRolePermissions(@PathVariable Long id) {
        return ApiResponse.success(roleService.getRolePermissions(id));
    }

    @GetMapping("/roles/{id}/gateways")
    public ApiResponse<Map<String, Object>> getRoleGateways(@PathVariable Long id) {
        List<Long> ids = roleGateways.getOrDefault(id, Collections.emptyList());
        Map<String, Object> result = new HashMap<>();
        result.put("gatewayIds", ids);
        return ApiResponse.success(result);
    }

    @PutMapping("/roles/{id}/gateways")
    @OperationLog(module = "ROLE", action = "BIND_GATEWAYS")
    public ApiResponse<Void> bindGateways(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        roleGateways.put(id, body.getOrDefault("gatewayIds", Collections.emptyList()));
        return ApiResponse.success();
    }
}