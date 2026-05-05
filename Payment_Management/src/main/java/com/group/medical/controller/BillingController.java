package com.group.medical.controller;

import com.group.medical.model.Invoice;
import com.group.medical.service.BillingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    // Main Page - Payment History (Billing Summary removed)
    @GetMapping("/history")
    public String paymentHistory(@RequestParam(required = false) String patientId, Model model) {
        if (patientId != null && !patientId.isEmpty()) {
            model.addAttribute("invoices", billingService.getPatientInvoices(patientId));
        } else {
            model.addAttribute("invoices", billingService.getAllInvoices());
        }
        model.addAttribute("patientId", patientId);
        return "billing/payment-history";
    }

    @GetMapping("/generate")
    public String showGeneratePage() {
        return "billing/invoice-generate";
    }

    @PostMapping("/generate")
    public String generateInvoice(@RequestParam String patientId,
                                  @RequestParam String patientName,
                                  @RequestParam String appointmentId,
                                  @RequestParam String doctorName,
                                  @RequestParam double amount,
                                  @RequestParam String paymentType,
                                  Model model) {

        Invoice invoice = billingService.generateInvoice(patientId, patientName, appointmentId, doctorName, amount, paymentType);
        model.addAttribute("success", true);
        model.addAttribute("invoice", invoice);
        return "billing/invoice-generate";
    }

    // Payment Window
    @GetMapping("/pay/{invoiceId}")
    public String showPaymentWindow(@PathVariable String invoiceId, Model model) {
        List<Invoice> invoices = billingService.getAllInvoices();
        Invoice invoice = invoices.stream()
                .filter(i -> i.getInvoiceId().equals(invoiceId))
                .findFirst()
                .orElse(null);

        if (invoice == null) {
            return "redirect:/billing/history";
        }

        model.addAttribute("invoice", invoice);
        return "billing/payment-window";
    }

    // Process Payment + Show Success Message
    @PostMapping("/process-payment")
    public String processPayment(@RequestParam String invoiceId,
                                 @RequestParam String paymentMethod, Model model) {

        billingService.updatePaymentStatus(invoiceId, "PAID");

        model.addAttribute("message", "✅ Payment Successful! Thank you.");
        model.addAttribute("invoices", billingService.getAllInvoices());
        return "billing/payment-history";
    }

    @PostMapping("/delete")
    public String deleteInvoice(@RequestParam String invoiceId) {
        billingService.deleteInvoice(invoiceId);
        return "redirect:/billing/history";
    }
}