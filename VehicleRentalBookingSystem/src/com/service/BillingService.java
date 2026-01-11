package com.service;

import com.management.BillingManagement;
import com.model.Billing;
import java.sql.Date;
import java.util.UUID;

public class BillingService {
    private BillingManagement dao = new BillingManagement();

    public java.util.List<Billing> buildBillingList(String input) { return com.util.ApplicationUtil.parseBillingList(input); }
    public String generateInvoiceId() { return "INV" + UUID.randomUUID().toString().substring(0,6).toUpperCase(); }

    public double calculateBillAmount(long days, double dailyRate) {
        if (days <= 0) days = 1;
        return days * dailyRate;
    }

    public boolean addInvoice(Billing b) {
        if (dao.isInvoiceExists(b.getInvoiceId())) { System.out.println("⚠ Invoice exists"); return false; }
        return dao.insertInvoice(b);
    }

    public Billing retrieveBillByBookingId(String bookingId) { return dao.getBillByBookingId(bookingId); }
    public Billing retrieveBillByInvoiceId(String invoiceId) { return dao.getBillByInvoiceId(invoiceId); }
}
