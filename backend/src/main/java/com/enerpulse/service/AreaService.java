package com.enerpulse.service;

import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.entity.Area;
import com.enerpulse.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaService {
    private final AreaRepository areaRepository;
    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<Map<String, Object>> tree() {
        List<Area> all = areaRepository.findByTenantIdAndDeletedAtIsNullOrderBySortNo(DEFAULT_TENANT_ID);
        Map<Long, List<Area>> byParent = all.stream().collect(Collectors.groupingBy(a -> a.getParentId() == null ? 0L : a.getParentId()));
        List<Map<String, Object>> roots = new ArrayList<>();
        for (Area root : byParent.getOrDefault(0L, List.of())) {
            roots.add(buildNode(root, byParent));
        }
        return roots;
    }

    private Map<String, Object> buildNode(Area area, Map<Long, List<Area>> byParent) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("id", area.getId());
        node.put("parentId", area.getParentId());
        node.put("code", area.getCode());
        node.put("name", area.getName());
        node.put("areaType", area.getAreaType());
        node.put("sortNo", area.getSortNo());
        node.put("status", area.getStatus());
        List<Map<String, Object>> children = new ArrayList<>();
        for (Area child : byParent.getOrDefault(area.getId(), List.of())) {
            children.add(buildNode(child, byParent));
        }
        node.put("children", children);
        return node;
    }

    public Area create(Area area) {
        area.setTenantId(DEFAULT_TENANT_ID);
        return areaRepository.save(area);
    }

    public Area update(Long id, Area area) {
        Area existing = get(id);
        existing.setParentId(area.getParentId());
        existing.setAreaType(area.getAreaType());
        existing.setCode(area.getCode());
        existing.setName(area.getName());
        existing.setSortNo(area.getSortNo());
        existing.setStatus(area.getStatus());
        return areaRepository.save(existing);
    }

    public void delete(Long id) {
        Area a = get(id);
        a.setDeletedAt(OffsetDateTime.now());
        areaRepository.save(a);
    }

    public Area get(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "区域不存在"));
    }
    public List<Area> list() {
        return areaRepository.findByTenantIdAndDeletedAtIsNullOrderBySortNo(DEFAULT_TENANT_ID);
    }

}
