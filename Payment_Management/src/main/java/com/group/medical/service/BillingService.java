package com.group.medical.service;

import com.group.medical.model.Invoice;
import com.group.medical.util.FileUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private static final String BILLING_FILE = "billing.txt";

    // Internal pipe-delimited format stored in file
    private static final String DELIMITER = "|";

    // Human-readable date format for the formatted block
    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    public Invoice generateInvoice(String patientId, String patientName, String appointmentId,
                                   String doctorName, double amount, String paymentType) {

        String invoiceId = "INV-" + System.currentTimeMillis();
        String status    = "PENDING";
        LocalDateTime now = LocalDateTime.now();

        // ── Write clearly formatted block to billing.txt ──────────────────────
        String separator = "=".repeat(60);
        String block = String.join(System.lineSeparator(),
                separator,
                "  INVOICE RECORD",
                separator,
                "  Invoice ID     : " + invoiceId,
                "  Patient ID     : " + patientId,
                "  Patient Name   : " + patientName,
                "  Appointment ID : " + appointmentId,
                "  Doctor Name    : " + doctorName,
                "  Amount (LKR)   : " + String.format("%.2f", amount),
                "  Payment Type   : " + paymentType,
                "  Status         : " + status,
                "  Generated Date : " + now.format(DISPLAY_FMT),
                separator
        );

        // Append the formatted block (human-readable)
        FileUtil.appendFormattedBlock(BILLING_FILE, block);

        // Also append the pipe-delimited data line (used for reading/updating)
        String dataLine = String.join(DELIMITER, invoiceId, patientId, patientName,
                appointmentId, doctorName, String.valueOf(amount), status, now.toString(), paymentType);
        FileUtil.appendToFile(BILLING_FILE, dataLine);

        return new Invoice(invoiceId, patientId, patientName, appointmentId,
                doctorName, amount, status, now, paymentType);
    }

    public List<Invoice> getAllInvoices() {
        return FileUtil.readAllLines(BILLING_FILE).stream()
                .filter(line -> line.contains(DELIMITER))
                .map(this::parseToInvoice)
                .collect(Collectors.toList());
    }

    public List<Invoice> getPatientInvoices(String patientId) {
        return getAllInvoices().stream()
                .filter(inv -> inv.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public void updatePaymentStatus(String invoiceId, String newStatus) {
        // Update the pipe line for lookup
        List<String> lines = FileUtil.readAllLines(BILLING_FILE);
        boolean updated = false;

        List<String> updatedLines = lines.stream().map(line -> {
            if (!line.contains(DELIMITER)) return line;
            String[] parts = line.split("\\|");
            if (parts[0].equals(invoiceId)) {
                parts[6] = newStatus;
                return String.join(DELIMITER, parts);
            }
            return line;
        }).collect(Collectors.toList());

        FileUtil.writeAllLines(BILLING_FILE, updatedLines);

        // Also update status in the formatted block
        updateStatusInFormattedBlock(invoiceId, newStatus);
    }

    private void updateStatusInFormattedBlock(String invoiceId, String newStatus) {
        List<String> raw = FileUtil.readRawLines(BILLING_FILE);
        List<String> updated = raw.stream().map(line -> {
            if (line.trim().startsWith("Invoice ID     : " + invoiceId)) {
                // found the invoice block; mark for next status line replacement
            }
            return line;
        }).collect(Collectors.toList());

        // Simple approach: replace "  Status         : PENDING/PAID/CANCELLED" near the invoiceId block
        boolean inBlock = false;
        for (int i = 0; i < raw.size(); i++) {
            if (raw.get(i).trim().startsWith("Invoice ID     : " + invoiceId)) {
                inBlock = true;
            }
            if (inBlock && raw.get(i).trim().startsWith("Status         :")) {
                raw.set(i, "  Status         : " + newStatus);
                break;
            }
        }
        FileUtil.writeRawLines(BILLING_FILE, raw);
    }

    public void deleteInvoice(String invoiceId) {
        // Remove the pipe line
        List<String> lines = FileUtil.readAllLines(BILLING_FILE);
        List<String> filtered = lines.stream()
                .filter(line -> !line.startsWith(invoiceId + DELIMITER))
                .collect(Collectors.toList());
        FileUtil.writeAllLines(BILLING_FILE, filtered);

        // Remove the formatted block
        List<String> raw = FileUtil.readRawLines(BILLING_FILE);
        List<String> cleanedRaw = new java.util.ArrayList<>();
        boolean inTargetBlock = false;
        boolean skipNext = false;
        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i);
            // Detect start of a block that contains this invoiceId
            if (i + 12 < raw.size() && raw.get(i + 2) != null &&
                    raw.get(i + 2).trim().startsWith("Invoice ID     : " + invoiceId)) {
                inTargetBlock = true;
            }
            if (inTargetBlock) {
                // Skip until end of block (the second separator line)
                if (line.trim().startsWith("===") && !cleanedRaw.isEmpty() &&
                        cleanedRaw.get(cleanedRaw.size() - 1).trim().startsWith("Generated Date")) {
                    inTargetBlock = false; // skip closing separator too
                    skipNext = true;       // skip the blank line after block
                    continue;
                }
                continue;
            }
            if (skipNext && line.trim().isEmpty()) { skipNext = false; continue; }
            cleanedRaw.add(line);
        }
        FileUtil.writeRawLines(BILLING_FILE, cleanedRaw);
    }

    private Invoice parseToInvoice(String line) {
        String[] p = line.split("\\|");
        Invoice inv = new Invoice();
        inv.setInvoiceId(p[0]);
        inv.setPatientId(p[1]);
        inv.setPatientName(p[2]);
        inv.setAppointmentId(p[3]);
        inv.setDoctorName(p[4]);
        inv.setAmount(Double.parseDouble(p[5]));
        inv.setStatus(p[6]);
        inv.setGeneratedDate(LocalDateTime.parse(p[7]));
        inv.setPaymentType(p.length > 8 ? p[8] : "DIRECT");
        return inv;
    }
}