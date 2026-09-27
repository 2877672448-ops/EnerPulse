package com.enerpulse.service;

import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.dto.request.AlarmRuleRequest;
import com.enerpulse.entity.Alarm;
import com.enerpulse.entity.AlarmRule;
import com.enerpulse.repository.AlarmRepository;
import com.enerpulse.repository.AlarmRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmRuleService {
    private final AlarmRuleRepository ruleRepository;
    private final AlarmRepository alarmRepository;
    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<AlarmRule> list() {
        return ruleRepository.findByTenantId(DEFAULT_TENANT_ID);
    }

    public AlarmRule create(AlarmRuleRequest req) {
        AlarmRule rule = new AlarmRule();
        rule.setTenantId(DEFAULT_TENANT_ID);
        rule.setPointId(req.getPointId());
        rule.setRuleName(req.getRuleName());
        rule.setCondition(req.getCondition());
        rule.setThreshold(req.getThreshold());
        rule.setLevel(req.getLevel() != null ? req.getLevel() : "WARNING");
        rule.setEnabled(req.getEnabled() != null ? req.getEnabled() : true);
        return ruleRepository.save(rule);
    }

    public AlarmRule update(Long id, AlarmRuleRequest req) {
        AlarmRule rule = get(id);
        rule.setPointId(req.getPointId());
        rule.setRuleName(req.getRuleName());
        rule.setCondition(req.getCondition());
        rule.setThreshold(req.getThreshold());
        rule.setLevel(req.getLevel() != null ? req.getLevel() : rule.getLevel());
        rule.setEnabled(req.getEnabled() != null ? req.getEnabled() : rule.getEnabled());
        return ruleRepository.save(rule);
    }

    public void delete(Long id) {
        ruleRepository.deleteById(id);
    }

    public AlarmRule get(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "告警规则不存在"));
    }

    /** Check incoming telemetry value against all rules for the point; create alarm if triggered. */
    public void checkAndTrigger(Long pointId, BigDecimal value) {
        List<AlarmRule> rules = ruleRepository.findByPointIdAndEnabledTrue(pointId);
        for (AlarmRule rule : rules) {
            boolean triggered = false;
            switch (rule.getCondition()) {
                case "GT" -> triggered = value.compareTo(rule.getThreshold()) > 0;
                case "GTE" -> triggered = value.compareTo(rule.getThreshold()) >= 0;
                case "LT" -> triggered = value.compareTo(rule.getThreshold()) < 0;
                case "LTE" -> triggered = value.compareTo(rule.getThreshold()) <= 0;
                case "EQ" -> triggered = value.compareTo(rule.getThreshold()) == 0;
            }
            if (triggered) {
                Alarm alarm = new Alarm();
                alarm.setTenantId(DEFAULT_TENANT_ID);
                alarm.setPointId(pointId);
                alarm.setAlarmCode("RULE_" + rule.getId());
                alarm.setAlarmType("THRESHOLD");
                alarm.setLevel(rule.getLevel());
                alarm.setMessage(rule.getRuleName() + ": " + rule.getCondition() + " " + rule.getThreshold());
                alarm.setValue(value);
                alarm.setThreshold(rule.getThreshold());
                alarm.setStatus("ACTIVE");
                alarm.setOccurredAt(OffsetDateTime.now());
                alarmRepository.save(alarm);
                log.info("Alarm triggered by rule {}: value={} threshold={}", rule.getId(), value, rule.getThreshold());
            }
        }
    }
}
