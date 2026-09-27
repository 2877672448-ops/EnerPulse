package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.common.api.PageResult;
import com.enerpulse.entity.Alarm;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.security.SecurityUtils;
import com.enerpulse.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    @GetMapping
    public ApiResponse<PageResult<Alarm>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long gatewayId,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Long pointId,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {
        return ApiResponse.success(alarmService.list(page, pageSize, gatewayId, deviceId, pointId, level, status, startTime, endTime));
    }

    @GetMapping("/{id}")
    public ApiResponse<Alarm> get(@PathVariable Long id) {
        return ApiResponse.success(alarmService.get(id));
    }

    @PostMapping("/{id}/ack")
    @OperationLog(module = "ALARM", action = "ACK")
    public ApiResponse<Alarm> ack(@PathVariable Long id) {
        return ApiResponse.success(alarmService.ack(id, SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/{id}/recover")
    @OperationLog(module = "ALARM", action = "RECOVER")
    public ApiResponse<Alarm> recover(@PathVariable Long id) {
        return ApiResponse.success(alarmService.recover(id));
    }

    @PostMapping("/{id}/close")
    @OperationLog(module = "ALARM", action = "CLOSE")
    public ApiResponse<Alarm> close(@PathVariable Long id) {
        return ApiResponse.success(alarmService.close(id, SecurityUtils.getCurrentUserId()));
    }
}
