package com.enerpulse.service;

import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.entity.Tariff;
import com.enerpulse.entity.TariffPeriod;
import com.enerpulse.repository.TariffPeriodRepository;
import com.enerpulse.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TariffService {
    private final TariffPeriodRepository periodRepository;
    private final TariffRepository tariffRepository;
    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<TariffPeriod> listPeriods() {
        return periodRepository.findByTenantIdOrderBySortNo(DEFAULT_TENANT_ID);
    }

    public TariffPeriod createPeriod(TariffPeriod period) {
        period.setTenantId(DEFAULT_TENANT_ID);
        return periodRepository.save(period);
    }

    public TariffPeriod updatePeriod(Long id, TariffPeriod period) {
        TariffPeriod existing = getPeriod(id);
        existing.setName(period.getName());
        existing.setPeriodType(period.getPeriodType());
        existing.setStartTime(period.getStartTime());
        existing.setEndTime(period.getEndTime());
        existing.setSortNo(period.getSortNo());
        existing.setStatus(period.getStatus());
        return periodRepository.save(existing);
    }

    public void deletePeriod(Long id) {
        periodRepository.deleteById(id);
    }

    public Map<String, Object> validatePeriods() {
        List<TariffPeriod> periods = periodRepository.findByTenantIdAndStatusOrderBySortNo(DEFAULT_TENANT_ID, "ACTIVE");
        int coveredMinutes = 0;
        boolean overlap = false;
        boolean gap = false;

        List<int[]> intervals = new ArrayList<>();
        for (TariffPeriod p : periods) {
            int start = toMinutes(p.getStartTime());
            int end = toMinutes(p.getEndTime());
            if (end <= start) end += 24 * 60;
            intervals.add(new int[]{start, end});
        }
        intervals.sort(Comparator.comparingInt(a -> a[0]));

        int prevEnd = 0;
        for (int[] iv : intervals) {
            if (iv[0] < prevEnd) overlap = true;
            if (iv[0] > prevEnd) gap = true;
            coveredMinutes += iv[1] - iv[0];
            prevEnd = Math.max(prevEnd, iv[1]);
        }
        if (prevEnd < 24 * 60) gap = true;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("valid", !overlap && !gap && coveredMinutes == 24 * 60);
        result.put("coveredMinutes", coveredMinutes);
        result.put("overlap", overlap);
        result.put("gap", gap);
        return result;
    }

    private int toMinutes(LocalTime t) {
        return t.getHour() * 60 + t.getMinute();
    }

    public TariffPeriod getPeriod(Long id) {
        return periodRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "时段不存在"));
    }

    public List<Tariff> listTariffs() {
        return tariffRepository.findByTenantIdAndStatus(DEFAULT_TENANT_ID, "ACTIVE");
    }

    public Map<String, Object> createTariffCombined(Map<String, Object> body) {
        // Create period first
        TariffPeriod period = new TariffPeriod();
        period.setTenantId(DEFAULT_TENANT_ID);
        period.setName((String) body.get("name"));
        period.setPeriodType((String) body.get("periodType"));
        String startTime = (String) body.get("startTime");
        String endTime = (String) body.get("endTime");
        if (startTime != null) period.setStartTime(LocalTime.parse(startTime));
        if (endTime != null) period.setEndTime(LocalTime.parse(endTime));
        Object sortNo = body.get("sortNo");
        period.setSortNo(sortNo != null ? ((Number) sortNo).intValue() : 0);
        period.setStatus((String) body.getOrDefault("status", "ACTIVE"));
        period = periodRepository.save(period);

        // Create tariff linked to period
        Tariff tariff = new Tariff();
        tariff.setTenantId(DEFAULT_TENANT_ID);
        Object etId = body.get("energyTypeId");
        tariff.setEnergyTypeId(etId != null ? ((Number) etId).longValue() : 1L);
        tariff.setTariffPeriodId(period.getId());
        Object price = body.get("price");
        tariff.setPrice(price != null ? new java.math.BigDecimal(price.toString()) : java.math.BigDecimal.ZERO);
        tariff.setStatus((String) body.getOrDefault("status", "ACTIVE"));
        tariff.setEffectiveFrom(OffsetDateTime.now());
        tariff = tariffRepository.save(tariff);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", tariff.getId());
        result.put("periodId", period.getId());
        result.put("name", period.getName());
        result.put("periodType", period.getPeriodType());
        result.put("startTime", period.getStartTime() != null ? period.getStartTime().toString() : null);
        result.put("endTime", period.getEndTime() != null ? period.getEndTime().toString() : null);
        result.put("energyTypeId", tariff.getEnergyTypeId());
        result.put("price", tariff.getPrice());
        result.put("status", tariff.getStatus());
        return result;
    }

    public List<Map<String, Object>> listTariffsCombined() {
        List<Tariff> tariffs = tariffRepository.findByTenantIdAndStatus(DEFAULT_TENANT_ID, "ACTIVE");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tariff t : tariffs) {
            TariffPeriod p = periodRepository.findById(t.getTariffPeriodId()).orElse(null);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", t.getId());
            item.put("periodId", p != null ? p.getId() : null);
            item.put("name", p != null ? p.getName() : null);
            item.put("periodType", p != null ? p.getPeriodType() : null);
            item.put("startTime", p != null && p.getStartTime() != null ? p.getStartTime().toString() : null);
            item.put("endTime", p != null && p.getEndTime() != null ? p.getEndTime().toString() : null);
            item.put("energyTypeId", t.getEnergyTypeId());
            item.put("price", t.getPrice());
            item.put("status", t.getStatus());
            result.add(item);
        }
        return result;
    }

    public Map<String, Object> updateTariffCombined(Long id, Map<String, Object> body) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "费率不存在"));
        if (tariff.getTariffPeriodId() != null) {
            TariffPeriod period = periodRepository.findById(tariff.getTariffPeriodId()).orElse(null);
            if (period != null) {
                if (body.containsKey("name")) period.setName((String) body.get("name"));
                if (body.containsKey("periodType")) period.setPeriodType((String) body.get("periodType"));
                if (body.containsKey("startTime")) period.setStartTime(LocalTime.parse((String) body.get("startTime")));
                if (body.containsKey("endTime")) period.setEndTime(LocalTime.parse((String) body.get("endTime")));
                if (body.containsKey("status")) period.setStatus((String) body.get("status"));
                periodRepository.save(period);
            }
        }
        if (body.containsKey("energyTypeId")) tariff.setEnergyTypeId(((Number) body.get("energyTypeId")).longValue());
        if (body.containsKey("price")) tariff.setPrice(new java.math.BigDecimal(body.get("price").toString()));
        if (body.containsKey("status")) tariff.setStatus((String) body.get("status"));
        tariffRepository.save(tariff);

        TariffPeriod p = periodRepository.findById(tariff.getTariffPeriodId()).orElse(null);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", tariff.getId());
        result.put("name", p != null ? p.getName() : null);
        result.put("periodType", p != null ? p.getPeriodType() : null);
        result.put("startTime", p != null && p.getStartTime() != null ? p.getStartTime().toString() : null);
        result.put("endTime", p != null && p.getEndTime() != null ? p.getEndTime().toString() : null);
        result.put("energyTypeId", tariff.getEnergyTypeId());
        result.put("price", tariff.getPrice());
        result.put("status", tariff.getStatus());
        return result;
    }

    public Tariff createTariff(Tariff tariff) {
        tariff.setTenantId(DEFAULT_TENANT_ID);
        return tariffRepository.save(tariff);
    }

    public Tariff updateTariff(Long id, Tariff tariff) {
        Tariff existing = tariffRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "费率不存在"));
        existing.setEnergyTypeId(tariff.getEnergyTypeId());
        existing.setTariffPeriodId(tariff.getTariffPeriodId());
        existing.setPrice(tariff.getPrice());
        existing.setCurrency(tariff.getCurrency());
        existing.setEffectiveFrom(tariff.getEffectiveFrom());
        existing.setEffectiveTo(tariff.getEffectiveTo());
        existing.setStatus(tariff.getStatus());
        return tariffRepository.save(existing);
    }

    public void deleteTariff(Long id) {
        tariffRepository.deleteById(id);
    }
}
