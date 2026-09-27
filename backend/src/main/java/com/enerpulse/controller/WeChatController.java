package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/wechat")
public class WeChatController {

    private final Map<Long, BlackItem> deviceBlacklist = new ConcurrentHashMap<>();
    private final Map<Long, BlackItem> userBlacklist = new ConcurrentHashMap<>();
    private final AtomicLong deviceBlId = new AtomicLong(1);
    private final AtomicLong userBlId = new AtomicLong(1);

    @Data
    public static class BlackItem {
        private Long id;
        private String deviceId;
        private String openid;
        private String nickname;
        private String reason;
        private String createdAt;
        public BlackItem() { this.createdAt = java.time.OffsetDateTime.now().toString(); }
    }

    @GetMapping("/users/count")
    public ApiResponse<Map<String, Object>> userCount() {
        return ApiResponse.success(Map.of("total", 0));
    }

    @GetMapping("/messages/stats")
    public ApiResponse<Map<String, Object>> messageStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ApiResponse.success(Map.of("total", 0, "items", new ArrayList<>()));
    }

    @GetMapping("/users")
    public ApiResponse<List<Map<String, Object>>> users(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String group,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(new ArrayList<>());
    }

    @GetMapping("/alarms/history")
    public ApiResponse<List<Map<String, Object>>> alarmHistory(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(new ArrayList<>());
    }

    @GetMapping("/device-blacklist")
    public ApiResponse<List<BlackItem>> deviceBlacklist() {
        return ApiResponse.success(new ArrayList<>(deviceBlacklist.values()));
    }

    @PostMapping("/device-blacklist")
    public ApiResponse<Void> addDeviceBlacklist(@RequestBody Map<String, String> body) {
        BlackItem item = new BlackItem();
        item.id = deviceBlId.getAndIncrement();
        item.deviceId = body.get("deviceId");
        item.reason = body.get("reason");
        deviceBlacklist.put(item.id, item);
        return ApiResponse.success();
    }

    @DeleteMapping("/device-blacklist/{id}")
    public ApiResponse<Void> removeDeviceBlacklist(@PathVariable Long id) {
        deviceBlacklist.remove(id);
        return ApiResponse.success();
    }

    @GetMapping("/user-blacklist")
    public ApiResponse<List<BlackItem>> userBlacklist() {
        return ApiResponse.success(new ArrayList<>(userBlacklist.values()));
    }

    @PostMapping("/user-blacklist")
    public ApiResponse<Void> addUserBlacklist(@RequestBody Map<String, String> body) {
        BlackItem item = new BlackItem();
        item.id = userBlId.getAndIncrement();
        item.openid = body.get("openid");
        item.nickname = body.get("nickname");
        item.reason = body.get("reason");
        userBlacklist.put(item.id, item);
        return ApiResponse.success();
    }

    @DeleteMapping("/user-blacklist/{id}")
    public ApiResponse<Void> removeUserBlacklist(@PathVariable Long id) {
        userBlacklist.remove(id);
        return ApiResponse.success();
    }
}