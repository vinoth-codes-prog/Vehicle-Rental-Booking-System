package com.management;

import com.model.Billing;
import java.sql.*;

public class BillingManagement {

    public boolean insertInvoice(Billing bill) {
        String sql = "INSERT INTO billing (invoiceId, bookingId, totalDays, totalAmount, paymentDate, paymentMode) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, bill.getInvoiceId());
            ps.setString(2, bill.getBookingId());
            ps.setInt(3, bill.getTotalDays());
            ps.setDouble(4, bill.getTotalAmount());
            ps.setDate(5, bill.getPaymentDate());
            ps.setString(6, bill.getPaymentMode());

            return ps.executeUpdate() == 1;
        } catch (Exception e) { System.out.println("❌ Insert Invoice Error: " + e.getMessage()); return false; }
    }

    public boolean isInvoiceExists(String invoiceId) {
        String sql = "SELECT invoiceId FROM billing WHERE invoiceId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { System.out.println("⚠ Check Invoice Error: " + e.getMessage()); }
        return false;
    }

    public Billing getBillByBookingId(String bookingId) {
        String sql = "SELECT * FROM billing WHERE bookingId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultToBilling(rs);
            }
        } catch (Exception e) { System.out.println("⚠ Retrieve Bill Error: " + e.getMessage()); }
        return null;
    }

    public Billing getBillByInvoiceId(String invoiceId) {
        String sql = "SELECT * FROM billing WHERE invoiceId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultToBilling(rs);
            }
        } catch (Exception e) { System.out.println("⚠ Retrieve Bill Error: " + e.getMessage()); }
        return null;
    }

    private Billing mapResultToBilling(ResultSet rs) throws SQLException {
        Billing b = new Billing();
        b.setInvoiceId(rs.getString("invoiceId"));
        b.setBookingId(rs.getString("bookingId"));
        b.setTotalDays(rs.getInt("totalDays"));
        b.setTotalAmount(rs.getDouble("totalAmount"));
        b.setPaymentDate(rs.getDate("paymentDate"));
        b.setPaymentMode(rs.getString("paymentMode"));
        return b;
    }
}
