package core;

import java.util.ArrayList;
import java.util.Date;

public class BranchManager extends Employee {

	private Branch managedBranch;
	private ArrayList<BranchReport> generatedReports = new ArrayList<>();

	public BranchManager() {
	}

	public BranchManager(Branch managedBranch) {
		setManagedBranch(managedBranch);
	}

	public Branch getManagedBranch() {
		return managedBranch;
	}

	public void setManagedBranch(Branch managedBranch) {
		this.managedBranch = managedBranch;
		if (managedBranch != null && managedBranch.getBranchManager() != this) {
			managedBranch.setBranchManager(this);
		}
	}

	public ArrayList<BranchReport> getGeneratedReports() {
		return generatedReports;
	}

	public void setGeneratedReports(ArrayList<BranchReport> generatedReports) {
		this.generatedReports = new ArrayList<>();
		if (generatedReports != null) {
			for (BranchReport report : generatedReports) {
				addGeneratedReport(report);
			}
		}
	}

	/**
	 * 
	 * @param employee
	 */
	public void addEmployee(Employee employee) {
		if (managedBranch == null) {
			throw new IllegalStateException("Branch manager does not manage a branch.");
		}
		if (employee == null) {
			throw new IllegalArgumentException("Employee cannot be empty.");
		}
		managedBranch.addEmployee(employee);
	}

	/**
	 * 
	 * @param employeeID
	 */
	public void removeEmployee(int employeeID) {
		if (managedBranch == null) {
			throw new IllegalStateException("Branch manager does not manage a branch.");
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
			throw new IllegalArgumentException("Report branch cannot be empty.");
		}

		BranchReport report = new BranchReport(createNextReportID(branch), new Date());
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

		addGeneratedReport(report);
		branch.addBranchReport(report);
		return report;
	}

	private double calculateTotalRevenue(Branch branch) {
		double totalRevenue = 0.0;
		ArrayList<Invoice> countedInvoices = new ArrayList<>();
		for (Vehicle vehicle : branch.getVehicles()) {
			for (Reservation reservation : vehicle.getReservations()) {
				RentalContract contract = reservation.getRentalContract();
				if (contract != null && contract.getInvoice() != null
						&& !countedInvoices.contains(contract.getInvoice())) {
					countedInvoices.add(contract.getInvoice());
					totalRevenue += contract.getInvoice().calculatePaidAmount();
				}
			}
		}
		return totalRevenue;
	}

	private void addGeneratedReport(BranchReport report) {
		if (report != null && !generatedReports.contains(report)) {
			generatedReports.add(report);
			report.setGeneratedBy(this);
		}
	}

	private int createNextReportID(Branch branch) {
		int maxReportID = 0;
		for (BranchReport report : generatedReports) {
			if (report.getReportID() > maxReportID) {
				maxReportID = report.getReportID();
			}
		}
		for (BranchReport report : branch.getBranchReports()) {
			if (report.getReportID() > maxReportID) {
				maxReportID = report.getReportID();
			}
		}
		return maxReportID + 1;
	}

}
