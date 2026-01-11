package com.management;

import com.model.Customer;
import java.sql.*;
import java.util.*;

public class CustomerManagement {

    public int insertCustomer(Customer c) {
        String sql = "INSERT INTO customer(firstName,lastName,email,phone,address,driverLicenseNumber,passwordHash,salt,createdAt,updatedAt) "
                   + "VALUES (?,?,?,?,?,?,?,?,NOW(),NOW())";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getLastName());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getAddress());
            ps.setString(6, c.getDriverLicenseNumber());
            ps.setString(7, c.getPasswordHash());
            ps.setString(8, c.getSalt());

            int rows = ps.executeUpdate();
            if (rows == 0) return -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLIntegrityConstraintViolationException icv) {
            System.out.println("⚠ Duplicate email or constraint violation: " + icv.getMessage());
        } catch (Exception e) {
            System.out.println("❌ InsertCustomerError: " + e.getMessage());
        }
        return -1;
    }

    public boolean isCustomerExistsByEmail(String email) {
        String sql = "SELECT customerId FROM customer WHERE email=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { System.out.println("⚠ CheckCustomerByEmailError: " + e.getMessage()); }
        return false;
    }

    public boolean isCustomerExistsById(int id) {
        String sql = "SELECT customerId FROM customer WHERE customerId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { System.out.println("⚠ CheckCustomerByIdError: " + e.getMessage()); }
        return false;
    }

    public boolean updateCustomerInfo(int customerId, String newEmail, String newPhone, String newAddress) {
        String sql = "UPDATE customer SET email=?, phone=?, address=?, updatedAt=NOW() WHERE customerId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newEmail);
            ps.setString(2, newPhone);
            ps.setString(3, newAddress);
            ps.setInt(4, customerId);
            return ps.executeUpdate() == 1;
        } catch (SQLIntegrityConstraintViolationException icv) {
            System.out.println("⚠ Email already in use: " + icv.getMessage());
        } catch (Exception e) {
            System.out.println("⚠ UpdateCustomerInfoError: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customer WHERE customerId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.out.println("⚠ DeleteCustomerError: " + e.getMessage());
            return false;
        }
    }

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customer WHERE customerId=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultToCustomer(rs);
            }
        } catch (Exception e) { System.out.println("⚠ GetCustomerByIdError: " + e.getMessage()); }
        return null;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customer ORDER BY customerId";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultToCustomer(rs));
        } catch (Exception e) { System.out.println("⚠ GetAllCustomersError: " + e.getMessage()); }
        return list;
    }

    public Customer getCustomerByEmail(String email) {
        String sql = "SELECT * FROM customer WHERE email=?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultToCustomer(rs);
            }
        } catch (Exception e) { System.out.println("⚠ GetCustomerByEmailError: " + e.getMessage()); }
        return null;
    }

    private Customer mapResultToCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customerId"));
        c.setFirstName(rs.getString("firstName"));
        c.setLastName(rs.getString("lastName"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        c.setDriverLicenseNumber(rs.getString("driverLicenseNumber"));
        c.setPasswordHash(rs.getString("passwordHash"));
        c.setSalt(rs.getString("salt"));
        return c;
    }
}
