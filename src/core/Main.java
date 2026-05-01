package core;

import java.util.Calendar;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		Date startDate = createDate(2026, Calendar.MAY, 1);
		Date endDate = createDate(2026, Calendar.MAY, 5);

		Reservation reservation = new Reservation(1, startDate, endDate, ReservationStatus.CONFIRMED);
		int days = reservation.calculateDuration();

		Addon childSeat = new Addon(1, "Child Seat", "Daily child seat add-on", 7.5);
		double addonCost = childSeat.calculateCost(days);

		Vehicle vehicle = new SUV(101, "34ABC123", "Toyota", "RAV4", 2024, 80.0);
		vehicle.setInsuranceOption(InsuranceOption.STANDARD);
		vehicle.setMileagePolicy(MileagePolicy.STANDARD);
		vehicle.setStatus(VehicleStatus.AVAILABLE);
		vehicle.setCurrentMileage(14500);
		vehicle.setLastMaintenanceMileage(10000);
		vehicle.setMaintenanceInterval(5000);

		double rentalCost = vehicle.calculateRentalCost(days);
		double distanceToMaintenance = vehicle.calculateDistanceToNextMaintenance();
		boolean needsMaintenance = vehicle.needsMaintenance();

		RentalContract contract = new RentalContract(1, startDate, endDate, ContractStatus.ACTIVE);
		contract.setInitialMileage(14500);
		contract.setFinalMileage(14920);
		int usedMileage = contract.calculateUsedMileage();
		double extraKmCharge = vehicle.getMileagePolicy().calculateExtraCharge(days, usedMileage);

		LoyaltyTier tier = LoyaltyTier.fromPoints(800);

		System.out.println("Reservation days: " + days);
		System.out.println("Addon cost: " + addonCost);
		System.out.println("Vehicle rental cost: " + rentalCost);
		System.out.println("Used mileage: " + usedMileage);
		System.out.println("Extra km charge: " + extraKmCharge);
		System.out.println("Distance to maintenance: " + distanceToMaintenance);
		System.out.println("Needs maintenance: " + needsMaintenance);
		System.out.println("Loyalty tier for 800 points: " + tier);
	}

	private static Date createDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month, day);
		return calendar.getTime();
	}
}
