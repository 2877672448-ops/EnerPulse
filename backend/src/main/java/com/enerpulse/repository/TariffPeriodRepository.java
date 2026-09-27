package com.enerpulse.repository;

import com.enerpulse.entity.TariffPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TariffPeriodRepository extends JpaRepository<TariffPeriod, Long> {
    List<TariffPeriod> findByTenantIdAndStatusOrderBySortNo(Long tenantId, String status);
    List<TariffPeriod> findByTenantIdOrderBySortNo(Long tenantId);
}
