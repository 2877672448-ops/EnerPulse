package com.enerpulse.repository;

import com.enerpulse.entity.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
    List<Dashboard> findByTenantId(Long tenantId);
}
