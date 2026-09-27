package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.entity.Device;
import com.enerpulse.entity.Point;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.service.DeviceService;
import com.enerpulse.service.PointService;
import com.enerpulse.dto.request.PointSyncRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DevicePointController {
    private final DeviceService deviceService;
    private final PointService pointService;

    @GetMapping("/devices")
    public ApiResponse<List<Device>> listDevices() {
        return ApiResponse.success(deviceService.list());
    }

    @PostMapping("/devices")
    @OperationLog(module = "DEVICE", action = "CREATE")
    public ApiResponse<Device> createDevice(@RequestBody Device device) {
        return ApiResponse.success(deviceService.create(device));
    }

    @PutMapping("/devices/{id}")
    @OperationLog(module = "DEVICE", action = "UPDATE")
    public ApiResponse<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        return ApiResponse.success(deviceService.update(id, device));
    }

    @DeleteMapping("/devices/{id}")
    @OperationLog(module = "DEVICE", action = "DELETE")
    public ApiResponse<Void> deleteDevice(@PathVariable Long id) {
        deviceService.delete(id);
        return ApiResponse.success();
    }

    @GetMapping("/points")
    public ApiResponse<List<Point>> listPoints(@RequestParam(required = false) Long gatewayId) {
        return ApiResponse.success(pointService.list(gatewayId));
    }

    @PostMapping("/points")
    @OperationLog(module = "POINT", action = "CREATE")
    public ApiResponse<Point> createPoint(@RequestBody Point point) {
        return ApiResponse.success(pointService.create(point));
    }

    @PutMapping("/points/{id}")
    @OperationLog(module = "POINT", action = "UPDATE")
    public ApiResponse<Point> updatePoint(@PathVariable Long id, @RequestBody Point point) {
        return ApiResponse.success(pointService.update(id, point));
    }

    @DeleteMapping("/points/{id}")
    @OperationLog(module = "POINT", action = "DELETE")
    public ApiResponse<Void> deletePoint(@PathVariable Long id) {
        pointService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/points/sync")
    @OperationLog(module = "POINT", action = "SYNC")
    public ApiResponse<List<Point>> syncPoints(@RequestBody PointSyncRequest request) {
        return ApiResponse.success(pointService.syncPoints(request.getGatewayId(), request.getPoints()));
    }
}