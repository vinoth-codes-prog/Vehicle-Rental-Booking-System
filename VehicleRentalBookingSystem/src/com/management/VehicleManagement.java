package com.management;

import com.model.Vehicle;
import java.sql.*;
import java.util.*;

public class VehicleManagement {

    public boolean insertVehicle(Vehicle v) {
        String sql = "INSERT INTO vehicle (vehicleId, make, model, year, vehicleType, licensePlate, dailyRate, status, mileage, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, v.getVehicleId());
            ps.setString(2, v.getMake());
            ps.setString(3, v.getModel());
            ps.setInt(4, v.getYear());
            ps.setString(5, v.getVehicleType());
            ps.setString(6, v.getLicensePlate());
            ps.setDouble(7, v.getDailyRate());
            ps.setString(8, v.getStatus());
            ps.setDouble(9, v.getMileage());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.out.println("❌ Insert Vehicle Error: " + e.getMessage());
            return false;
        }
    }

    public boolean insertVehicleBatch(List<Vehicle> vehicles) {
        String sql = "INSERT INTO vehicle (vehicleId, make, model, year, vehicleType, licensePlate, dailyRate, status, mileage, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (Vehicle v : vehicles) {
                ps.setString(1, v.getVehicleId());
                ps.setString(2, v.getMake());
                ps.setString(3, v.getModel());
                ps.setInt(4, v.getYear());
                ps.setString(5, v.getVehicleType());
                ps.setString(6, v.getLicensePlate());
                ps.setDouble(7, v.getDailyRate());
                ps.setString(8, v.getStatus());
                ps.setDouble(9, v.getMileage());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (Exception e) {
            System.out.println("⚠ Batch Insertion Error: " + e.getMessage());
            return false;
        }
    }

    public boolean isVehicleExists(String vehicleId) {
        String sql = "SELECT vehicleId FROM vehicle WHERE vehicleId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { System.out.println("⚠ Check Vehicle ID Error: " + e.getMessage()); }
        return false;
    }

    public boolean updateVehicleStatus(String vehicleId, String newStatus) {
        String sql = "UPDATE vehicle SET status=?, updatedAt=NOW() WHERE vehicleId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, vehicleId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.out.println("⚠ Update Vehicle Status Error: " + e.getMessage());
            return false;
        }
    }

    public Vehicle getVehicleById(String vehicleId) {
        String sql = "SELECT * FROM vehicle WHERE vehicleId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultToVehicle(rs);
            }
        } catch (Exception e) { System.out.println("⚠ Retrieve Vehicle By ID Error: " + e.getMessage()); }
        return null;
    }

    public List<Vehicle> getVehiclesByType(String type) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicle WHERE vehicleType=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultToVehicle(rs));
            }
        } catch (Exception e) { System.out.println("⚠ Retrieve Vehicle By Type Error: " + e.getMessage()); }
        return list;
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicle WHERE status='AVAILABLE'";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultToVehicle(rs));
        } catch (Exception e) { System.out.println("⚠ Retrieve Available Vehicles Error: " + e.getMessage()); }
        return list;
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicle ORDER BY vehicleId";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultToVehicle(rs));
        } catch (Exception e) { System.out.println("⚠ Retrieve All Vehicles Error: " + e.getMessage()); }
        return list;
    }

    public boolean deleteVehicle(String vehicleId) {
        String sql = "DELETE FROM vehicle WHERE vehicleId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vehicleId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) { System.out.println("⚠ Delete Vehicle Error: " + e.getMessage()); return false; }
    }

    private Vehicle mapResultToVehicle(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getString("vehicleId"));
        v.setMake(rs.getString("make"));
        v.setModel(rs.getString("model"));
        v.setYear(rs.getInt("year"));
        v.setVehicleType(rs.getString("vehicleType"));
        v.setLicensePlate(rs.getString("licensePlate"));
        v.setDailyRate(rs.getDouble("dailyRate"));
        v.setStatus(rs.getString("status"));
        v.setMileage(rs.getDouble("mileage"));
        return v;
    }
}
