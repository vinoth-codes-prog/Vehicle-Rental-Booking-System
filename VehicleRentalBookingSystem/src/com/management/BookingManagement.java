package com.management;

import com.model.Booking;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class BookingManagement {

    public boolean insertBooking(Booking b) {
        String sql = "INSERT INTO booking (bookingId, customerId, vehicleId, bookingDate, pickupDate, returnDate, bookingStatus) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getBookingId());
            ps.setInt(2, b.getCustomerId());
            ps.setString(3, b.getVehicleId());
            ps.setDate(4, b.getBookingDate());
            ps.setDate(5, b.getPickupDate());
            ps.setDate(6, b.getReturnDate());
            ps.setString(7, b.getBookingStatus());
            return ps.executeUpdate() == 1;
        } catch (Exception e) { System.out.println("❌ Insert Booking Error: " + e.getMessage()); return false; }
    }

    public boolean isBookingIdExists(String bookingId) {
        String sql = "SELECT bookingId FROM booking WHERE bookingId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { System.out.println("⚠ Check Booking ID Error: " + e.getMessage()); }
        return false;
    }

    public boolean updateBookingStatus(String bookingId, String newStatus) {
        String sql = "UPDATE booking SET bookingStatus=? WHERE bookingId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, bookingId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) { System.out.println("⚠ Update Booking Status Error: " + e.getMessage()); return false; }
    }

    public Booking getBookingById(String bookingId) {
        String sql = "SELECT * FROM booking WHERE bookingId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking b = new Booking();
                    b.setBookingId(rs.getString("bookingId"));
                    b.setCustomerId(rs.getInt("customerId"));
                    b.setVehicleId(rs.getString("vehicleId"));
                    b.setBookingDate(rs.getDate("bookingDate"));
                    b.setPickupDate(rs.getDate("pickupDate"));
                    b.setReturnDate(rs.getDate("returnDate"));
                    b.setBookingStatus(rs.getString("bookingStatus"));
                    return b;
                }
            }
        } catch (Exception e) { System.out.println("⚠ Retrieve Booking Error: " + e.getMessage()); }
        return null;
    }

    public boolean cancelBooking(String bookingId) {
        String sql = "UPDATE booking SET bookingStatus='CANCELLED' WHERE bookingId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookingId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                updateVehicleAvailability(bookingId, "AVAILABLE");
                return true;
            }
        } catch (Exception e) { System.out.println("⚠ Cancel Booking Error: " + e.getMessage()); }
        return false;
    }

    public List<String> fetchAvailableVehicleIds() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT vehicleId FROM vehicle WHERE status='AVAILABLE'";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString("vehicleId"));
        } catch (Exception e) { System.out.println("⚠ Fetch Vehicles Error: " + e.getMessage()); }
        return list;
    }

    public boolean isVehicleAvailableForDates(String vehicleId, Date pickup, Date ret) {
        String sql = "SELECT 1 FROM booking WHERE vehicleId=? AND bookingStatus='CONFIRMED' AND ( (pickupDate <= ? AND returnDate >= ?) OR (pickupDate <= ? AND returnDate >= ?))";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vehicleId);
            ps.setDate(2, pickup);
            ps.setDate(3, pickup);
            ps.setDate(4, ret);
            ps.setDate(5, ret);
            try (ResultSet rs = ps.executeQuery()) { return !rs.next(); }
        } catch (Exception e) { System.out.println("⚠ Check Vehicle Availability Error: " + e.getMessage()); }
        return false;
    }

    private void updateVehicleAvailability(String bookingId, String newStatus) {
        String sql = "UPDATE vehicle SET status=? WHERE vehicleId=(SELECT vehicleId FROM booking WHERE bookingId=?)";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, bookingId);
            ps.executeUpdate();
        } catch (Exception e) { System.out.println("⚠ Vehicle Availability Update Error: " + e.getMessage()); }
    }
}
