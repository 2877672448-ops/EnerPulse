package com.enerpulse.repository;

import com.enerpulse.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByTenantIdAndDeletedAtIsNull(Long tenantId);
    List<Device> findByGatewayId(Long gatewayId);
}
