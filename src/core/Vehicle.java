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
		// TODO - implement Vehicle.calculateRentalCost
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public boolean isAvailable(Date startDate, Date endDate) {
		// TODO - implement Vehicle.isAvailable
		throw new UnsupportedOperationException();
	}

	public double calculateDistanceToNextMaintenance() {
		// TODO - implement Vehicle.calculateDistanceToNextMaintenance
		throw new UnsupportedOperationException();
	}

	public boolean needsMaintenance() {
		// TODO - implement Vehicle.needsMaintenance
		throw new UnsupportedOperationException();
	}

}
