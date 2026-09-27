package com.enerpulse.repository;

import com.enerpulse.entity.EnergyDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EnergyDailyRepository extends JpaRepository<EnergyDaily, Long> {
    Optional<EnergyDaily> findByTenantIdAndPointIdAndPeriodDate(Long tenantId, Long pointId, LocalDate periodDate);

    @Query("select d from EnergyDaily d where d.tenantId = :tenantId and d.pointId = :pointId and d.periodDate between :start and :end order by d.periodDate")
    List<EnergyDaily> findByPointIdAndPeriodDateBetween(@Param("tenantId") Long tenantId, @Param("pointId") Long pointId,
                                                         @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("select d from EnergyDaily d where d.tenantId = :tenantId and d.energyTypeId = :energyTypeId and d.periodDate between :start and :end order by d.periodDate")
    List<EnergyDaily> findByEnergyTypeAndPeriod(@Param("tenantId") Long tenantId, @Param("energyTypeId") Long energyTypeId,
                                                 @Param("start") LocalDate start, @Param("end") LocalDate end);
}
