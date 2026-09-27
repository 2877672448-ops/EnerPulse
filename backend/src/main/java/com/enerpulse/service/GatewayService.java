package com.enerpulse.service;

import com.enerpulse.common.api.ErrorCode;
import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.entity.Gateway;
import com.enerpulse.repository.GatewayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GatewayService {
    private final GatewayRepository gatewayRepository;
    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<Gateway> list() {
        return gatewayRepository.findByTenantIdAndDeletedAtIsNull(DEFAULT_TENANT_ID);
    }

    public Gateway create(Gateway gateway) {
        gateway.setTenantId(DEFAULT_TENANT_ID);
        if (gateway.getServerHost() == null) {
            gateway.setServerHost("localhost");
        }
        if (gateway.getServerPort() == null) {
            gateway.setServerPort(1883);
        }
        if (gateway.getReportInterval() == null) {
            gateway.setReportInterval(60);
        }
        if (gateway.getStatus() == null) {
            gateway.setStatus("OFFLINE");
        }
        try {
            return gatewayRepository.save(gateway);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.CONFLICT, "网关编码或Topic已存在");
        }
    }

    public Gateway update(Long id, Gateway gateway) {
        Gateway existing = get(id);
        existing.setGatewayCode(gateway.getGatewayCode());
        existing.setName(gateway.getName());
        existing.setTopic(gateway.getTopic());
        existing.setServerHost(gateway.getServerHost());
        existing.setServerPort(gateway.getServerPort());
        existing.setReportInterval(gateway.getReportInterval());
        existing.setStatus(gateway.getStatus());
        try {
            return gatewayRepository.save(existing);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.CONFLICT, "网关编码或Topic已存在");
        }
    }

    public void delete(Long id) {
        Gateway g = get(id);
        g.setDeletedAt(OffsetDateTime.now());
        gatewayRepository.save(g);
    }

    public Gateway get(Long id) {
        return gatewayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "网关不存在"));
    }

    public Map<String, Object> status(Long id) {
        Gateway g = get(id);
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("id", g.getId());
        result.put("gatewayCode", g.getGatewayCode());
        result.put("status", g.getStatus());
        result.put("lastOnlineAt", g.getLastOnlineAt());
        return result;
    }
}
