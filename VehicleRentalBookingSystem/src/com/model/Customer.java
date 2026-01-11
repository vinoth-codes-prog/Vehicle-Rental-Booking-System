package com.model;

public class Customer {
    private int customerId;
    private String firstName;
    private String lastName;
    private String email;            // username
    private String phone;
    private String address;
    private String driverLicenseNumber;
    private String passwordHash;     // stored hash
    private String salt;             // base64 salt

    public Customer() {}

    // getters / setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDriverLicenseNumber() { return driverLicenseNumber; }
    public void setDriverLicenseNumber(String driverLicenseNumber) { this.driverLicenseNumber = driverLicenseNumber; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    @Override
    public String toString() {
        return String.format("ID:%d | %s %s | %s | %s | DL:%s",
                customerId, firstName, lastName, email, phone, driverLicenseNumber);
    }
}
