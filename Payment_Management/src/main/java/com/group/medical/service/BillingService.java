package com.group.medical.service;

import com.group.medical.model.Invoice;
import com.group.medical.util.FileUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private static final String BILLING_FILE = "billing.txt";

    public Invoice generateInvoice(String patientId, String patientName, String appointmentId,
                                   String doctorName, double amount, String paymentType) {

        String invoiceId = "INV-" + System.currentTimeMillis();
        String status = "PENDING";
        LocalDateTime now = LocalDateTime.now();

        String line = String.join("|", invoiceId, patientId, patientName, appointmentId,
                doctorName, String.valueOf(amount), status, now.toString(), paymentType);

        FileUtil.appendToFile(BILLING_FILE, line);

        Invoice invoice = new Invoice(invoiceId, patientId, patientName, appointmentId,
                doctorName, amount, status, now, paymentType);

        return invoice;
    }

    public List<Invoice> getAllInvoices() {
        return FileUtil.readAllLines(BILLING_FILE).stream()
                .map(this::parseToInvoice)
                .collect(Collectors.toList());
    }

    public List<Invoice> getPatientInvoices(String patientId) {
        return getAllInvoices().stream()
                .filter(inv -> inv.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public void updatePaymentStatus(String invoiceId, String newStatus) {
        List<String> lines = FileUtil.readAllLines(BILLING_FILE);
        List<String> updatedLines = lines.stream().map(line -> {
            String[] parts = line.split("\\|");
            if (parts[0].equals(invoiceId)) {
                parts[6] = newStatus; // status field
                return String.join("|", parts);
            }
            return line;
        }).collect(Collectors.toList());

        FileUtil.writeAllLines(BILLING_FILE, updatedLines);
    }

    public void deleteInvoice(String invoiceId) {
        List<String> lines = FileUtil.readAllLines(BILLING_FILE);
        List<String> filtered = lines.stream()
                .filter(line -> !line.startsWith(invoiceId + "|"))
                .collect(Collectors.toList());
        FileUtil.writeAllLines(BILLING_FILE, filtered);
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