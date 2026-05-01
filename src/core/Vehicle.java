package core;

import java.util.Date;
import java.util.ArrayList;

public abstract class Vehicle {

	private int vehicleID;
	private String plateNumber;
	private String brand;
	private String model;
	private int year;
	private double dailyRate;
	private InsuranceOption insuranceOption;
	private MileagePolicy mileagePolicy;
	private VehicleStatus status;
	private int currentMileage;
	private int maintenanceInterval;
	private int lastMaintenanceMileage;
	private Branch branch;
	private ArrayList<Reservation> reservations = new ArrayList<>();
	private ArrayList<MaintenanceTask> maintenanceTasks = new ArrayList<>();
	private ArrayList<DamageAssessment> damageAssessments = new ArrayList<>();

	public Vehicle() {
	}

	public Vehicle(int vehicleID, String plateNumber, String brand, String model, int year, double dailyRate) {
		this.vehicleID = vehicleID;
		this.plateNumber = plateNumber;
		this.brand = brand;
		this.model = model;
		this.year = year;
		this.dailyRate = dailyRate;
	}

	public int getVehicleID() {
		return vehicleID;
	}

	public void setVehicleID(int vehicleID) {
		this.vehicleID = vehicleID;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public double getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(double dailyRate) {
		this.dailyRate = dailyRate;
	}

	public InsuranceOption getInsuranceOption() {
		return insuranceOption;
	}

	public void setInsuranceOption(InsuranceOption insuranceOption) {
		this.insuranceOption = insuranceOption;
	}

	public MileagePolicy getMileagePolicy() {
		return mileagePolicy;
	}

	public void setMileagePolicy(MileagePolicy mileagePolicy) {
		this.mileagePolicy = mileagePolicy;
	}

	public VehicleStatus getStatus() {
		return status;
	}

	public void setStatus(VehicleStatus status) {
		this.status = status;
	}

	public int getCurrentMileage() {
		return currentMileage;
	}

	public void setCurrentMileage(int currentMileage) {
		this.currentMileage = currentMileage;
	}

	public int getMaintenanceInterval() {
		return maintenanceInterval;
	}

	public void setMaintenanceInterval(int maintenanceInterval) {
		this.maintenanceInterval = maintenanceInterval;
	}

	public int getLastMaintenanceMileage() {
		return lastMaintenanceMileage;
	}

	public void setLastMaintenanceMileage(int lastMaintenanceMileage) {
		this.lastMaintenanceMileage = lastMaintenanceMileage;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public ArrayList<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(ArrayList<Reservation> reservations) {
		this.reservations = reservations;
	}

	public ArrayList<MaintenanceTask> getMaintenanceTasks() {
		return maintenanceTasks;
	}

	public void setMaintenanceTasks(ArrayList<MaintenanceTask> maintenanceTasks) {
		this.maintenanceTasks = maintenanceTasks;
	}

	public ArrayList<DamageAssessment> getDamageAssessments() {
		return damageAssessments;
	}

	public void setDamageAssessments(ArrayList<DamageAssessment> damageAssessments) {
		this.damageAssessments = damageAssessments;
	}

	/**
	 * 
	 * @param days
	 */
	public double calculateRentalCost(int days) {
		double insuranceCost = 0.0;
		if (insuranceOption != null) {
			insuranceCost = insuranceOption.calculateCost(days);
		}
		return dailyRate * days + insuranceCost;
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public boolean isAvailable(Date startDate, Date endDate) {
		return false;
	}

	public double calculateDistanceToNextMaintenance() {
		return maintenanceInterval - (currentMileage - lastMaintenanceMileage);
	}

	public boolean needsMaintenance() {
		return calculateDistanceToNextMaintenance() <= 0;
	}

}
