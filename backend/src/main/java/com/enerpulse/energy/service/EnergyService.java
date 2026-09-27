package com.enerpulse.energy.service;

import com.enerpulse.entity.EnergyDaily;
import com.enerpulse.entity.EnergyRawData;
import com.enerpulse.entity.Point;
import com.enerpulse.repository.EnergyDailyRepository;
import com.enerpulse.repository.EnergyRawDataRepository;
import com.enerpulse.repository.PointRepository;
import com.enerpulse.repository.TariffRepository;
import com.enerpulse.entity.Tariff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnergyService {
    private final EnergyRawDataRepository rawDataRepository;
    private final EnergyDailyRepository dailyRepository;
    private final PointRepository pointRepository;
    private final TariffRepository tariffRepository;
    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<Map<String, Object>> history(Long gatewayId, Long deviceId, Long pointId,
                                             Long energyTypeId, String quality,
                                             OffsetDateTime startTime, OffsetDateTime endTime) {
        List<EnergyRawData> data;
        if (pointId != null) {
            data = rawDataRepository.findByPointIdAndTsBetweenOrderByTs(pointId, startTime, endTime);
        } else if (deviceId != null) {
            data = rawDataRepository.findByDeviceIdAndTsBetweenOrderByTs(deviceId, startTime, endTime);
        } else if (gatewayId != null) {
            data = rawDataRepository.findByGatewayIdAndTsBetweenOrderByTs(gatewayId, startTime, endTime);
        } else {
            data = List.of();
        }
        if (quality != null) {
            data = data.stream().filter(d -> quality.equals(d.getQuality())).toList();
        }
        return data.stream().map(d -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("pointId", d.getPointId());
            m.put("timestamp", d.getTs());
            m.put("value", d.getValue());
            m.put("unit", d.getUnit());
            m.put("quality", d.getQuality());
            return m;
        }).toList();
    }

    @Scheduled(cron = "0 5 0 * * ?")
    public void aggregateDaily() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        aggregateForDate(yesterday);
    }

    public void aggregateForDate(LocalDate date) {
        OffsetDateTime start = date.atStartOfDay().atOffset(java.time.ZoneOffset.ofHours(8));
        OffsetDateTime end = date.plusDays(1).atStartOfDay().atOffset(java.time.ZoneOffset.ofHours(8));

        for (Point point : pointRepository.findAll()) {
            if (!"CUMULATIVE".equals(point.getValueType())) continue;
            List<EnergyRawData> raw = rawDataRepository.findByPointIdAndTsBetweenOrderByTs(point.getId(), start, end);
            raw = raw.stream().filter(d -> "GOOD".equals(d.getQuality())).toList();
            if (raw.size() < 2) continue;

            BigDecimal consumption = BigDecimal.ZERO;
            BigDecimal prev = null;
            for (EnergyRawData r : raw) {
                if (prev == null) {
                    prev = r.getValue();
                    continue;
                }
                BigDecimal diff = r.getValue().subtract(prev);
                if (diff.compareTo(BigDecimal.ZERO) >= 0) {
                    consumption = consumption.add(diff);
                }
                prev = r.getValue();
            }

            BigDecimal cost = calculateCost(point, consumption);
            EnergyDaily daily = dailyRepository
                    .findByTenantIdAndPointIdAndPeriodDate(DEFAULT_TENANT_ID, point.getId(), date)
                    .orElseGet(EnergyDaily::new);
            daily.setTenantId(DEFAULT_TENANT_ID);
            daily.setPointId(point.getId());
            daily.setDeviceId(point.getDeviceId());
            daily.setEnergyTypeId(point.getEnergyTypeId());
            daily.setPeriodDate(date);
            daily.setConsumption(consumption);
            daily.setCost(cost);
            dailyRepository.save(daily);
        }
        log.info("Aggregated energy for {}", date);
    }

    private BigDecimal calculateCost(Point point, BigDecimal consumption) {
        if (point.getEnergyTypeId() == null || consumption.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        List<Tariff> tariffs = tariffRepository.findByEnergyTypeIdAndStatus(point.getEnergyTypeId(), "ACTIVE");
        if (tariffs.isEmpty()) return BigDecimal.ZERO;
        BigDecimal avgPrice = tariffs.stream()
                .map(Tariff::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(tariffs.size()), 6, RoundingMode.HALF_UP);
        return consumption.multiply(avgPrice).setScale(6, RoundingMode.HALF_UP);
    }

    public Map<String, Object> analysis(String objectType, Long objectId, Long energyTypeId,
                                        String period, LocalDate startDate, LocalDate endDate) {
        List<EnergyDaily> data = dailyRepository.findByEnergyTypeAndPeriod(DEFAULT_TENANT_ID, energyTypeId, startDate, endDate);

        Map<LocalDate, BigDecimal> consByDate = new LinkedHashMap<>();
        Map<LocalDate, BigDecimal> costByDate = new LinkedHashMap<>();
        for (EnergyDaily d : data) {
            consByDate.merge(d.getPeriodDate(), d.getConsumption(), BigDecimal::add);
            costByDate.merge(d.getPeriodDate(), d.getCost(), BigDecimal::add);
        }

        List<Map<String, Object>> series = new ArrayList<>();
        BigDecimal totalCons = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            BigDecimal c = consByDate.getOrDefault(d, BigDecimal.ZERO);
            BigDecimal co = costByDate.getOrDefault(d, BigDecimal.ZERO);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("date", d.toString());
            m.put("consumption", c);
            m.put("cost", co);
            series.add(m);
            totalCons = totalCons.add(c);
            totalCost = totalCost.add(co);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("series", series);
        result.put("totalConsumption", totalCons);
        result.put("totalCost", totalCost);
        return result;
    }

    public Map<String, Object> ratio(Long energyTypeId, LocalDate startDate, LocalDate endDate) {
        List<EnergyDaily> data = dailyRepository.findByEnergyTypeAndPeriod(DEFAULT_TENANT_ID, energyTypeId, startDate, endDate);
        Map<Long, BigDecimal> byType = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (EnergyDaily d : data) {
            byType.merge(d.getEnergyTypeId(), d.getConsumption(), BigDecimal::add);
            total = total.add(d.getConsumption());
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> e : byType.entrySet()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("energyTypeId", e.getKey());
            m.put("consumption", e.getValue());
            m.put("ratio", total.compareTo(BigDecimal.ZERO) > 0
                    ? e.getValue().divide(total, 6, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            items.add(m);
        }
        return Map.of("items", items);
    }

    public Map<String, Object> yoy(Long energyTypeId, LocalDate startDate, LocalDate endDate) {
        List<EnergyDaily> current = dailyRepository.findByEnergyTypeAndPeriod(DEFAULT_TENANT_ID, energyTypeId, startDate, endDate);
        LocalDate prevStart = startDate.minusYears(1);
        LocalDate prevEnd = endDate.minusYears(1);
        List<EnergyDaily> previous = dailyRepository.findByEnergyTypeAndPeriod(DEFAULT_TENANT_ID, energyTypeId, prevStart, prevEnd);

        BigDecimal currentSum = current.stream().map(EnergyDaily::getConsumption).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal previousSum = previous.stream().map(EnergyDaily::getConsumption).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal diff = currentSum.subtract(previousSum);
        BigDecimal rate = previousSum.compareTo(BigDecimal.ZERO) > 0
                ? diff.divide(previousSum, 6, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("current", currentSum);
        result.put("previous", previousSum);
        result.put("difference", diff);
        result.put("rate", rate);
        return result;
    }

    public Map<String, Object> consumption(Long pointId, Long energyTypeId, String period,
                                           LocalDate startDate, LocalDate endDate) {
        List<EnergyDaily> data = dailyRepository.findByEnergyTypeAndPeriod(DEFAULT_TENANT_ID, energyTypeId, startDate, endDate);
        if (pointId != null) {
            data = data.stream().filter(d -> pointId.equals(d.getPointId())).toList();
        }
        Map<String, BigDecimal> byPeriod = new LinkedHashMap<>();
        for (EnergyDaily d : data) {
            String key = switch (period) {
                case "MONTH" -> d.getPeriodDate().withDayOfMonth(1).toString();
                case "YEAR" -> String.valueOf(d.getPeriodDate().getYear());
                default -> d.getPeriodDate().toString();
            };
            byPeriod.merge(key, d.getConsumption(), BigDecimal::add);
        }
        List<Map<String, Object>> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> e : byPeriod.entrySet()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("period", e.getKey());
            m.put("consumption", e.getValue());
            items.add(m);
            total = total.add(e.getValue());
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", items);
        result.put("total", total);
        return result;
    }
}