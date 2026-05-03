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
		setVehicleID(vehicleID);
		setPlateNumber(plateNumber);
		setBrand(brand);
		setModel(model);
		setYear(year);
		setDailyRate(dailyRate);
	}

	public int getVehicleID() {
		return vehicleID;
	}

	public void setVehicleID(int vehicleID) {
		if (vehicleID < 0) {
			throw new IllegalArgumentException("Vehicle ID cannot be negative.");
		}
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
		if (year < 0) {
			throw new IllegalArgumentException("Vehicle year cannot be negative.");
		}
		this.year = year;
	}

	public double getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(double dailyRate) {
		if (dailyRate < 0) {
			throw new IllegalArgumentException("Daily rate cannot be negative.");
		}
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
		if (currentMileage < 0) {
			throw new IllegalArgumentException("Current mileage cannot be negative.");
		}
		this.currentMileage = currentMileage;
	}

	public int getMaintenanceInterval() {
		return maintenanceInterval;
	}

	public void setMaintenanceInterval(int maintenanceInterval) {
		if (maintenanceInterval < 0) {
			throw new IllegalArgumentException("Maintenance interval cannot be negative.");
		}
		this.maintenanceInterval = maintenanceInterval;
	}

	public int getLastMaintenanceMileage() {
		return lastMaintenanceMileage;
	}

	public void setLastMaintenanceMileage(int lastMaintenanceMileage) {
		if (lastMaintenanceMileage < 0) {
			throw new IllegalArgumentException("Last maintenance mileage cannot be negative.");
		}
		this.lastMaintenanceMileage = lastMaintenanceMileage;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
		if (branch != null && !branch.getVehicles().contains(this)) {
			branch.getVehicles().add(this);
		}
	}

	public ArrayList<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(ArrayList<Reservation> reservations) {
		this.reservations = new ArrayList<>();
		if (reservations != null) {
			for (Reservation reservation : reservations) {
				if (reservation != null && !this.reservations.contains(reservation)) {
					this.reservations.add(reservation);
					reservation.setVehicle(this);
				}
			}
		}
	}

	public ArrayList<MaintenanceTask> getMaintenanceTasks() {
		return maintenanceTasks;
	}

	public void setMaintenanceTasks(ArrayList<MaintenanceTask> maintenanceTasks) {
		this.maintenanceTasks = new ArrayList<>();
		if (maintenanceTasks != null) {
			for (MaintenanceTask task : maintenanceTasks) {
				if (task != null && !this.maintenanceTasks.contains(task)) {
					this.maintenanceTasks.add(task);
					task.setVehicle(this);
				}
			}
		}
	}

	public ArrayList<DamageAssessment> getDamageAssessments() {
		return damageAssessments;
	}

	public void setDamageAssessments(ArrayList<DamageAssessment> damageAssessments) {
		this.damageAssessments = new ArrayList<>();
		if (damageAssessments != null) {
			for (DamageAssessment assessment : damageAssessments) {
				if (assessment != null && !this.damageAssessments.contains(assessment)) {
					this.damageAssessments.add(assessment);
					assessment.setVehicle(this);
				}
			}
		}
	}

	/**
	 * 
	 * @param days
	 */
	public double calculateRentalCost(int days) {
		if (days < 0) {
			throw new IllegalArgumentException("Days cannot be negative.");
		}
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
		if (startDate == null || endDate == null) {
			throw new IllegalArgumentException("Availability dates cannot be empty.");
		}
		if (!endDate.after(startDate)) {
			throw new IllegalArgumentException("Availability end date must be after start date.");
		}
		if (status == VehicleStatus.IN_MAINTENANCE || status == VehicleStatus.OUT_OF_SERVICE) {
			return false;
		}

		boolean hasKnownRentalPeriod = false;
		for (Reservation reservation : reservations) {
			if (reservation.getStatus() == ReservationStatus.CANCELLED) {
				continue;
			}
			if (isActiveRentalPeriod(reservation)) {
				hasKnownRentalPeriod = true;
			}
			if (datesOverlap(startDate, endDate, reservation.getStartDate(), reservation.getEndDate())) {
				return false;
			}
		}

		if (status == VehicleStatus.RENTED && !hasKnownRentalPeriod) {
			return false;
		}

		return true;
	}

	private boolean isActiveRentalPeriod(Reservation reservation) {
		return reservation.getStatus() == ReservationStatus.CONFIRMED
				|| reservation.getStatus() == ReservationStatus.PENDING
				|| reservation.getStatus() == ReservationStatus.COMPLETED;
	}

	private boolean datesOverlap(Date startDate, Date endDate, Date reservedStartDate, Date reservedEndDate) {
		if (reservedStartDate == null || reservedEndDate == null) {
			throw new IllegalStateException("Reservation dates cannot be empty.");
		}
		if (!reservedEndDate.after(reservedStartDate)) {
			throw new IllegalStateException("Reservation end date must be after start date.");
		}
		return startDate.before(reservedEndDate) && endDate.after(reservedStartDate);
	}

	public double calculateDistanceToNextMaintenance() {
		return maintenanceInterval - (currentMileage - lastMaintenanceMileage);
	}

	public boolean needsMaintenance() {
		return calculateDistanceToNextMaintenance() <= 0;
	}

}
