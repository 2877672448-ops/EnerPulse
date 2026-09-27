package com.enerpulse.repository;

import com.enerpulse.entity.DashboardWidget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DashboardWidgetRepository extends JpaRepository<DashboardWidget, Long> {
    List<DashboardWidget> findByDashboardIdOrderBySortNo(Long dashboardId);
    void deleteByDashboardId(Long dashboardId);
}
