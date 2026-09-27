package com.enerpulse.service;

import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.entity.Point;
import com.enerpulse.repository.PointRepository;
import com.enerpulse.dto.request.PointSyncItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<Point> list(Long gatewayId) {
        if (gatewayId != null) return pointRepository.findByGatewayId(gatewayId);
        return pointRepository.findAll();
    }

    public Point create(Point point) {
        point.setTenantId(DEFAULT_TENANT_ID);
        return pointRepository.save(point);
    }

    public Point update(Long id, Point point) {
        Point existing = get(id);
        existing.setGatewayId(point.getGatewayId());
        existing.setDeviceId(point.getDeviceId());
        existing.setPointCode(point.getPointCode());
        existing.setName(point.getName());
        existing.setEnergyTypeId(point.getEnergyTypeId());
        existing.setEnergyItemId(point.getEnergyItemId());
        existing.setValueType(point.getValueType());
        existing.setUnit(point.getUnit());
        existing.setTotalFlag(point.getTotalFlag());
        existing.setMultiplier(point.getMultiplier());
        existing.setStatus(point.getStatus());
        existing.setDescription(point.getDescription());
        return pointRepository.save(existing);
    }

    public void delete(Long id) {
        pointRepository.deleteById(id);
    }

    public Point get(Long id) {
        return pointRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "测点不存在"));
    }

    public List<Point> syncPoints(Long gatewayId, List<PointSyncItem> items) {
        java.util.List<Point> result = new java.util.ArrayList<>();
        for (PointSyncItem item : items) {
            Point point = pointRepository.findByGatewayIdAndPointCode(gatewayId, item.getPointCode())
                    .orElseGet(Point::new);
            point.setTenantId(DEFAULT_TENANT_ID);
            point.setGatewayId(gatewayId);
            point.setPointCode(item.getPointCode());
            point.setName(item.getName());
            point.setEnergyTypeId(item.getEnergyTypeId());
            point.setEnergyItemId(item.getEnergyItemId());
            point.setValueType(item.getValueType());
            point.setUnit(item.getUnit());
            point.setTotalFlag(item.getTotalFlag() != null ? item.getTotalFlag() : false);
            point.setMultiplier(item.getMultiplier() != null ? item.getMultiplier() : java.math.BigDecimal.ONE);
            if (point.getStatus() == null) point.setStatus("ACTIVE");
            point.setDescription(item.getDescription());
            result.add(pointRepository.save(point));
        }
        return result;
    }
}