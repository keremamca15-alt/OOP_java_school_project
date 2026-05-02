package core;

import java.util.Calendar;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		Date startDate = createDate(2026, Calendar.MAY, 1);
		Date endDate = createDate(2026, Calendar.MAY, 5);
		Date searchStartDate = createDate(2026, Calendar.MAY, 2);
		Date searchEndDate = createDate(2026, Calendar.MAY, 4);

		Branch branch = new Branch(1, "Bornova Branch", "Bornova, Izmir");
		Vehicle suv = new SUV(101, "35ABC123", "Toyota", "RAV4", 2024, 80.0);
		Vehicle economy = new Economy(102, "35XYZ987", "Renault", "Clio", 2023, 45.0);
		prepareVehicle(suv);
		economy.setStatus(VehicleStatus.AVAILABLE);
		branch.addVehicle(suv);
		branch.addVehicle(economy);

		Reservation reservation = new Reservation(1, startDate, endDate, ReservationStatus.CONFIRMED);
		reservation.setVehicle(suv);
		suv.getReservations().add(reservation);

		int days = reservation.calculateDuration();
		Addon childSeat = new Addon(1, "Child Seat", "Daily child seat add-on", 7.5);
		double addonCost = childSeat.calculateCost(days);
		double rentalCost = suv.calculateRentalCost(days);
		double distanceToMaintenance = suv.calculateDistanceToNextMaintenance();
		boolean needsMaintenance = suv.needsMaintenance();

		RentalContract contract = new RentalContract(1, startDate, endDate, ContractStatus.ACTIVE);
		contract.setInitialMileage(14500);
		contract.setFinalMileage(14920);
		contract.setInvoice(createPaidInvoice(startDate, addonCost));
		reservation.setRentalContract(contract);
		int usedMileage = contract.calculateUsedMileage();
		double extraKmCharge = suv.getMileagePolicy().calculateExtraCharge(days, usedMileage);

		Customer customer = new Customer(1, "Ayse", "Demir", "ayse@example.com", 800, LoyaltyTier.fromPoints(800));
		Reservation customerReservation = customer.makeReservation(2, economy, searchStartDate, searchEndDate);
		customer.earnPoints(500.0);

		Payment validPayment = new Payment(1, 120.0, startDate, PaymentPurpose.PREPAYMENT, 1);
		Payment invalidPayment = new Payment(2, 0.0, startDate, PaymentPurpose.DEPOSIT, 1);

		BranchManager branchManager = new BranchManager(branch);
		Employee employee = new RentalAgent();
		employee.setEmployeeID(10);
		branchManager.addEmployee(employee);
		int employeeCountAfterAdd = branch.getEmployees().size();
		branchManager.removeEmployee(10);
		int employeeCountAfterRemove = branch.getEmployees().size();
		branchManager.addEmployee(employee);
		BranchReport report = branchManager.generateReport(branch);

		RentalAgent rentalAgent = new RentalAgent();
		rentalAgent.processPickup(reservation);
		DamageAssessment assessment = rentalAgent.assessDamage(suv, "Small bumper scratch", 250.0);
		Invoice returnInvoice = rentalAgent.processReturn(reservation, assessment);

		MaintenanceTask maintenanceTask = new MaintenanceTask();
		maintenanceTask.scheduleMaintenance();
		MaintenanceStatus scheduledStatus = maintenanceTask.getStatus();
		maintenanceTask.completeMaintenance();

		System.out.println("== Reservation ==");
		System.out.println("Reservation days: " + days);
		reservation.cancelReservation();
		System.out.println("Reservation status after cancel: " + reservation.getStatus());
		reservation.confirmReservation();
		System.out.println("Reservation status after confirm: " + reservation.getStatus());
		System.out.println("Available vehicles for search range: " + branch.findAvailableVehicles(searchStartDate, searchEndDate).size());

		System.out.println("\n== Costs ==");
		System.out.println("Addon cost: " + addonCost);
		System.out.println("Vehicle rental cost: " + rentalCost);
		System.out.println("Used mileage: " + usedMileage);
		System.out.println("Extra km charge: " + extraKmCharge);
		System.out.println("Return invoice total: " + returnInvoice.calculateTotal());

		System.out.println("\n== Payments and Loyalty ==");
		System.out.println("Valid payment processed: " + validPayment.processPayment());
		System.out.println("Invalid payment processed: " + invalidPayment.processPayment());
		System.out.println("Customer reservation count: " + customer.viewMyReservations().size());
		System.out.println("Customer new loyalty points: " + customer.getLoyaltyPoints());
		System.out.println("Customer loyalty tier: " + customer.getLoyaltyTier());
		System.out.println("Customer reservation vehicle plate: " + customerReservation.getVehicle().getPlateNumber());

		System.out.println("\n== Branch Report ==");
		System.out.println("Employee count after add: " + employeeCountAfterAdd);
		System.out.println("Employee count after remove: " + employeeCountAfterRemove);
		System.out.println("Branch report total vehicles: " + report.getTotalVehicles());
		System.out.println("Branch report total revenue: " + report.getTotalRevenue());

		System.out.println("\n== Maintenance and Damage ==");
		System.out.println("Distance to maintenance: " + distanceToMaintenance);
		System.out.println("Needs maintenance: " + needsMaintenance);
		System.out.println("Damage assessment cost: " + assessment.getDamageCost());
		System.out.println("Maintenance status after schedule: " + scheduledStatus);
		System.out.println("Maintenance status after complete: " + maintenanceTask.getStatus());
	}

	private static void prepareVehicle(Vehicle vehicle) {
		vehicle.setInsuranceOption(InsuranceOption.STANDARD);
		vehicle.setMileagePolicy(MileagePolicy.STANDARD);
		vehicle.setStatus(VehicleStatus.AVAILABLE);
		vehicle.setCurrentMileage(14500);
		vehicle.setLastMaintenanceMileage(10000);
		vehicle.setMaintenanceInterval(5000);
	}

	private static Invoice createPaidInvoice(Date paymentDate, double addonCost) {
		Invoice invoice = new Invoice(1, 300.0, 20.0, addonCost);
		Customer customer = new Customer(1, "Ayse", "Demir", "ayse@example.com", 800, LoyaltyTier.SILVER);
		invoice.applyDiscount(customer);
		invoice.calculateTotal();
		invoice.addPayment(new Payment(3, 200.0, paymentDate, PaymentPurpose.ADDITIONAL_CHARGE, 1));
		return invoice;
	}

	private static Date createDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month, day);
		return calendar.getTime();
	}
}
