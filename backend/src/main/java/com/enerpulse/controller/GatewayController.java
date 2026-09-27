package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.common.util.BeanCopyUtils;
import com.enerpulse.dto.request.GatewayRequest;
import com.enerpulse.dto.response.GatewayResponse;
import com.enerpulse.entity.Gateway;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.service.GatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gateways")
@RequiredArgsConstructor
public class GatewayController {
    private final GatewayService gatewayService;

    @GetMapping
    public ApiResponse<List<GatewayResponse>> list() {
        return ApiResponse.success(BeanCopyUtils.copyList(gatewayService.list(), GatewayResponse.class));
    }

    @PostMapping
    @OperationLog(module = "GATEWAY", action = "CREATE")
    public ApiResponse<GatewayResponse> create(@Valid @RequestBody GatewayRequest req) {
        Gateway g = BeanCopyUtils.copy(req, Gateway.class);
        return ApiResponse.success(BeanCopyUtils.copy(gatewayService.create(g), GatewayResponse.class));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "GATEWAY", action = "UPDATE")
    public ApiResponse<GatewayResponse> update(@PathVariable Long id, @Valid @RequestBody GatewayRequest req) {
        Gateway g = BeanCopyUtils.copy(req, Gateway.class);
        return ApiResponse.success(BeanCopyUtils.copy(gatewayService.update(id, g), GatewayResponse.class));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "GATEWAY", action = "DELETE")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        gatewayService.delete(id);
        return ApiResponse.success();
    }
}
