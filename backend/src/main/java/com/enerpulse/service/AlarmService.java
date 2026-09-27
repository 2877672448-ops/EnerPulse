package com.enerpulse.service;

import com.enerpulse.common.api.PageResult;
import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.entity.Alarm;
import com.enerpulse.repository.AlarmRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public PageResult<Alarm> list(int page, int pageSize, Long gatewayId, Long deviceId, Long pointId,
                                  String level, String status, OffsetDateTime startTime, OffsetDateTime endTime) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "occurredAt"));
        Specification<Alarm> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            if (gatewayId != null) preds.add(cb.equal(root.get("gatewayId"), gatewayId));
            if (deviceId != null) preds.add(cb.equal(root.get("deviceId"), deviceId));
            if (pointId != null) preds.add(cb.equal(root.get("pointId"), pointId));
            if (level != null) preds.add(cb.equal(root.get("level"), level));
            if (status != null) preds.add(cb.equal(root.get("status"), status));
            if (startTime != null) preds.add(cb.greaterThanOrEqualTo(root.get("occurredAt"), startTime));
            if (endTime != null) preds.add(cb.lessThanOrEqualTo(root.get("occurredAt"), endTime));
            return cb.and(preds.toArray(new Predicate[0]));
        };
        Page<Alarm> p = alarmRepository.findAll(spec, pageable);
        return new PageResult<>(p.getContent(), page, pageSize, p.getTotalElements());
    }

    public Alarm ack(Long id, Long userId) {
        Alarm a = get(id);
        a.setStatus("ACKNOWLEDGED");
        a.setAckBy(userId);
        a.setAckAt(OffsetDateTime.now());
        return alarmRepository.save(a);
    }

    public Alarm recover(Long id) {
        Alarm a = get(id);
        a.setStatus("RECOVERED");
        a.setRecoveredAt(OffsetDateTime.now());
        return alarmRepository.save(a);
    }

    public Alarm close(Long id, Long userId) {
        Alarm a = get(id);
        a.setStatus("CLOSED");
        a.setClosedBy(userId);
        a.setClosedAt(OffsetDateTime.now());
        return alarmRepository.save(a);
    }

    public Alarm get(Long id) {
        return alarmRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "告警不存在"));
    }
}
