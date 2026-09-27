package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.entity.Dashboard;
import com.enerpulse.entity.DashboardWidget;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ApiResponse<List<Dashboard>> list() {
        return ApiResponse.success(dashboardService.list());
    }

    @PostMapping
    @OperationLog(module = "DASHBOARD", action = "CREATE")
    public ApiResponse<Dashboard> create(@RequestBody Dashboard dashboard) {
        return ApiResponse.success(dashboardService.create(dashboard));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "DASHBOARD", action = "UPDATE")
    public ApiResponse<Dashboard> update(@PathVariable Long id, @RequestBody Dashboard dashboard) {
        return ApiResponse.success(dashboardService.update(id, dashboard));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "DASHBOARD", action = "DELETE")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        dashboardService.delete(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}/widgets")
    public ApiResponse<List<DashboardWidget>> listWidgets(@PathVariable Long id) {
        return ApiResponse.success(dashboardService.listWidgets(id));
    }

    @PostMapping("/{id}/widgets")
    @OperationLog(module = "DASHBOARD", action = "CREATE_WIDGET")
    public ApiResponse<DashboardWidget> createWidget(@PathVariable Long id, @RequestBody DashboardWidget widget) {
        return ApiResponse.success(dashboardService.createWidget(id, widget));
    }

    @PutMapping("/{id}/widgets/{widgetId}")
    @OperationLog(module = "DASHBOARD", action = "UPDATE_WIDGET")
    public ApiResponse<DashboardWidget> updateWidget(@PathVariable Long id, @PathVariable Long widgetId,
                                                      @RequestBody DashboardWidget widget) {
        return ApiResponse.success(dashboardService.updateWidget(id, widgetId, widget));
    }

    @DeleteMapping("/{id}/widgets/{widgetId}")
    @OperationLog(module = "DASHBOARD", action = "DELETE_WIDGET")
    public ApiResponse<Void> deleteWidget(@PathVariable Long widgetId) {
        dashboardService.deleteWidget(widgetId);
        return ApiResponse.success();
    }
}
