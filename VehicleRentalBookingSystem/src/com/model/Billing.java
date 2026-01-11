package com.model;

import java.sql.Date;

public class Billing {
    private String invoiceId;
    private String bookingId;
    private int totalDays;
    private double totalAmount;
    private Date paymentDate;
    private String paymentMode;

    public Billing() {}

    // getters/setters
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    @Override
    public String toString() {
        return String.format("%s | Booking:%s | %d days | ₹%.2f | %s | %s",
                invoiceId, bookingId, totalDays, totalAmount,
                paymentMode, paymentDate);
    }
}
