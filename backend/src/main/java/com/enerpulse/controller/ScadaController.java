package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/scada")
public class ScadaController {

    private final Map<Long, ScadaItem> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Data
    public static class ScadaItem {
        private Long id;
        private String name;
        private String homePageFile;
        private String fileName;
        private String accessType = "LOGIN";
        private String status = "PUBLISHED";
        private String createdAt;
        public ScadaItem() { this.createdAt = java.time.OffsetDateTime.now().toString(); }
    }

    @GetMapping
    public ApiResponse<List<ScadaItem>> list() {
        return ApiResponse.success(new ArrayList<>(store.values()));
    }

    @PostMapping
    public ApiResponse<ScadaItem> create(@RequestParam String name,
                                          @RequestParam(required = false) String homePageFile,
                                          @RequestParam(required = false) String fileName) {
        ScadaItem item = new ScadaItem();
        item.id = idGen.getAndIncrement();
        item.name = name;
        item.homePageFile = homePageFile;
        item.fileName = fileName;
        store.put(item.id, item);
        return ApiResponse.success(item);
    }

    @PutMapping("/{id}")
    public ApiResponse<ScadaItem> update(@PathVariable Long id, @RequestBody ScadaItem body) {
        ScadaItem item = store.get(id);
        if (item == null) return ApiResponse.success(null);
        if (body.name != null) item.name = body.name;
        return ApiResponse.success(item);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        store.remove(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/upload")
    public ApiResponse<Void> upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        ScadaItem item = store.get(id);
        if (item != null && file.getOriginalFilename() != null) {
            item.fileName = file.getOriginalFilename();
        }
        return ApiResponse.success();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=scada.zip")
                .body(new byte[0]);
    }

    @PutMapping("/{id}/access")
    public ApiResponse<Void> toggleAccess(@PathVariable Long id, @RequestBody Map<String, String> body) {
        ScadaItem item = store.get(id);
        if (item != null && body.get("accessType") != null) {
            item.accessType = body.get("accessType");
        }
        return ApiResponse.success();
    }

    @PutMapping("/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable Long id) {
        ScadaItem item = store.get(id);
        if (item != null) item.status = "PUBLISHED";
        return ApiResponse.success();
    }
}