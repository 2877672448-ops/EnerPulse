package com.enerpulse.repository;

import com.enerpulse.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    List<Tariff> findByTenantIdAndStatus(Long tenantId, String status);
    List<Tariff> findByEnergyTypeIdAndStatus(Long energyTypeId, String status);
}
