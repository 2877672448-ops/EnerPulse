package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "dashboard_widgets")
public class DashboardWidget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dashboard_id", nullable = false)
    private Long dashboardId;

    @Column(name = "widget_type", nullable = false, length = 32)
    private String widgetType;

    @Column(length = 128)
    private String title;

    @Column(nullable = false)
    private Integer x = 0;

    @Column(nullable = false)
    private Integer y = 0;

    @Column(nullable = false)
    private Integer w = 4;

    @Column(nullable = false)
    private Integer h = 4;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> configJson = Map.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data_source", columnDefinition = "jsonb")
    private Map<String, Object> dataSource;

    @Column(name = "sort_no", nullable = false)
    private Integer sortNo = 0;

    @Column(nullable = false)
    private Boolean visible = true;
}
