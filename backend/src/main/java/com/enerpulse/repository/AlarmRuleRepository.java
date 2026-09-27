package com.enerpulse.repository;

import com.enerpulse.entity.AlarmRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlarmRuleRepository extends JpaRepository<AlarmRule, Long> {
    List<AlarmRule> findByPointIdAndEnabledTrue(Long pointId);
    List<AlarmRule> findByTenantId(Long tenantId);
}
