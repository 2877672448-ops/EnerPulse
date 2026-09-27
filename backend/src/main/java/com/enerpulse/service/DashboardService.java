package com.enerpulse.service;

import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.entity.Dashboard;
import com.enerpulse.entity.DashboardWidget;
import com.enerpulse.repository.DashboardRepository;
import com.enerpulse.repository.DashboardWidgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository dashboardRepository;
    private final DashboardWidgetRepository widgetRepository;
    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<Dashboard> list() {
        return dashboardRepository.findByTenantId(DEFAULT_TENANT_ID);
    }

    public Dashboard create(Dashboard dashboard) {
        dashboard.setTenantId(DEFAULT_TENANT_ID);
        return dashboardRepository.save(dashboard);
    }

    public Dashboard update(Long id, Dashboard dashboard) {
        Dashboard existing = get(id);
        existing.setName(dashboard.getName());
        existing.setDescription(dashboard.getDescription());
        existing.setStatus(dashboard.getStatus());
        return dashboardRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        widgetRepository.deleteByDashboardId(id);
        dashboardRepository.deleteById(id);
    }

    public Dashboard get(Long id) {
        return dashboardRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "仪表板不存在"));
    }

    public List<DashboardWidget> listWidgets(Long dashboardId) {
        return widgetRepository.findByDashboardIdOrderBySortNo(dashboardId);
    }

    public DashboardWidget createWidget(Long dashboardId, DashboardWidget widget) {
        widget.setDashboardId(dashboardId);
        return widgetRepository.save(widget);
    }

    public DashboardWidget updateWidget(Long dashboardId, Long widgetId, DashboardWidget widget) {
        DashboardWidget existing = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new BusinessException(40401, "组件不存在"));
        existing.setWidgetType(widget.getWidgetType());
        existing.setTitle(widget.getTitle());
        existing.setX(widget.getX());
        existing.setY(widget.getY());
        existing.setW(widget.getW());
        existing.setH(widget.getH());
        existing.setConfigJson(widget.getConfigJson());
        existing.setDataSource(widget.getDataSource());
        existing.setSortNo(widget.getSortNo());
        existing.setVisible(widget.getVisible());
        return widgetRepository.save(existing);
    }

    public void deleteWidget(Long widgetId) {
        widgetRepository.deleteById(widgetId);
    }
}
