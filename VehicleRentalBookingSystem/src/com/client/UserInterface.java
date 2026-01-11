package com.client;

import com.model.*;
import com.service.*;
import com.util.ApplicationUtil;

import java.sql.Date;
import java.util.*;

public class UserInterface {
    private static Scanner sc = new Scanner(System.in);
    private static CustomerService customerService = new CustomerService();
    private static VehicleService vehicleService = new VehicleService();
    private static BookingService bookingService = new BookingService();
    private static BillingService billingService = new BillingService();

    public static void main(String[] args) {
        banner();
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            String ch = sc.nextLine().trim();
            if ("1".equals(ch)) loginFlow();
            else if ("2".equals(ch)) {
                int id = customerService.registerCustomerInteractive();
                if (id > 0) System.out.println("Now please login.");
            }
            else if ("3".equals(ch)) { System.out.println("Goodbye!"); break; }
            else System.out.println("Invalid choice");
        }
    }

    private static void banner() {
        System.out.println("\n\033[1;93m==============================================\033[0m");
        System.out.println("\033[1;96m   🚘  WELCOME TO  🌟 VEHICLE RENTAL BOOKING SYSTEM (VRBS) 🌟  🚘\033[0m");
        System.out.println("\033[1;93m==============================================\033[0m\n");
    }

    private static void loginFlow() {
        String email = ApplicationUtil.readLine("Email: ");
        String password = ApplicationUtil.readLine("Password: ");
        // master admin password
        if ("vrbs12345".equals(password)) {
            System.out.println("👑 Admin login granted.");
            adminMenu();
            return;
        }
        Customer c = customerService.login(email, password);
        if (c != null) {
            customerMenu(c);
        } else {
            System.out.println("❌ Invalid credentials or user not found.");
        }
    }

    // ===== Admin menu =====
    private static void adminMenu() {
        while (true) {
            System.out.println("\nADMIN MENU: 1.Vehicle 2.Customer 3.Booking 4.Billing 5.Logout");
            String ch = sc.nextLine().trim();
            if ("1".equals(ch)) adminVehicleMenu();
            else if ("2".equals(ch)) adminCustomerMenu();
            else if ("3".equals(ch)) adminBookingMenu();
            else if ("4".equals(ch)) adminBillingMenu();
            else if ("5".equals(ch)) return;
            else System.out.println("Invalid");
        }
    }

    private static void adminVehicleMenu() {
        System.out.println("\nVehicle: 1.Add 2.Batch Add 3.UpdateStatus 4.Delete 5.ListAll 6.ListByType 7.Back");
        String ch = sc.nextLine().trim();
        if ("1".equals(ch)) {
            String input = ApplicationUtil.readLine("Enter vehicle (vehicleId:make:model:year:type:licensePlate:dailyRate:status:mileage): ");
            List<Vehicle> list = ApplicationUtil.parseVehicleList(input);
            if (!list.isEmpty()) vehicleService.addVehicle(list.get(0));
        } else if ("2".equals(ch)) {
            String input = ApplicationUtil.readLine("Enter multiple vehicles (separated by comma): ");
            List<Vehicle> list = ApplicationUtil.parseVehicleList(input);
            vehicleService.addMultipleVehicles(list);
        } else if ("3".equals(ch)) {
            String id = ApplicationUtil.readLine("Vehicle ID: ");
            String ns = ApplicationUtil.readLine("New status (AVAILABLE/BOOKED/IN_SERVICE): ");
            vehicleService.updateVehicleStatus(id, ns.toUpperCase());
        } else if ("4".equals(ch)) {
            String id = ApplicationUtil.readLine("Vehicle ID to delete: ");
            vehicleService.deleteVehicle(id);
        } else if ("5".equals(ch)) {
            vehicleService.listAllVehicles().forEach(System.out::println);
        } else if ("6".equals(ch)) {
            String type = ApplicationUtil.readLine("Type (CAR/BIKE/VAN): ");
            vehicleService.getVehiclesByType(type).forEach(System.out::println);
        }
    }

    private static void adminCustomerMenu() {
        System.out.println("\nCustomer: 1.ListAll 2.Delete 3.UpdateInfo 4.Back");
        String ch = sc.nextLine().trim();
        if ("1".equals(ch)) customerService.listAllCustomers().forEach(System.out::println);
        else if ("2".equals(ch)) {
            int id = Integer.parseInt(ApplicationUtil.readLine("Customer ID: "));
            customerService.deleteCustomer(id);
        } else if ("3".equals(ch)) {
            int id = Integer.parseInt(ApplicationUtil.readLine("Customer ID: "));
            String email = ApplicationUtil.readLine("New Email: ");
            String phone = ApplicationUtil.readLine("New Phone: ");
            String addr = ApplicationUtil.readLine("New Address: ");
            customerService.updateCustomerInfo(id, email, phone, addr);
        }
    }

    private static void adminBookingMenu() {
        System.out.println("\nBooking: 1.ListAvailableVehicles 2.CreateBooking 3.CancelBooking 4.ListById 5.Back");
        String ch = sc.nextLine().trim();
        if ("1".equals(ch)) bookingService.fetchAvailableVehicleIds().forEach(System.out::println);
        else if ("2".equals(ch)) {
            String cust = ApplicationUtil.readLine("Customer ID: ");
            String veh = ApplicationUtil.readLine("Vehicle ID: ");
            Date pickup = ApplicationUtil.toSqlDate(ApplicationUtil.readLine("Pickup date (dd-MM-yyyy): "));
            Date ret = ApplicationUtil.toSqlDate(ApplicationUtil.readLine("Return date (dd-MM-yyyy): "));
            Booking b = new Booking();
            b.setBookingId(bookingService.generateBookingId());
            b.setCustomerId(Integer.parseInt(cust));
            b.setVehicleId(veh);
            b.setBookingDate(new Date(System.currentTimeMillis()));
            b.setPickupDate(pickup);
            b.setReturnDate(ret);
            b.setBookingStatus("CONFIRMED");
            bookingService.createBooking(b);
        } else if ("3".equals(ch)) {
            String id = ApplicationUtil.readLine("Booking ID to cancel: ");
            bookingService.cancelBooking(id);
        } else if ("4".equals(ch)) {
            String id = ApplicationUtil.readLine("Booking ID: ");
            System.out.println(bookingService.retrieveBookingById(id));
        }
    }

    private static void adminBillingMenu() {
        System.out.println("\nBilling: 1.Create Invoice 2.View Invoice 3.Back");
        String ch = sc.nextLine().trim();
        if ("1".equals(ch)) {
            String bid = ApplicationUtil.readLine("Booking ID: ");
            int days = Integer.parseInt(ApplicationUtil.readLine("Total days: "));
            double rate = Double.parseDouble(ApplicationUtil.readLine("Daily rate: "));
            Billing bill = new Billing();
            bill.setInvoiceId(billingService.generateInvoiceId());
            bill.setBookingId(bid);
            bill.setTotalDays(days);
            bill.setTotalAmount(billingService.calculateBillAmount(days, rate));
            bill.setPaymentDate(new Date(System.currentTimeMillis()));
            bill.setPaymentMode(ApplicationUtil.readLine("Payment mode: "));
            billingService.addInvoice(bill);
        } else if ("2".equals(ch)) {
            String id = ApplicationUtil.readLine("Invoice ID: ");
            System.out.println(billingService.retrieveBillByInvoiceId(id));
        }
    }

    // ===== Customer menu after login =====
    private static void customerMenu(Customer c) {
        while (true) {
            System.out.println("\nCUSTOMER MENU: 1.ListAvailable 2.Book 3.MyBookings 4.Profile 5.Logout");
            String ch = sc.nextLine().trim();
            if ("1".equals(ch)) vehicleService.listAvailableVehicles().forEach(System.out::println);
            else if ("2".equals(ch)) {
                String veh = ApplicationUtil.readLine("Vehicle ID: ");
                Date pickup = ApplicationUtil.toSqlDate(ApplicationUtil.readLine("Pickup date (dd-MM-yyyy): "));
                Date ret = ApplicationUtil.toSqlDate(ApplicationUtil.readLine("Return date (dd-MM-yyyy): "));
                Booking b = new Booking();
                b.setBookingId(bookingService.generateBookingId());
                b.setCustomerId(c.getCustomerId());
                b.setVehicleId(veh);
                b.setBookingDate(new Date(System.currentTimeMillis()));
                b.setPickupDate(pickup);
                b.setReturnDate(ret);
                b.setBookingStatus("CONFIRMED");
                bookingService.createBooking(b);
            } else if ("3".equals(ch)) {
                System.out.println("Feature: view my bookings - use booking ID (future)");
            } else if ("4".equals(ch)) {
                System.out.println(c);
                String upd = ApplicationUtil.readLine("Update profile? (y/n): ");
                if ("y".equalsIgnoreCase(upd)) {
                    String email = ApplicationUtil.readLine("New Email: ");
                    String phone = ApplicationUtil.readLine("New Phone: ");
                    String addr = ApplicationUtil.readLine("New Address: ");
                    customerService.updateCustomerInfo(c.getCustomerId(), email, phone, addr);
                }
            } else if ("5".equals(ch)) {
                System.out.println("Logged out.");
                return;
            }
        }
    }
}
