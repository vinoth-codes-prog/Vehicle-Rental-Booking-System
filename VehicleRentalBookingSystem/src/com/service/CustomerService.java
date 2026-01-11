package com.service;

import com.management.CustomerManagement;
import com.model.Customer;
import com.util.ApplicationUtil;
import java.util.List;

public class CustomerService {
    private CustomerManagement dao = new CustomerManagement();

    public List<Customer> buildCustomerList(String input) { return ApplicationUtil.parseCustomerList(input); }

    public int registerCustomerInteractive() {
        try {
            String firstName = ApplicationUtil.readLine("First name: ");
            String lastName = ApplicationUtil.readLine("Last name: ");
            String email = ApplicationUtil.readLine("Email (will be username): ");
            String phone = ApplicationUtil.readLine("Phone (10 digits): ");
            String address = ApplicationUtil.readLine("Address: ");
            String dl = ApplicationUtil.readLine("Driver License No: ");
            String password = ApplicationUtil.readLine("Choose password (min 8 chars): ");
            if (password == null || password.length() < 8) {
                System.out.println("⚠ Password must be at least 8 characters."); return -1;
            }
            if (dao.isCustomerExistsByEmail(email)) { System.out.println("⚠ Email already registered. Please login."); return -1; }

            String salt = ApplicationUtil.genSalt();
            String hash = ApplicationUtil.sha256(salt + password);

            Customer c = new Customer();
            c.setFirstName(firstName);
            c.setLastName(lastName);
            c.setEmail(email);
            c.setPhone(phone);
            c.setAddress(address);
            c.setDriverLicenseNumber(dl);
            c.setSalt(salt);
            c.setPasswordHash(hash);

            int id = dao.insertCustomer(c);
            if (id > 0) System.out.println("✅ Registered (Customer ID: " + id + ")");
            return id;
        } catch (Exception e) { System.out.println("❌ Registration error: " + e.getMessage()); return -1; }
    }

    public int registerCustomer(Customer c, String plainPassword) {
        if (dao.isCustomerExistsByEmail(c.getEmail())) { System.out.println("⚠ Email already exists"); return -1; }
        String salt = ApplicationUtil.genSalt();
        String hash = ApplicationUtil.sha256(salt + plainPassword);
        c.setSalt(salt); c.setPasswordHash(hash);
        return dao.insertCustomer(c);
    }

    public Customer login(String email, String password) {
        if (email == null || email.isEmpty()) return null;
        Customer stored = dao.getCustomerByEmail(email);
        if (stored == null) return null;
        String computed = ApplicationUtil.sha256(stored.getSalt() + password);
        if (ApplicationUtil.constantTimeEquals(stored.getPasswordHash(), computed)) {
            System.out.println("✅ Login successful. Welcome " + stored.getFirstName());
            return stored;
        } else return null;
    }

    public boolean updateCustomerInfo(int id, String email, String phone, String address) { return dao.updateCustomerInfo(id, email, phone, address); }
    public boolean deleteCustomer(int id) { return dao.deleteCustomer(id); }
    public Customer getCustomerById(int id) { return dao.getCustomerById(id); }
    public List<Customer> listAllCustomers() { return dao.getAllCustomers(); }
}
