package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.energy.service.EnergyService;
import com.enerpulse.energy.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/energy")
@RequiredArgsConstructor
public class EnergyController {
    private final EnergyService energyService;
    private final ExcelExportService excelExportService;

    @GetMapping("/history")
    public ApiResponse<List<Map<String, Object>>> history(
            @RequestParam(required = false) Long gatewayId,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Long pointId,
            @RequestParam(required = false) Long energyTypeId,
            @RequestParam(required = false) String quality,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {
        return ApiResponse.success(energyService.history(gatewayId, deviceId, pointId, energyTypeId, quality, startTime, endTime));
    }

    @GetMapping("/device-data")
    public ApiResponse<List<Map<String, Object>>> deviceData(
            @RequestParam Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {
        return ApiResponse.success(energyService.history(null, deviceId, null, null, null, startTime, endTime));
    }

    @GetMapping("/consumption")
    public ApiResponse<Map<String, Object>> consumption(
            @RequestParam(required = false) Long pointId,
            @RequestParam(required = false) Long energyTypeId,
            @RequestParam(defaultValue = "DAY") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = LocalDate.now();
        return ApiResponse.success(energyService.consumption(pointId, energyTypeId, period, startDate, endDate));
    }

    @GetMapping("/analysis")
    public ApiResponse<Map<String, Object>> analysis(
            @RequestParam(required = false, defaultValue = "DEVICE") String objectType,
            @RequestParam(required = false, defaultValue = "0") Long objectId,
            @RequestParam(required = false) Long energyTypeId,
            @RequestParam(defaultValue = "DAY") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = LocalDate.now();
        return ApiResponse.success(energyService.analysis(objectType, objectId, energyTypeId, period, startDate, endDate));
    }

    @GetMapping("/ratio")
    public ApiResponse<Map<String, Object>> ratio(
            @RequestParam(required = false) Long energyTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = LocalDate.now();
        return ApiResponse.success(energyService.ratio(energyTypeId, startDate, endDate));
    }

    @GetMapping("/yoy")
    public ApiResponse<Map<String, Object>> yoy(
            @RequestParam(required = false) Long energyTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = LocalDate.now();
        return ApiResponse.success(energyService.yoy(energyTypeId, startDate, endDate));
    }

    @PostMapping("/aggregate")
    public ApiResponse<Void> aggregate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        energyService.aggregateForDate(date);
        return ApiResponse.success();
    }

    @GetMapping("/export/device")
    public ResponseEntity<byte[]> exportDeviceData(
            @RequestParam Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) throws IOException {
        byte[] data = excelExportService.exportDeviceData(deviceId, startTime, endTime);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=device-data.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/export/history")
    public ResponseEntity<byte[]> exportHistoryData(
            @RequestParam Long pointId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) throws IOException {
        byte[] data = excelExportService.exportHistoryData(pointId, startTime, endTime);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=history-data.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
