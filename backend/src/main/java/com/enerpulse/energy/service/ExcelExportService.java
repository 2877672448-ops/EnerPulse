package com.enerpulse.energy.service;

import com.enerpulse.entity.EnergyRawData;
import com.enerpulse.repository.EnergyRawDataRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final EnergyRawDataRepository rawDataRepository;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] exportDeviceData(Long deviceId, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
        List<EnergyRawData> data = rawDataRepository.findByDeviceIdAndTsBetweenOrderByTs(deviceId, startTime, endTime);

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("设备数据");
            CellStyle headerStyle = createHeaderStyle(wb);

            String[] headers = {"测点ID", "时间", "数值", "单位", "质量"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (EnergyRawData d : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(d.getPointId() != null ? d.getPointId() : 0);
                row.createCell(1).setCellValue(d.getTs() != null ? d.getTs().format(DT_FMT) : "");
                row.createCell(2).setCellValue(d.getValue() != null ? d.getValue().doubleValue() : 0);
                row.createCell(3).setCellValue(d.getUnit() != null ? d.getUnit() : "");
                row.createCell(4).setCellValue(d.getQuality() != null ? d.getQuality() : "");
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportHistoryData(Long pointId, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
        List<EnergyRawData> data = rawDataRepository.findByPointIdAndTsBetweenOrderByTs(pointId, startTime, endTime);
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("历史数据");
            CellStyle headerStyle = createHeaderStyle(wb);
            String[] headers = {"时间", "数值", "单位", "质量"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }
            int rowIdx = 1;
            for (EnergyRawData d : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(d.getTs() != null ? d.getTs().format(DT_FMT) : "");
                row.createCell(1).setCellValue(d.getValue() != null ? d.getValue().doubleValue() : 0);
                row.createCell(2).setCellValue(d.getUnit() != null ? d.getUnit() : "");
                row.createCell(3).setCellValue(d.getQuality() != null ? d.getQuality() : "");
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
