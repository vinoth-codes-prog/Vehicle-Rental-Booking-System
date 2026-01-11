package com.service;
 
import com.management.VehicleManagement;
import com.model.Vehicle;
import com.util.ApplicationUtil;
import java.util.*;
 
public class VehicleService {
private VehicleManagement dao = new VehicleManagement();
 
public List<Vehicle> buildVehicleList(String input) { return ApplicationUtil.parseVehicleList(input); }
public String generateVehicleId() { return "V" + UUID.randomUUID().toString().substring(0,6).toUpperCase(); }
 
public boolean addVehicle(Vehicle v) {
if (!ApplicationUtil.isValidIndianLicense(v.getLicensePlate())) { System.out.println("⚠ Invalid license plate"); return false; }
if (dao.isVehicleExists(v.getVehicleId())) { System.out.println("⚠ VehicleId exists"); return false; }
return dao.insertVehicle(v);
}
public List<Vehicle> getAvailableVehicles() {
VehicleManagement dao = new VehicleManagement();
List<Vehicle> list = dao.getAvailableVehicles();
if (list.isEmpty()) {
System.out.println("⚠ No vehicles available right now.");
}
return list;
}
 
 
public boolean addMultipleVehicles(List<Vehicle> list) { return dao.insertVehicleBatch(list); }
public boolean updateVehicleStatus(String vehicleId, String newStatus) { return dao.updateVehicleStatus(vehicleId, newStatus); }
public boolean deleteVehicle(String vehicleId) { return dao.deleteVehicle(vehicleId); }
public Vehicle getVehicleById(String id) { return dao.getVehicleById(id); }
public List<Vehicle> getVehiclesByType(String type) { return dao.getVehiclesByType(type.toUpperCase()); }
public List<Vehicle> listAvailableVehicles() { return dao.getAvailableVehicles(); }
public List<Vehicle> listAllVehicles() { return dao.getAllVehicles(); }
}