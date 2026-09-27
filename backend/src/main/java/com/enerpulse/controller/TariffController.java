package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.entity.Tariff;
import com.enerpulse.entity.TariffPeriod;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TariffController {
    private final TariffService tariffService;

    @GetMapping("/tariff-periods")
    public ApiResponse<List<TariffPeriod>> listPeriods() {
        return ApiResponse.success(tariffService.listPeriods());
    }

    @PostMapping("/tariff-periods")
    @OperationLog(module = "TARIFF", action = "CREATE_PERIOD")
    public ApiResponse<TariffPeriod> createPeriod(@RequestBody TariffPeriod period) {
        return ApiResponse.success(tariffService.createPeriod(period));
    }

    @PutMapping("/tariff-periods/{id}")
    @OperationLog(module = "TARIFF", action = "UPDATE_PERIOD")
    public ApiResponse<TariffPeriod> updatePeriod(@PathVariable Long id, @RequestBody TariffPeriod period) {
        return ApiResponse.success(tariffService.updatePeriod(id, period));
    }

    @DeleteMapping("/tariff-periods/{id}")
    @OperationLog(module = "TARIFF", action = "DELETE_PERIOD")
    public ApiResponse<Void> deletePeriod(@PathVariable Long id) {
        tariffService.deletePeriod(id);
        return ApiResponse.success();
    }

    @PostMapping("/tariff-periods/validate")
    public ApiResponse<Map<String, Object>> validatePeriods() {
        return ApiResponse.success(tariffService.validatePeriods());
    }

    @GetMapping("/tariffs")
    public ApiResponse<List<Map<String, Object>>> listTariffs() {
        return ApiResponse.success(tariffService.listTariffsCombined());
    }

    @PostMapping("/tariffs")
    @OperationLog(module = "TARIFF", action = "CREATE_TARIFF")
    public ApiResponse<Map<String, Object>> createTariff(@RequestBody Map<String, Object> body) {
        return ApiResponse.success(tariffService.createTariffCombined(body));
    }

    @PutMapping("/tariffs/{id}")
    @OperationLog(module = "TARIFF", action = "UPDATE_TARIFF")
    public ApiResponse<Map<String, Object>> updateTariff(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(tariffService.updateTariffCombined(id, body));
    }

    @DeleteMapping("/tariffs/{id}")
    @OperationLog(module = "TARIFF", action = "DELETE_TARIFF")
    public ApiResponse<Void> deleteTariff(@PathVariable Long id) {
        tariffService.deleteTariff(id);
        return ApiResponse.success();
    }
}
