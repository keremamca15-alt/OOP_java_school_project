package core;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, InvalidFileFormatException,
			InvalidReservationException, VehicleNotAvailableException {
		Date startDate = createDate(2026, Calendar.MAY, 10);
		Date endDate = createDate(2026, Calendar.MAY, 14);
		Date searchStartDate = createDate(2026, Calendar.MAY, 11);
		Date searchEndDate = createDate(2026, Calendar.MAY, 13);

		FileManager fileManager = new FileManager();
		ArrayList<Branch> branches = fileManager.loadBranches();
		ArrayList<Customer> customers = fileManager.loadCustomers();
		ArrayList<Employee> employees = fileManager.loadEmployees(branches);
		ArrayList<Vehicle> vehicles = fileManager.loadVehicles(branches);
		ArrayList<Mechanic> mechanics = getMechanics(employees);
		ArrayList<BranchManager> branchManagers = getBranchManagers(employees);
		ArrayList<MaintenanceTask> maintenanceTasks = fileManager.loadMaintenanceTasks(vehicles, mechanics);
		ArrayList<Addon> addons = fileManager.loadAddons();
		ArrayList<Payment> payments = fileManager.loadPayments(customers);
		ArrayList<Reservation> reservations = fileManager.loadReservations(customers, vehicles, payments, addons);
		ArrayList<RentalAgent> rentalAgents = getRentalAgents(employees);
		ArrayList<RentalContract> rentalContracts = fileManager.loadRentalContracts(
				reservations, rentalAgents, payments, addons);
		ArrayList<DamageAssessment> damageAssessments = fileManager.loadDamageAssessments(vehicles, rentalAgents);
		ArrayList<Invoice> invoices = fileManager.loadInvoices(rentalContracts, damageAssessments, payments);
		ArrayList<BranchReport> branchReports = fileManager.loadBranchReports(branches, branchManagers);
		fileManager.saveBranches(branches);
		fileManager.saveCustomers(customers);
		fileManager.saveEmployees(employees);
		fileManager.saveVehicles(vehicles);
		fileManager.saveMaintenanceTasks(maintenanceTasks);
		fileManager.saveAddons(addons);
		fileManager.savePayments(payments);
		fileManager.saveReservations(reservations);
		fileManager.saveRentalContracts(rentalContracts);
		fileManager.saveDamageAssessments(damageAssessments);
		fileManager.saveInvoices(invoices);
		fileManager.saveBranchReports(branchReports);

		Branch branch = branches.get(0);
		Customer customer = customers.get(0);
		Vehicle suv = vehicles.get(1);
		Vehicle economy = vehicles.get(0);

		Reservation reservation = new Reservation(1, startDate, endDate, ReservationStatus.CONFIRMED);
		reservation.setVehicle(suv);

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

		Reservation customerReservation = customer.makeReservation(2, economy, searchStartDate, searchEndDate);
		ArrayList<Vehicle> searchedVehicles = customer.searchAvailableVehicles(branch, searchStartDate, searchEndDate);
		customer.earnPoints(500.0);

		Payment validPayment = new Payment(1, 120.0, startDate, PaymentPurpose.PREPAYMENT, 1);
		Payment invalidPayment = new Payment(2, 0.0, startDate, PaymentPurpose.DEPOSIT, 1);

		BranchManager branchManager = new BranchManager(branch);
		Employee employee = new RentalAgent();
		employee.setEmployeeID(40);
		branchManager.addEmployee(employee);
		int employeeCountAfterAdd = branch.getEmployees().size();
		branchManager.removeEmployee(40);
		int employeeCountAfterRemove = branch.getEmployees().size();
		branchManager.addEmployee(employee);
		BranchReport report = branchManager.generateReport(branch);

		RentalAgent rentalAgent = new RentalAgent();
		rentalAgent.processPickup(reservation);
		DamageAssessment assessment = rentalAgent.assessDamage(suv, "Small bumper scratch", 250.0);
		Invoice returnInvoice = rentalAgent.processReturn(reservation, assessment);

		MaintenanceTask maintenanceTask = new MaintenanceTask();
		maintenanceTask.setVehicle(economy);
		if (!mechanics.isEmpty()) {
			mechanics.get(0).performMaintenance(maintenanceTask);
		}
		maintenanceTask.scheduleMaintenance();
		MaintenanceStatus scheduledStatus = maintenanceTask.getStatus();
		VehicleStatus maintenanceVehicleStatusAfterSchedule = economy.getStatus();
		maintenanceTask.completeMaintenance();
		VehicleStatus maintenanceVehicleStatusAfterComplete = economy.getStatus();
		int lastMaintenanceMileageAfterComplete = economy.getLastMaintenanceMileage();

		System.out.println("== Load / Save ==");
		System.out.println("Loaded branches: " + branches.size());
		System.out.println("Loaded customers: " + customers.size());
		System.out.println("Loaded employees: " + employees.size());
		System.out.println("Loaded vehicles: " + vehicles.size());
		System.out.println("Loaded maintenance tasks: " + maintenanceTasks.size());
		System.out.println("Loaded addons: " + addons.size());
		System.out.println("Loaded payments: " + payments.size());
		System.out.println("Loaded reservations: " + reservations.size());
		System.out.println("Loaded rental contracts: " + rentalContracts.size());
		System.out.println("Loaded damage assessments: " + damageAssessments.size());
		System.out.println("Loaded invoices: " + invoices.size());
		System.out.println("Loaded branch reports: " + branchReports.size());
		System.out.println("Saved demo data to data/");

		System.out.println("\n== Reservation ==");
		System.out.println("Reservation days: " + days);
		reservation.cancelReservation();
		System.out.println("Reservation status after cancel: " + reservation.getStatus());
		reservation.confirmReservation();
		System.out.println("Reservation status after confirm: " + reservation.getStatus());
		System.out.println("Available vehicles for search range: " + branch.findAvailableVehicles(searchStartDate, searchEndDate).size());
		System.out.println("Customer search result count: " + searchedVehicles.size());
		System.out.println("Customer reservation count: " + customer.viewMyReservations().size());
		System.out.println("Customer reservation vehicle plate: " + customerReservation.getVehicle().getPlateNumber());

		System.out.println("\n== Pickup / Return ==");
		System.out.println("Rental contract status after return: " + reservation.getRentalContract().getStatus());
		System.out.println("Vehicle status after return: " + suv.getStatus());
		System.out.println("Damage assessment cost: " + assessment.getDamageCost());
		System.out.println("Return invoice total: " + returnInvoice.calculateTotal());

		System.out.println("\n== Payment / Invoice ==");
		System.out.println("Addon cost: " + addonCost);
		System.out.println("Vehicle rental cost: " + rentalCost);
		System.out.println("Used mileage: " + usedMileage);
		System.out.println("Extra km charge: " + extraKmCharge);
		System.out.println("Valid payment processed: " + validPayment.processPayment());
		System.out.println("Invalid payment processed: " + invalidPayment.processPayment());
		System.out.println("Customer new loyalty points: " + customer.getLoyaltyPoints());
		System.out.println("Customer loyalty tier: " + customer.getLoyaltyTier());

		System.out.println("\n== Maintenance ==");
		System.out.println("Distance to maintenance: " + distanceToMaintenance);
		System.out.println("Needs maintenance: " + needsMaintenance);
		System.out.println("Maintenance status after schedule: " + scheduledStatus);
		System.out.println("Maintenance vehicle status after schedule: " + maintenanceVehicleStatusAfterSchedule);
		System.out.println("Maintenance status after complete: " + maintenanceTask.getStatus());
		System.out.println("Maintenance vehicle status after complete: " + maintenanceVehicleStatusAfterComplete);
		System.out.println("Last maintenance mileage after complete: " + lastMaintenanceMileageAfterComplete);

		System.out.println("\n== Branch Report ==");
		System.out.println("Employee count after add: " + employeeCountAfterAdd);
		System.out.println("Employee count after remove: " + employeeCountAfterRemove);
		System.out.println("Branch report total vehicles: " + report.getTotalVehicles());
		System.out.println("Branch report total revenue: " + report.getTotalRevenue());
	}

	private static Invoice createPaidInvoice(Date paymentDate, double addonCost) {
		Invoice invoice = new Invoice(1, 300.0, 20.0, addonCost);
		Customer customer = new Customer(1, "Ayse", "Demir", "ayse@example.com", 800, LoyaltyTier.SILVER);
		invoice.applyDiscount(customer);
		invoice.calculateTotal();
		invoice.addPayment(new Payment(3, 200.0, paymentDate, PaymentPurpose.ADDITIONAL_CHARGE, 1));
		return invoice;
	}

	private static ArrayList<RentalAgent> getRentalAgents(ArrayList<Employee> employees) {
		ArrayList<RentalAgent> rentalAgents = new ArrayList<>();
		for (Employee employee : employees) {
			if (employee instanceof RentalAgent) {
				rentalAgents.add((RentalAgent) employee);
			}
		}
		return rentalAgents;
	}

	private static ArrayList<Mechanic> getMechanics(ArrayList<Employee> employees) {
		ArrayList<Mechanic> mechanics = new ArrayList<>();
		for (Employee employee : employees) {
			if (employee instanceof Mechanic) {
				mechanics.add((Mechanic) employee);
			}
		}
		return mechanics;
	}

	private static ArrayList<BranchManager> getBranchManagers(ArrayList<Employee> employees) {
		ArrayList<BranchManager> branchManagers = new ArrayList<>();
		for (Employee employee : employees) {
			if (employee instanceof BranchManager) {
				branchManagers.add((BranchManager) employee);
			}
		}
		return branchManagers;
	}

	private static Date createDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month, day);
		return calendar.getTime();
	}
}
