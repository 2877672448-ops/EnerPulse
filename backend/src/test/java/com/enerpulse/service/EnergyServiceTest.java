package com.enerpulse.service;

import com.enerpulse.entity.EnergyDaily;
import com.enerpulse.repository.EnergyDailyRepository;
import com.enerpulse.repository.EnergyRawDataRepository;
import com.enerpulse.repository.PointRepository;
import com.enerpulse.repository.TariffRepository;
import com.enerpulse.energy.service.EnergyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnergyServiceTest {

    @Mock
    private EnergyRawDataRepository rawDataRepository;
    @Mock
    private EnergyDailyRepository dailyRepository;
    @Mock
    private PointRepository pointRepository;
    @Mock
    private TariffRepository tariffRepository;

    @InjectMocks
    private EnergyService energyService;

    private EnergyDaily d1;
    private EnergyDaily d2;

    @BeforeEach
    void setUp() {
        d1 = new EnergyDaily();
        d1.setPointId(1L);
        d1.setEnergyTypeId(1L);
        d1.setPeriodDate(LocalDate.of(2026, 9, 1));
        d1.setConsumption(new BigDecimal("100"));
        d1.setCost(new BigDecimal("80"));

        d2 = new EnergyDaily();
        d2.setPointId(2L);
        d2.setEnergyTypeId(1L);
        d2.setPeriodDate(LocalDate.of(2026, 9, 2));
        d2.setConsumption(new BigDecimal("150"));
        d2.setCost(new BigDecimal("120"));
    }

    @Test
    void ratio_calculatesCorrectPercentage() {
        when(dailyRepository.findByEnergyTypeAndPeriod(any(), any(), any(), any()))
                .thenReturn(List.of(d1, d2));

        Map<String, Object> result = energyService.ratio(1L,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        List<?> items = (List<?>) result.get("items");
        assertEquals(1, items.size());
        Map<?, ?> item = (Map<?, ?>) items.get(0);
        assertEquals(new BigDecimal("250"), item.get("consumption"));
        assertEquals(new BigDecimal("1.000000"), item.get("ratio"));
    }

    @Test
    void yoy_calculatesGrowthRate() {
        EnergyDaily prev = new EnergyDaily();
        prev.setEnergyTypeId(1L);
        prev.setPeriodDate(LocalDate.of(2025, 9, 1));
        prev.setConsumption(new BigDecimal("200"));

        when(dailyRepository.findByEnergyTypeAndPeriod(any(), any(), any(), any()))
                .thenReturn(List.of(d1, d2))
                .thenReturn(List.of(prev));

        Map<String, Object> result = energyService.yoy(1L,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        assertEquals(new BigDecimal("250"), result.get("current"));
        assertEquals(new BigDecimal("200"), result.get("previous"));
        assertEquals(new BigDecimal("50"), result.get("difference"));
        assertEquals(new BigDecimal("0.250000"), result.get("rate"));
    }

    @Test
    void yoy_noPreviousData_rateIsZero() {
        when(dailyRepository.findByEnergyTypeAndPeriod(any(), any(), any(), any()))
                .thenReturn(List.of(d1))
                .thenReturn(List.of());

        Map<String, Object> result = energyService.yoy(1L,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        assertEquals(new BigDecimal("100"), result.get("current"));
        assertEquals(BigDecimal.ZERO, result.get("previous"));
        assertEquals(BigDecimal.ZERO, result.get("rate"));
    }

    @Test
    void consumption_aggregatesByDay() {
        when(dailyRepository.findByEnergyTypeAndPeriod(any(), any(), any(), any()))
                .thenReturn(List.of(d1, d2));

        Map<String, Object> result = energyService.consumption(null, 1L, "DAY",
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        assertEquals(new BigDecimal("250"), result.get("total"));
    }
}