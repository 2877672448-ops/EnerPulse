package com.enerpulse.repository;

import com.enerpulse.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByTenantIdAndDeletedAtIsNullOrderBySortNo(Long tenantId);
    List<Area> findByParentIdAndDeletedAtIsNullOrderBySortNo(Long parentId);
}
