package com.enerpulse.repository;

import com.enerpulse.entity.EnergyRawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface EnergyRawDataRepository extends JpaRepository<EnergyRawData, Long> {
    List<EnergyRawData> findByPointIdAndTsBetweenOrderByTs(Long pointId, OffsetDateTime start, OffsetDateTime end);
    List<EnergyRawData> findByDeviceIdAndTsBetweenOrderByTs(Long deviceId, OffsetDateTime start, OffsetDateTime end);
    List<EnergyRawData> findByGatewayIdAndTsBetweenOrderByTs(Long gatewayId, OffsetDateTime start, OffsetDateTime end);
    Optional<EnergyRawData> findByMessageIdAndPointId(String messageId, Long pointId);

    @Query("select r from EnergyRawData r where r.pointId = :pointId and r.quality = 'GOOD' order by r.ts desc limit 1")
    Optional<EnergyRawData> findLatestByPointId(@Param("pointId") Long pointId);
}
