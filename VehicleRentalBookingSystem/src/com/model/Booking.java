package com.model;

import java.sql.Date;

public class Booking {
    private String bookingId;
    private int customerId;
    private String vehicleId;
    private Date bookingDate;
    private Date pickupDate;
    private Date returnDate;
    private String bookingStatus; // CONFIRMED, CANCELLED, COMPLETED

    public Booking() {}

    // getters/setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public Date getPickupDate() { return pickupDate; }
    public void setPickupDate(Date pickupDate) { this.pickupDate = pickupDate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    @Override
    public String toString() {
        return String.format("%s | Cust:%d | Veh:%s | %s -> %s | %s",
                bookingId, customerId, vehicleId,
                bookingDate, pickupDate, bookingStatus);
    }
}
