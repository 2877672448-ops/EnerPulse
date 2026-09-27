package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.dto.request.AlarmRuleRequest;
import com.enerpulse.entity.AlarmRule;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.service.AlarmRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alarm-rules")
@RequiredArgsConstructor
public class AlarmRuleController {
    private final AlarmRuleService alarmRuleService;

    @GetMapping
    public ApiResponse<List<AlarmRule>> list() {
        return ApiResponse.success(alarmRuleService.list());
    }

    @PostMapping
    @OperationLog(module = "ALARM_RULE", action = "CREATE")
    public ApiResponse<AlarmRule> create(@Valid @RequestBody AlarmRuleRequest req) {
        return ApiResponse.success(alarmRuleService.create(req));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "ALARM_RULE", action = "UPDATE")
    public ApiResponse<AlarmRule> update(@PathVariable Long id, @Valid @RequestBody AlarmRuleRequest req) {
        return ApiResponse.success(alarmRuleService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "ALARM_RULE", action = "DELETE")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        alarmRuleService.delete(id);
        return ApiResponse.success();
    }
}
