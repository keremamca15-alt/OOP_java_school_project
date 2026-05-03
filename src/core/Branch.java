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
		setBranchID(branchID);
		setName(name);
		setAddress(address);
	}

	public int getBranchID() {
		return branchID;
	}

	public void setBranchID(int branchID) {
		if (branchID < 0) {
			throw new IllegalArgumentException("Branch ID cannot be negative.");
		}
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
		this.employees = new ArrayList<>();
		if (employees != null) {
			for (Employee employee : employees) {
				addEmployee(employee);
			}
		}
	}

	public ArrayList<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(ArrayList<Vehicle> vehicles) {
		this.vehicles = new ArrayList<>();
		if (vehicles != null) {
			for (Vehicle vehicle : vehicles) {
				addVehicle(vehicle);
			}
		}
	}

	public BranchManager getBranchManager() {
		return branchManager;
	}

	public void setBranchManager(BranchManager branchManager) {
		this.branchManager = branchManager;
		if (branchManager != null && branchManager.getManagedBranch() != this) {
			branchManager.setManagedBranch(this);
		}
	}

	public ArrayList<BranchReport> getBranchReports() {
		return branchReports;
	}

	public void setBranchReports(ArrayList<BranchReport> branchReports) {
		this.branchReports = new ArrayList<>();
		if (branchReports != null) {
			for (BranchReport report : branchReports) {
				addBranchReport(report);
			}
		}
	}

	public void addEmployee(Employee employee) {
		if (employee != null && !employees.contains(employee)) {
			employees.add(employee);
			employee.setBranch(this);
		}
	}

	/**
	 * 
	 * @param vehicle
	 */
	public void addVehicle(Vehicle vehicle) {
		if (vehicle != null && !vehicles.contains(vehicle)) {
			vehicles.add(vehicle);
			vehicle.setBranch(this);
		}
	}

	/**
	 * 
	 * @param vehicleID
	 */
	public void removeVehicle(int vehicleID) {
		for (Vehicle vehicle : vehicles) {
			if (vehicle.getVehicleID() == vehicleID) {
				vehicle.setBranch(null);
				break;
			}
		}
		vehicles.removeIf(vehicle -> vehicle.getVehicleID() == vehicleID);
	}

	public void addBranchReport(BranchReport report) {
		if (report != null && !branchReports.contains(report)) {
			branchReports.add(report);
			report.setBranch(this);
		}
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

	@Override
	public String toString() {
		return name + " (" + address + ")";
	}

}
