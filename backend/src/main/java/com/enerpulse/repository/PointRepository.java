package com.enerpulse.repository;

import com.enerpulse.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByGatewayId(Long gatewayId);
    Optional<Point> findByGatewayIdAndPointCode(Long gatewayId, String pointCode);
}
