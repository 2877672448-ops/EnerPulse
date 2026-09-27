package com.enerpulse.repository;

import com.enerpulse.entity.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GatewayRepository extends JpaRepository<Gateway, Long> {
    List<Gateway> findByTenantIdAndDeletedAtIsNull(Long tenantId);
    Optional<Gateway> findByGatewayCode(String gatewayCode);
    Optional<Gateway> findByTenantIdAndGatewayCode(Long tenantId, String gatewayCode);
    Optional<Gateway> findByTopic(String topic);
}
