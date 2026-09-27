package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.dto.request.MenuRequest;
import com.enerpulse.entity.Menu;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ApiResponse<List<Menu>> list() {
        return ApiResponse.success(menuService.list());
    }

    @PostMapping
    @OperationLog(module = "MENU", action = "CREATE")
    public ApiResponse<Menu> create(@Valid @RequestBody MenuRequest req) {
        return ApiResponse.success(menuService.create(req));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "MENU", action = "UPDATE")
    public ApiResponse<Menu> update(@PathVariable Long id, @Valid @RequestBody MenuRequest req) {
        return ApiResponse.success(menuService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "MENU", action = "DELETE")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/public-link")
    @OperationLog(module = "MENU", action = "GENERATE_PUBLIC_LINK")
    public ApiResponse<Map<String, String>> generatePublicLink(@PathVariable Long id) {
        return ApiResponse.success(Map.of("link", menuService.generatePublicLink(id)));
    }
}
