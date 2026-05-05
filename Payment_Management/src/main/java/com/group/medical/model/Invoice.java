package com.group.medical.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    private String invoiceId;
    private String patientId;
    private String patientName;
    private String appointmentId;
    private String doctorName;
    private double amount;
    private String status;           // PENDING, PAID, CANCELLED
    private LocalDateTime generatedDate;
    private String paymentType;      // DIRECT or INSURANCE
}