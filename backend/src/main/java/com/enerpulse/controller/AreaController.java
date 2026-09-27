package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.common.util.BeanCopyUtils;
import com.enerpulse.dto.request.AreaRequest;
import com.enerpulse.dto.response.AreaResponse;
import com.enerpulse.entity.Area;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.service.AreaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class AreaController {
    private final AreaService areaService;


    @GetMapping
    public ApiResponse<List<Area>> list() {
        return ApiResponse.success(areaService.list());
    }

    @GetMapping("/tree")
    public ApiResponse<List<AreaResponse>> tree() {
        List<Map<String, Object>> raw = areaService.tree();
        return ApiResponse.success(convertTree(raw));
    }

    @PostMapping
    @OperationLog(module = "AREA", action = "CREATE")
    public ApiResponse<AreaResponse> create(@Valid @RequestBody AreaRequest req) {
        Area area = BeanCopyUtils.copy(req, Area.class);
        return ApiResponse.success(BeanCopyUtils.copy(areaService.create(area), AreaResponse.class));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "AREA", action = "UPDATE")
    public ApiResponse<AreaResponse> update(@PathVariable Long id, @Valid @RequestBody AreaRequest req) {
        Area area = BeanCopyUtils.copy(req, Area.class);
        return ApiResponse.success(BeanCopyUtils.copy(areaService.update(id, area), AreaResponse.class));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "AREA", action = "DELETE")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        areaService.delete(id);
        return ApiResponse.success();
    }

    @SuppressWarnings("unchecked")
    private List<AreaResponse> convertTree(List<Map<String, Object>> raw) {
        return raw.stream().map(m -> {
            AreaResponse r = new AreaResponse();
            r.setId(((Number) m.get("id")).longValue());
            r.setName((String) m.get("name"));
            r.setCode((String) m.get("code"));
            r.setAreaType((String) m.get("areaType"));
            r.setSortNo(m.get("sortNo") != null ? ((Number) m.get("sortNo")).intValue() : 0);
            r.setStatus((String) m.get("status"));
            r.setParentId(m.get("parentId") != null ? ((Number) m.get("parentId")).longValue() : null);
            Object children = m.get("children");
            if (children instanceof List) {
                r.setChildren(convertTree((List<Map<String, Object>>) children));
            }
            return r;
        }).toList();
    }
}
