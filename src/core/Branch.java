package core;

import java.util.Date;
import java.util.ArrayList;

public class Branch {

	private int branchID;
	private String name;
	private String address;
	private ArrayList<Employee> employees = new ArrayList<>();
	private ArrayList<Vehicle> vehicles = new ArrayList<>();
	private BranchManager branchManager;
	private ArrayList<BranchReport> branchReports = new ArrayList<>();

	public Branch() {
	}

	public Branch(int branchID, String name, String address) {
		this.branchID = branchID;
		this.name = name;
		this.address = address;
	}

	public int getBranchID() {
		return branchID;
	}

	public void setBranchID(int branchID) {
		this.branchID = branchID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ArrayList<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(ArrayList<Employee> employees) {
		this.employees = employees;
	}

	public ArrayList<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(ArrayList<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public BranchManager getBranchManager() {
		return branchManager;
	}

	public void setBranchManager(BranchManager branchManager) {
		this.branchManager = branchManager;
	}

	public ArrayList<BranchReport> getBranchReports() {
		return branchReports;
	}

	public void setBranchReports(ArrayList<BranchReport> branchReports) {
		this.branchReports = branchReports;
	}

	/**
	 * 
	 * @param vehicle
	 */
	public void addVehicle(Vehicle vehicle) {
		vehicles.add(vehicle);
		vehicle.setBranch(this);
	}

	/**
	 * 
	 * @param vehicleID
	 */
	public void removeVehicle(int vehicleID) {
		vehicles.removeIf(vehicle -> vehicle.getVehicleID() == vehicleID);
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public ArrayList<Vehicle> findAvailableVehicles(Date startDate, Date endDate) {
		ArrayList<Vehicle> availableVehicles = new ArrayList<>();
		for (Vehicle vehicle : vehicles) {
			if (vehicle.isAvailable(startDate, endDate)) {
				availableVehicles.add(vehicle);
			}
		}
		return availableVehicles;
	}

}
