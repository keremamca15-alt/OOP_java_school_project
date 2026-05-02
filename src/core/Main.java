package core;

import java.util.Calendar;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		Date startDate = createDate(2026, Calendar.MAY, 1);
		Date endDate = createDate(2026, Calendar.MAY, 5);

		Reservation reservation = new Reservation(1, startDate, endDate, ReservationStatus.CONFIRMED);
		int days = reservation.calculateDuration();
		reservation.cancelReservation();
		ReservationStatus cancelledStatus = reservation.getStatus();
		reservation.confirmReservation();
		ReservationStatus confirmedStatus = reservation.getStatus();

		Addon childSeat = new Addon(1, "Child Seat", "Daily child seat add-on", 7.5);
		double addonCost = childSeat.calculateCost(days);

		Vehicle vehicle = new SUV(101, "35ABC123", "Toyota", "RAV4", 2024, 80.0);
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
		Payment validPayment = new Payment(1, 120.0, startDate, PaymentPurpose.PREPAYMENT, 1);
		Payment invalidPayment = new Payment(2, 0.0, startDate, PaymentPurpose.DEPOSIT, 1);

		Branch branch = new Branch(1, "Bornova Branch", "Bornova, Izmir");
		Vehicle secondVehicle = new Economy(102, "35XYZ987", "Renault", "Clio", 2023, 45.0);
		secondVehicle.setStatus(VehicleStatus.AVAILABLE);
		branch.addVehicle(vehicle);
		branch.addVehicle(secondVehicle);
		reservation.setVehicle(vehicle);
		vehicle.getReservations().add(reservation);

		Date searchStartDate = createDate(2026, Calendar.MAY, 2);
		Date searchEndDate = createDate(2026, Calendar.MAY, 4);

		Customer customer = new Customer(1, "Ayse", "Demir", "ayse@example.com", 800, tier);
		Invoice invoice = new Invoice(1, 300.0, 20.0, addonCost);
		invoice.applyDiscount(customer);
		double invoiceTotal = invoice.calculateTotal();
		Payment invoicePayment = new Payment(3, 200.0, startDate, PaymentPurpose.ADDITIONAL_CHARGE, 1);
		invoice.addPayment(invoicePayment);

		BranchManager branchManager = new BranchManager(branch);
		Employee employee = new RentalAgent();
		employee.setEmployeeID(10);
		branchManager.addEmployee(employee);
		contract.setInvoice(invoice);
		reservation.setRentalContract(contract);
		BranchReport report = branchManager.generateReport(branch);

		RentalAgent rentalAgent = new RentalAgent();
		rentalAgent.processPickup(reservation);
		Invoice returnInvoice = rentalAgent.processReturn(reservation);
		DamageAssessment assessment = rentalAgent.assessDamage(vehicle, "Small bumper scratch", 250.0);

		System.out.println("Reservation days: " + days);
		System.out.println("Reservation status after cancel: " + cancelledStatus);
		System.out.println("Reservation status after confirm: " + confirmedStatus);
		System.out.println("Addon cost: " + addonCost);
		System.out.println("Vehicle rental cost: " + rentalCost);
		System.out.println("Used mileage: " + usedMileage);
		System.out.println("Extra km charge: " + extraKmCharge);
		System.out.println("Valid payment processed: " + validPayment.processPayment());
		System.out.println("Invalid payment processed: " + invalidPayment.processPayment());
		System.out.println("Distance to maintenance: " + distanceToMaintenance);
		System.out.println("Needs maintenance: " + needsMaintenance);
		System.out.println("Loyalty tier for 800 points: " + tier);
		System.out.println("Available vehicles for search range: " + branch.findAvailableVehicles(searchStartDate, searchEndDate).size());
		System.out.println("Invoice discount: " + invoice.getDiscountAmount());
		System.out.println("Invoice total: " + invoiceTotal);
		System.out.println("Invoice paid amount: " + invoice.calculatePaidAmount());
		System.out.println("Invoice remaining amount: " + invoice.calculateRemainingAmount());
		System.out.println("Branch employee count: " + branch.getEmployees().size());
		System.out.println("Branch report total vehicles: " + report.getTotalVehicles());
		System.out.println("Branch report total revenue: " + report.getTotalRevenue());
		System.out.println("Return invoice created: " + (returnInvoice != null));
		System.out.println("Damage assessment created: " + (assessment != null));
		System.out.println("Damage assessment cost: " + assessment.getDamageCost());
	}

	private static Date createDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month, day);
		return calendar.getTime();
	}
}
