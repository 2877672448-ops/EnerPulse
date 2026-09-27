package com.enerpulse.service;

import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.entity.Device;
import com.enerpulse.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<Device> list() {
        return deviceRepository.findByTenantIdAndDeletedAtIsNull(DEFAULT_TENANT_ID);
    }

    public Device create(Device device) {
        device.setTenantId(DEFAULT_TENANT_ID);
        return deviceRepository.save(device);
    }

    public Device update(Long id, Device device) {
        Device existing = get(id);
        existing.setDeviceCode(device.getDeviceCode());
        existing.setName(device.getName());
        existing.setGatewayId(device.getGatewayId());
        existing.setAreaId(device.getAreaId());
        existing.setEnergyTypeId(device.getEnergyTypeId());
        existing.setInstallLocation(device.getInstallLocation());
        existing.setStatus(device.getStatus());
        existing.setDescription(device.getDescription());
        return deviceRepository.save(existing);
    }

    public void delete(Long id) {
        Device d = get(id);
        d.setDeletedAt(OffsetDateTime.now());
        deviceRepository.save(d);
    }

    public Device get(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "设备不存在"));
    }
}
