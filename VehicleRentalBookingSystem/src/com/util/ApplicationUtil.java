package com.util;

import com.model.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Date;
import java.util.Base64;

public class ApplicationUtil {
    private static final SimpleDateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
    private static final SecureRandom SR = new SecureRandom();

    // Parse input where records separated by comma, tokens by delimiter (e.g., ":")
    public static List<String[]> parseInputData(String input, String delimiter) {
        List<String[]> out = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) return out;
        String[] records = input.split(",");
        for (String r : records) out.add(r.trim().split(delimiter));
        return out;
    }

    // ===== Vehicle parsing (single / batch) =====
    public static List<Vehicle> parseVehicleList(String input) {
        List<Vehicle> list = new ArrayList<>();
        for (String[] t : parseInputData(input, ":")) {
            if (t.length != 9) throw new IllegalArgumentException("Vehicle format requires 9 tokens");
            Vehicle v = new Vehicle();
            v.setVehicleId(t[0].trim());
            v.setMake(t[1].trim());
            v.setModel(t[2].trim());
            v.setYear(Integer.parseInt(t[3].trim()));
            v.setVehicleType(t[4].trim().toUpperCase());
            v.setLicensePlate(t[5].trim().replaceAll("\\s+","").toUpperCase());
            v.setDailyRate(Double.parseDouble(t[6].trim()));
            v.setStatus(t[7].trim().toUpperCase());
            v.setMileage(Double.parseDouble(t[8].trim()));
            list.add(v);
        }
        return list;
    }

    // ===== Customer parsing (batch) =====
    public static List<Customer> parseCustomerList(String input) {
        List<Customer> list = new ArrayList<>();
        for (String[] t : parseInputData(input, ":")) {
            if (t.length != 6 && t.length != 7)
                throw new IllegalArgumentException("Customer format: firstName:lastName:email:phone:address:driverLicenseNumber");
            int idx = 0;
            Customer c = new Customer();
            if (t.length == 7 && t[0].matches("\\d+")) { c.setCustomerId(Integer.parseInt(t[0].trim())); idx = 1; }
            c.setFirstName(t[idx++].trim());
            c.setLastName(t[idx++].trim());
            c.setEmail(t[idx++].trim());
            c.setPhone(t[idx++].trim());
            c.setAddress(t[idx++].trim());
            c.setDriverLicenseNumber(t[idx++].trim());
            list.add(c);
        }
        return list;
    }

    // ===== Booking parsing =====
    public static List<Booking> parseBookingList(String input) {
        List<Booking> list = new ArrayList<>();
        for (String[] t : parseInputData(input, ":")) {
            if (t.length != 7) throw new IllegalArgumentException("Booking format: bookingId:customerId:vehicleId:bookingDate:pickupDate:returnDate:status");
            Booking b = new Booking();
            b.setBookingId(t[0].trim());
            b.setCustomerId(Integer.parseInt(t[1].trim()));
            b.setVehicleId(t[2].trim());
            b.setBookingDate(toSqlDate(t[3].trim()));
            b.setPickupDate(toSqlDate(t[4].trim()));
            b.setReturnDate(toSqlDate(t[5].trim()));
            b.setBookingStatus(t[6].trim().toUpperCase());
            list.add(b);
        }
        return list;
    }

    // ===== Billing parsing =====
    public static List<Billing> parseBillingList(String input) {
        List<Billing> list = new ArrayList<>();
        for (String[] t : parseInputData(input, ":")) {
            if (t.length != 6) throw new IllegalArgumentException("Billing format: invoiceId:bookingId:totalDays:totalAmount:paymentDate:paymentMode");
            Billing b = new Billing();
            b.setInvoiceId(t[0].trim());
            b.setBookingId(t[1].trim());
            b.setTotalDays(Integer.parseInt(t[2].trim()));
            b.setTotalAmount(Double.parseDouble(t[3].trim()));
            b.setPaymentDate(toSqlDate(t[4].trim()));
            b.setPaymentMode(t[5].trim());
            list.add(b);
        }
        return list;
    }

    // ===== Date conversion =====
    public static Date toSqlDate(String s) {
        try {
            java.util.Date d = DF.parse(s);
            return new Date(d.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Date must be dd-MM-yyyy");
        }
    }
    public static String toDisplay(Date d) { if (d==null) return ""; return DF.format(d); }

    // ===== Console helpers =====
    public static String readLine(String prompt) {
        try {
            System.out.print(prompt);
            String line = READER.readLine();
            return (line == null) ? "" : line.trim();
        } catch (Exception e) {
            return "";
        }
    }

    // ===== Hashing + salt =====
    public static String genSalt() {
        byte[] b = new byte[16]; SR.nextBytes(b); return Base64.getEncoder().encodeToString(b);
    }
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte by : dig) sb.append(String.format("%02x", by));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // constant-time equals to avoid timing leaks
    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }

    // Indian license plate validation (simplified)
    public static boolean isValidIndianLicense(String plate) {
        if (plate == null) return false;
        return plate.toUpperCase().matches("^[A-Z]{2}\\d{1,2}[A-Z]{1,2}\\d{4}$");
    }

    // days between pickup and return (exclusive)
    public static int daysBetween(Date from, Date to) {
        long ms = to.getTime() - from.getTime();
        return (int)(ms / (24L*60*60*1000));
    }

    // parse single token helper
    public static String safe(String s) { return s == null ? "" : s.trim(); }
}
