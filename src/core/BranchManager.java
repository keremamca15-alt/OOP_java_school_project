package core;

import java.util.ArrayList;
import java.util.Date;

public class BranchManager extends Employee {

	private Branch managedBranch;
	private ArrayList<BranchReport> generatedReports = new ArrayList<>();

	public BranchManager() {
	}

	public BranchManager(Branch managedBranch) {
		this.managedBranch = managedBranch;
	}

	public Branch getManagedBranch() {
		return managedBranch;
	}

	public void setManagedBranch(Branch managedBranch) {
		this.managedBranch = managedBranch;
	}

	public ArrayList<BranchReport> getGeneratedReports() {
		return generatedReports;
	}

	public void setGeneratedReports(ArrayList<BranchReport> generatedReports) {
		this.generatedReports = generatedReports;
	}

	/**
	 * 
	 * @param employee
	 */
	public void addEmployee(Employee employee) {
		if (managedBranch != null && employee != null) {
			managedBranch.getEmployees().add(employee);
			employee.setBranch(managedBranch);
		}
	}

	/**
	 * 
	 * @param employeeID
	 */
	public void removeEmployee(int employeeID) {
		if (managedBranch == null) {
			return;
		}
		for (Employee employee : managedBranch.getEmployees()) {
			if (employee.getEmployeeID() == employeeID) {
				employee.setBranch(null);
				break;
			}
		}
		managedBranch.getEmployees().removeIf(employee -> employee.getEmployeeID() == employeeID);
	}

	/**
	 * 
	 * @param branch
	 */
	public BranchReport generateReport(Branch branch) {
		if (branch == null) {
			return null;
		}

		BranchReport report = new BranchReport(generatedReports.size() + 1, new Date());
		report.setBranch(branch);
		report.setGeneratedBy(this);
		report.setTotalVehicles(branch.getVehicles().size());

		int availableVehicles = 0;
		int rentedVehicles = 0;
		int totalReservations = 0;

		for (Vehicle vehicle : branch.getVehicles()) {
			if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
				availableVehicles++;
			}
			if (vehicle.getStatus() == VehicleStatus.RENTED) {
				rentedVehicles++;
			}
			totalReservations += vehicle.getReservations().size();
		}

		report.setAvailableVehicles(availableVehicles);
		report.setRentedVehicles(rentedVehicles);
		report.setTotalReservations(totalReservations);
		report.setTotalRevenue(calculateTotalRevenue(branch));

		generatedReports.add(report);
		branch.getBranchReports().add(report);
		return report;
	}

	private double calculateTotalRevenue(Branch branch) {
		double totalRevenue = 0.0;
		for (Vehicle vehicle : branch.getVehicles()) {
			for (Reservation reservation : vehicle.getReservations()) {
				RentalContract contract = reservation.getRentalContract();
				if (contract != null && contract.getInvoice() != null) {
					totalRevenue += contract.getInvoice().calculatePaidAmount();
				}
			}
		}
		return totalRevenue;
	}

}
