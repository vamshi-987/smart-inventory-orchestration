package com.vamshi.stockflow_backend.analytics.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.vamshi.stockflow_backend.analytics.dto.PeakHourResponse;
import com.vamshi.stockflow_backend.analytics.dto.SalesByWarehouseResponse;
import com.vamshi.stockflow_backend.analytics.service.AnalyticsReportService;
import com.vamshi.stockflow_backend.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsReportServiceImpl implements AnalyticsReportService {

    private final AnalyticsService analyticsService;

    @Override
    public byte[] generateSalesByWarehouseCsv(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt) {
        List<SalesByWarehouseResponse> rows = analyticsService.getSalesByWarehouse(warehouseId, fromCreatedAt, toCreatedAt);

        StringBuilder csv = new StringBuilder();
        csv.append("warehouseId,warehouseName,orderCount,totalSales\n");

        for (SalesByWarehouseResponse row : rows) {
            csv.append(safe(row.getWarehouseId() != null ? row.getWarehouseId().toString() : ""))
                    .append(',')
                    .append(safe(row.getWarehouseName()))
                    .append(',')
                    .append(row.getOrderCount() != null ? row.getOrderCount() : 0)
                    .append(',')
                    .append(row.getTotalSales() != null ? row.getTotalSales() : BigDecimal.ZERO)
                    .append('\n');
        }

        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public byte[] generateSalesByWarehousePdf(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt) {
        List<SalesByWarehouseResponse> rows = analyticsService.getSalesByWarehouse(warehouseId, fromCreatedAt, toCreatedAt);
        return buildSalesByWarehousePdf(rows);
    }

    @Override
    public byte[] generatePeakHoursCsv(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt) {
        List<PeakHourResponse> rows = analyticsService.getPeakHours(warehouseId, fromCreatedAt, toCreatedAt);

        StringBuilder csv = new StringBuilder();
        csv.append("hour,orderCount,totalSales\n");

        for (PeakHourResponse row : rows) {
            csv.append(row.getHour() != null ? row.getHour() : 0)
                    .append(',')
                    .append(row.getOrderCount() != null ? row.getOrderCount() : 0)
                    .append(',')
                    .append(row.getTotalSales() != null ? row.getTotalSales() : BigDecimal.ZERO)
                    .append('\n');
        }

        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public byte[] generatePeakHoursPdf(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt) {
        List<PeakHourResponse> rows = analyticsService.getPeakHours(warehouseId, fromCreatedAt, toCreatedAt);
        return buildPeakHoursPdf(rows);
    }

    private byte[] buildSalesByWarehousePdf(List<SalesByWarehouseResponse> rows) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(new Paragraph("Sales By Warehouse Report", titleFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            addHeaderCell(table, "Warehouse ID");
            addHeaderCell(table, "Warehouse Name");
            addHeaderCell(table, "Order Count");
            addHeaderCell(table, "Total Sales");

            for (SalesByWarehouseResponse row : rows) {
                table.addCell(row.getWarehouseId() != null ? row.getWarehouseId().toString() : "");
                table.addCell(row.getWarehouseName() != null ? row.getWarehouseName() : "");
                table.addCell(String.valueOf(row.getOrderCount() != null ? row.getOrderCount() : 0));
                table.addCell(String.valueOf(row.getTotalSales() != null ? row.getTotalSales() : BigDecimal.ZERO));
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | java.io.IOException exception) {
            throw new IllegalStateException("Failed to generate sales by warehouse PDF", exception);
        }
    }

    private byte[] buildPeakHoursPdf(List<PeakHourResponse> rows) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(new Paragraph("Peak Hours Report", titleFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            addHeaderCell(table, "Hour");
            addHeaderCell(table, "Order Count");
            addHeaderCell(table, "Total Sales");

            for (PeakHourResponse row : rows) {
                table.addCell(String.valueOf(row.getHour() != null ? row.getHour() : 0));
                table.addCell(String.valueOf(row.getOrderCount() != null ? row.getOrderCount() : 0));
                table.addCell(String.valueOf(row.getTotalSales() != null ? row.getTotalSales() : BigDecimal.ZERO));
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | java.io.IOException exception) {
            throw new IllegalStateException("Failed to generate peak hours PDF", exception);
        }
    }

    private void addHeaderCell(PdfPTable table, String label) {
        PdfPCell cell = new PdfPCell(new Phrase(label));
        cell.setPadding(6);
        table.addCell(cell);
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}