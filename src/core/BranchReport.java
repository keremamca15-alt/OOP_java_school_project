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
		this.reportID = reportID;
		this.generatedDate = generatedDate;
	}

	public int getReportID() {
		return reportID;
	}

	public void setReportID(int reportID) {
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
		this.totalVehicles = totalVehicles;
	}

	public int getTotalReservations() {
		return totalReservations;
	}

	public void setTotalReservations(int totalReservations) {
		this.totalReservations = totalReservations;
	}

	public int getAvailableVehicles() {
		return availableVehicles;
	}

	public void setAvailableVehicles(int availableVehicles) {
		this.availableVehicles = availableVehicles;
	}

	public int getRentedVehicles() {
		return rentedVehicles;
	}

	public void setRentedVehicles(int rentedVehicles) {
		this.rentedVehicles = rentedVehicles;
	}

	public double getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public BranchManager getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(BranchManager generatedBy) {
		this.generatedBy = generatedBy;
	}

}
