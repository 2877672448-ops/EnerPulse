package com.enerpulse.service;

import com.enerpulse.entity.Alarm;
import com.enerpulse.entity.AlarmRule;
import com.enerpulse.repository.AlarmRepository;
import com.enerpulse.repository.AlarmRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmRuleServiceTest {

    @Mock
    private AlarmRuleRepository ruleRepository;

    @Mock
    private AlarmRepository alarmRepository;

    @InjectMocks
    private AlarmRuleService alarmRuleService;

    private AlarmRule gtRule;
    private AlarmRule ltRule;

    @BeforeEach
    void setUp() {
        gtRule = new AlarmRule();
        gtRule.setId(1L);
        gtRule.setPointId(100L);
        gtRule.setRuleName("VoltageHigh");
        gtRule.setCondition("GT");
        gtRule.setThreshold(new BigDecimal("250"));
        gtRule.setLevel("CRITICAL");

        ltRule = new AlarmRule();
        ltRule.setId(2L);
        ltRule.setPointId(100L);
        ltRule.setRuleName("VoltageLow");
        ltRule.setCondition("LT");
        ltRule.setThreshold(new BigDecimal("100"));
        ltRule.setLevel("WARNING");
    }

    @Test
    void checkAndTrigger_GT_triggersAlarmWhenValueExceedsThreshold() {
        when(ruleRepository.findByPointIdAndEnabledTrue(100L)).thenReturn(List.of(gtRule));
        when(alarmRepository.save(any(Alarm.class))).thenAnswer(inv -> inv.getArgument(0));

        alarmRuleService.checkAndTrigger(100L, new BigDecimal("260"));

        ArgumentCaptor<Alarm> captor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmRepository).save(captor.capture());
        Alarm saved = captor.getValue();
        assertEquals("ACTIVE", saved.getStatus());
        assertEquals("CRITICAL", saved.getLevel());
        assertEquals(new BigDecimal("260"), saved.getValue());
    }

    @Test
    void checkAndTrigger_GT_noAlarmWhenValueBelowThreshold() {
        when(ruleRepository.findByPointIdAndEnabledTrue(100L)).thenReturn(List.of(gtRule));

        alarmRuleService.checkAndTrigger(100L, new BigDecimal("240"));

        verify(alarmRepository, never()).save(any());
    }

    @Test
    void checkAndTrigger_LT_triggersAlarmWhenValueBelowThreshold() {
        when(ruleRepository.findByPointIdAndEnabledTrue(100L)).thenReturn(List.of(ltRule));

        alarmRuleService.checkAndTrigger(100L, new BigDecimal("90"));

        verify(alarmRepository, times(1)).save(any(Alarm.class));
    }

    @Test
    void checkAndTrigger_noRules_noAlarm() {
        when(ruleRepository.findByPointIdAndEnabledTrue(100L)).thenReturn(Collections.emptyList());

        alarmRuleService.checkAndTrigger(100L, new BigDecimal("500"));

        verify(alarmRepository, never()).save(any());
    }

    @Test
    void checkAndTrigger_multipleRules_bothTrigger() {
        AlarmRule gteRule = new AlarmRule();
        gteRule.setId(3L);
        gteRule.setPointId(100L);
        gteRule.setCondition("GTE");
        gteRule.setThreshold(new BigDecimal("250"));

        when(ruleRepository.findByPointIdAndEnabledTrue(100L)).thenReturn(List.of(gtRule, gteRule));

        alarmRuleService.checkAndTrigger(100L, new BigDecimal("251"));

        verify(alarmRepository, times(2)).save(any(Alarm.class));
    }
}