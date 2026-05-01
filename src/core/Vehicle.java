package core;

import java.util.Date;

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

	/**
	 * 
	 * @param days
	 */
	public double calculateRentalCost(int days) {
		return 0.0;
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
		return 0.0;
	}

	public boolean needsMaintenance() {
		return false;
	}

}
