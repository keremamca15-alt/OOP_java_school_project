package core;

import java.util.Date;

public class BranchReport {

	private int reportID;
	private Date generatedDate;
	private int totalVehicles;
	private int totalReservations;
	private int availableVehicles;
	private int rentedVehicles;
	private double totalRevenue;
	private Branch branch;
	private BranchManager generatedBy;

	public BranchReport() {
	}

	public BranchReport(int reportID, Date generatedDate) {
		setReportID(reportID);
		setGeneratedDate(generatedDate);
	}

	public int getReportID() {
		return reportID;
	}

	public void setReportID(int reportID) {
		if (reportID < 0) {
			throw new IllegalArgumentException("Report ID cannot be negative.");
		}
		this.reportID = reportID;
	}

	public Date getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}

	public int getTotalVehicles() {
		return totalVehicles;
	}

	public void setTotalVehicles(int totalVehicles) {
		if (totalVehicles < 0) {
			throw new IllegalArgumentException("Total vehicles cannot be negative.");
		}
		this.totalVehicles = totalVehicles;
	}

	public int getTotalReservations() {
		return totalReservations;
	}

	public void setTotalReservations(int totalReservations) {
		if (totalReservations < 0) {
			throw new IllegalArgumentException("Total reservations cannot be negative.");
		}
		this.totalReservations = totalReservations;
	}

	public int getAvailableVehicles() {
		return availableVehicles;
	}

	public void setAvailableVehicles(int availableVehicles) {
		if (availableVehicles < 0) {
			throw new IllegalArgumentException("Available vehicles cannot be negative.");
		}
		this.availableVehicles = availableVehicles;
	}

	public int getRentedVehicles() {
		return rentedVehicles;
	}

	public void setRentedVehicles(int rentedVehicles) {
		if (rentedVehicles < 0) {
			throw new IllegalArgumentException("Rented vehicles cannot be negative.");
		}
		this.rentedVehicles = rentedVehicles;
	}

	public double getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(double totalRevenue) {
		if (totalRevenue < 0) {
			throw new IllegalArgumentException("Total revenue cannot be negative.");
		}
		this.totalRevenue = totalRevenue;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
		if (branch != null && !branch.getBranchReports().contains(this)) {
			branch.getBranchReports().add(this);
		}
	}

	public BranchManager getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(BranchManager generatedBy) {
		this.generatedBy = generatedBy;
		if (generatedBy != null && !generatedBy.getGeneratedReports().contains(this)) {
			generatedBy.getGeneratedReports().add(this);
		}
	}

}
