package com.service;

import com.management.BookingManagement;
import com.model.Booking;
import com.util.ApplicationUtil;
import java.util.*;
import java.sql.Date;

public class BookingService {
    private BookingManagement dao = new BookingManagement();

    public List<Booking> buildBookingList(String input) { return ApplicationUtil.parseBookingList(input); }
    public String generateBookingId() { return "B" + UUID.randomUUID().toString().substring(0,6).toUpperCase(); }

    public boolean createBooking(Booking b) {
        if (!dao.isVehicleAvailableForDates(b.getVehicleId(), b.getPickupDate(), b.getReturnDate())) {
            System.out.println("⚠ Vehicle not available for selected dates."); return false;
        }
        boolean ok = dao.insertBooking(b);
        if (ok) {
            // update vehicle status to BOOKED
            com.management.VehicleManagement vm = new com.management.VehicleManagement();
            vm.updateVehicleStatus(b.getVehicleId(), "BOOKED");
        }
        return ok;
    }

    public boolean checkVehicleAvailability(String vehicleId, Date pickup, Date ret) {
        return dao.isVehicleAvailableForDates(vehicleId, pickup, ret);
    }

    public Booking retrieveBookingById(String id) { return dao.getBookingById(id); }
    public boolean updateBookingStatus(String id, String status) { return dao.updateBookingStatus(id, status); }
    public boolean cancelBooking(String id) { return dao.cancelBooking(id); }
    public long calculateBookingDuration(Date pickup, Date ret) { return ApplicationUtil.daysBetween(pickup, ret); }
    public List<String> fetchAvailableVehicleIds() { return dao.fetchAvailableVehicleIds(); }
}
