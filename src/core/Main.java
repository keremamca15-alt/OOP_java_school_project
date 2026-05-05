package core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, InvalidFileFormatException {
		SeedData seedData = createSeedData();
		FileManager fileManager = new FileManager();
		saveSeedData(fileManager, seedData);
		verifySeedData(fileManager);

		System.out.println("Kepler seed data generated.");
		System.out.println("Branches: " + seedData.branches.size());
		System.out.println("Customers: " + seedData.customers.size());
		System.out.println("Employees: " + seedData.employees.size());
		System.out.println("Vehicles: " + seedData.vehicles.size());
		System.out.println("Reservations: " + seedData.reservations.size());
		System.out.println("Contracts: " + seedData.rentalContracts.size());
		System.out.println("Invoices: " + seedData.invoices.size());
		System.out.println("Payments: " + seedData.payments.size());
		System.out.println("Maintenance tasks: " + seedData.maintenanceTasks.size());
		System.out.println("Damage assessments: " + seedData.damageAssessments.size());
		System.out.println("Branch reports: " + seedData.branchReports.size());
	}

	private static SeedData createSeedData() {
		SeedData data = new SeedData();

		Branch bornova = new Branch(1, "Bornova Branch", "Bornova, Izmir");
		Branch karsiyaka = new Branch(2, "Karsiyaka Branch", "Karsiyaka, Izmir");
		Branch konak = new Branch(3, "Konak Branch", "Konak, Izmir");
		Branch buca = new Branch(4, "Buca Branch", "Buca, Izmir");
		Branch gaziemir = new Branch(5, "Gaziemir Branch", "Gaziemir, Izmir");
		data.branches.add(bornova);
		data.branches.add(karsiyaka);
		data.branches.add(konak);
		data.branches.add(buca);
		data.branches.add(gaziemir);

		Customer ayse = customer(1, "Ayse", "Demir", "ayse.demir@example.com", 420, LoyaltyTier.BRONZE);
		Customer mehmet = customer(2, "Mehmet", "Yilmaz", "mehmet.yilmaz@example.com", 760, LoyaltyTier.SILVER);
		Customer zeynep = customer(3, "Zeynep", "Kaya", "zeynep.kaya@example.com", 1650, LoyaltyTier.GOLD);
		Customer emre = customer(4, "Emre", "Celik", "emre.celik@example.com", 120, LoyaltyTier.BRONZE);
		Customer deniz = customer(5, "Deniz", "Aydin", "deniz.aydin@example.com", 980, LoyaltyTier.SILVER);
		Customer elif = customer(6, "Elif", "Arslan", "elif.arslan@example.com", 2250, LoyaltyTier.GOLD);
		Customer bora = customer(7, "Bora", "Koc", "bora.koc@example.com", 310, LoyaltyTier.BRONZE);
		Customer ceren = customer(8, "Ceren", "Yildirim", "ceren.yildirim@example.com", 640, LoyaltyTier.SILVER);
		Customer umut = customer(9, "Umut", "Dogan", "umut.dogan@example.com", 1180, LoyaltyTier.SILVER);
		Customer irem = customer(10, "Irem", "Ozkan", "irem.ozkan@example.com", 1820, LoyaltyTier.GOLD);
		Customer tolga = customer(11, "Tolga", "Ergin", "tolga.ergin@example.com", 90, LoyaltyTier.BRONZE);
		Customer asya = customer(12, "Asya", "Kaplan", "asya.kaplan@example.com", 2540, LoyaltyTier.GOLD);
		data.customers.add(ayse);
		data.customers.add(mehmet);
		data.customers.add(zeynep);
		data.customers.add(emre);
		data.customers.add(deniz);
		data.customers.add(elif);
		data.customers.add(bora);
		data.customers.add(ceren);
		data.customers.add(umut);
		data.customers.add(irem);
		data.customers.add(tolga);
		data.customers.add(asya);

		RentalAgent ece = rentalAgent(10, 1010, "Ece", "Kaya", "ece.kaya@company.com", 35000.0, bornova);
		RentalAgent ozan = rentalAgent(11, 1011, "Ozan", "Polat", "ozan.polat@company.com", 34500.0, karsiyaka);
		RentalAgent melis = rentalAgent(12, 1012, "Melis", "Kurt", "melis.kurt@company.com", 35250.0, konak);
		RentalAgent can = rentalAgent(13, 1013, "Can", "Yildiz", "can.yildiz@company.com", 34200.0, buca);
		RentalAgent naz = rentalAgent(14, 1014, "Naz", "Aksoy", "naz.aksoy@company.com", 34800.0, gaziemir);
		Mechanic mert = mechanic(20, 1020, "Mert", "Aydin", "mert.aydin@company.com", 33000.0, bornova);
		Mechanic burak = mechanic(21, 1021, "Burak", "Sahin", "burak.sahin@company.com", 32500.0, karsiyaka);
		Mechanic pinar = mechanic(22, 1022, "Pinar", "Eren", "pinar.eren@company.com", 32750.0, konak);
		BranchManager selin = branchManager(30, 1030, "Selin", "Arslan", "selin.arslan@company.com", 48000.0, bornova);
		BranchManager hakan = branchManager(31, 1031, "Hakan", "Demir", "hakan.demir@company.com", 47000.0, karsiyaka);
		Employee unassignedAgent = rentalAgent(15, 1015, "Derya", "Ozturk", "derya.ozturk@company.com", 32000.0, null);
		Employee unassignedMechanic = mechanic(23, 1023, "Kerem", "Uslu", "kerem.uslu@company.com", 31500.0, null);
		data.employees.add(ece);
		data.employees.add(ozan);
		data.employees.add(melis);
		data.employees.add(can);
		data.employees.add(naz);
		data.employees.add(mert);
		data.employees.add(burak);
		data.employees.add(pinar);
		data.employees.add(selin);
		data.employees.add(hakan);
		data.employees.add(unassignedAgent);
		data.employees.add(unassignedMechanic);

		data.addons.add(new Addon(1, "Child Seat", "Daily child seat rental", 7.5));
		data.addons.add(new Addon(2, "GPS Navigation", "Portable navigation device", 5.0));
		data.addons.add(new Addon(3, "Additional Driver", "Second authorized driver", 12.0));
		data.addons.add(new Addon(4, "Winter Tire Package", "Seasonal tire package", 9.0));
		data.addons.add(new Addon(5, "Roadside Plus", "Extended roadside support", 6.0));
		data.addons.add(new Addon(6, "Airport Pickup", "Airport delivery and pickup service", 18.0));
		data.addons.add(new Addon(7, "Roof Rack", "Extra luggage rack", 8.0));

		Vehicle v101 = vehicle(new Economy(101, "35ABC01", "Hyundai", "i20", 2022, 45.0),
				InsuranceOption.STANDARD, MileagePolicy.STANDARD, VehicleStatus.AVAILABLE, 43200, 10000, 36000, bornova);
		Vehicle v102 = vehicle(new SUV(102, "35DEF02", "Toyota", "RAV4", 2023, 85.0),
				InsuranceOption.PREMIUM, MileagePolicy.STANDARD, VehicleStatus.RENTED, 61200, 10000, 55000, bornova);
		Vehicle v103 = vehicle(new Van(103, "35GHI03", "Ford", "Transit", 2021, 70.0),
				InsuranceOption.STANDARD, MileagePolicy.LIMITED, VehicleStatus.OUT_OF_SERVICE, 88500, 8000, 78000, bornova);
		Vehicle v104 = vehicle(new Luxury(104, "35JKL04", "BMW", "320i", 2024, 140.0),
				InsuranceOption.PREMIUM, MileagePolicy.UNLIMITED, VehicleStatus.AVAILABLE, 20600, 12000, 12000, bornova);
		Vehicle v105 = vehicle(new Economy(105, "35MNO05", "Renault", "Clio", 2020, 38.0),
				InsuranceOption.BASIC, MileagePolicy.LIMITED, VehicleStatus.IN_MAINTENANCE, 73600, 9000, 72000, bornova);
		Vehicle v106 = vehicle(new SUV(106, "35PRS06", "Nissan", "Qashqai", 2022, 78.0),
				InsuranceOption.STANDARD, MileagePolicy.STANDARD, VehicleStatus.AVAILABLE, 50200, 10000, 45200, karsiyaka);
		Vehicle v107 = vehicle(new Economy(107, "35TUV07", "Fiat", "Egea", 2021, 42.0),
				InsuranceOption.BASIC, MileagePolicy.STANDARD, VehicleStatus.AVAILABLE, 39100, 10000, 34000, karsiyaka);
		Vehicle v108 = vehicle(new Luxury(108, "35YZA08", "Mercedes", "C200", 2023, 155.0),
				InsuranceOption.PREMIUM, MileagePolicy.UNLIMITED, VehicleStatus.AVAILABLE, 24200, 12000, 18000, konak);
		Vehicle v109 = vehicle(new Van(109, "35BCD09", "Volkswagen", "Caravelle", 2022, 92.0),
				InsuranceOption.PREMIUM, MileagePolicy.STANDARD, VehicleStatus.AVAILABLE, 46300, 9000, 41000, konak);
		Vehicle v110 = vehicle(new Economy(110, "35EFG10", "Peugeot", "208", 2024, 48.0),
				InsuranceOption.STANDARD, MileagePolicy.LIMITED, VehicleStatus.AVAILABLE, 8700, 10000, 2000, buca);
		Vehicle v111 = vehicle(new SUV(111, "35HIJ11", "Kia", "Sportage", 2023, 82.0),
				InsuranceOption.STANDARD, MileagePolicy.STANDARD, VehicleStatus.AVAILABLE, 27800, 10000, 21500, gaziemir);
		Vehicle v112 = vehicle(new Luxury(112, "35KLM12", "Audi", "A4", 2021, 130.0),
				InsuranceOption.PREMIUM, MileagePolicy.UNLIMITED, VehicleStatus.AVAILABLE, 69000, 12000, 63000, null);
		data.vehicles.add(v101);
		data.vehicles.add(v102);
		data.vehicles.add(v103);
		data.vehicles.add(v104);
		data.vehicles.add(v105);
		data.vehicles.add(v106);
		data.vehicles.add(v107);
		data.vehicles.add(v108);
		data.vehicles.add(v109);
		data.vehicles.add(v110);
		data.vehicles.add(v111);
		data.vehicles.add(v112);

		Reservation completedWithRefund = reservation(1, ayse, v101,
				date(2026, Calendar.APRIL, 10), date(2026, Calendar.APRIL, 13),
				ReservationStatus.COMPLETED, 148.5, 300.0, data.addons.get(0), data.addons.get(1));
		Reservation completedWithCharge = reservation(2, mehmet, v104,
				date(2026, Calendar.APRIL, 15), date(2026, Calendar.APRIL, 19),
				ReservationStatus.COMPLETED, 558.0, 300.0, data.addons.get(2));
		Reservation active = reservation(3, zeynep, v102,
				date(2026, Calendar.MAY, 5), date(2026, Calendar.MAY, 9),
				ReservationStatus.CONFIRMED, 306.0, 300.0, data.addons.get(4));
		Reservation pending = reservation(4, ayse, v106,
				date(2026, Calendar.JUNE, 5), date(2026, Calendar.JUNE, 9),
				ReservationStatus.PENDING, 352.0, 300.0, data.addons.get(1));
		Reservation confirmed = reservation(5, deniz, v108,
				date(2026, Calendar.JUNE, 8), date(2026, Calendar.JUNE, 11),
				ReservationStatus.CONFIRMED, 499.5, 300.0, data.addons.get(2), data.addons.get(4));
		Reservation cancelled = reservation(6, emre, v107,
				date(2026, Calendar.APRIL, 3), date(2026, Calendar.APRIL, 5),
				ReservationStatus.CANCELLED, 0.0, 0.0);
		Reservation futureConflict = reservation(7, elif, v101,
				date(2026, Calendar.JULY, 1), date(2026, Calendar.JULY, 5),
				ReservationStatus.CONFIRMED, 178.2, 300.0, data.addons.get(0));
		data.reservations.add(completedWithRefund);
		data.reservations.add(completedWithCharge);
		data.reservations.add(active);
		data.reservations.add(pending);
		data.reservations.add(confirmed);
		data.reservations.add(cancelled);
		data.reservations.add(futureConflict);

		addPrepayment(data, 1, completedWithRefund);
		addPrepayment(data, 2, completedWithCharge);
		addPrepayment(data, 3, active);
		addPrepayment(data, 4, pending);
		addPrepayment(data, 5, confirmed);
		addPrepayment(data, 6, futureConflict);

		RentalContract contract1 = contract(1, completedWithRefund, ece,
				date(2026, Calendar.APRIL, 10), date(2026, Calendar.APRIL, 13),
				date(2026, Calendar.APRIL, 13), 300.0, 42100, 42540, ContractStatus.CLOSED);
		RentalContract contract2 = contract(2, completedWithCharge, ece,
				date(2026, Calendar.APRIL, 15), date(2026, Calendar.APRIL, 19),
				date(2026, Calendar.APRIL, 19), 300.0, 18800, 19750, ContractStatus.CLOSED);
		RentalContract contract3 = contract(3, active, ece,
				date(2026, Calendar.MAY, 5), date(2026, Calendar.MAY, 9),
				null, 300.0, 61200, 0, ContractStatus.ACTIVE);
		data.rentalContracts.add(contract1);
		data.rentalContracts.add(contract2);
		data.rentalContracts.add(contract3);
		v102.setStatus(VehicleStatus.RENTED);

		addDeposit(data, 7, contract1, ayse);
		addDeposit(data, 8, contract2, mehmet);
		addDeposit(data, 9, contract3, zeynep);

		DamageAssessment assessment1 = damage(1, v101, ece, date(2026, Calendar.APRIL, 13),
				"Small parking scratch on rear bumper", 120.0);
		DamageAssessment assessment2 = damage(2, v104, ece, date(2026, Calendar.APRIL, 19),
				"Front bumper and wheel damage", 520.0);
		DamageAssessment unresolved = damage(3, v109, melis, date(2026, Calendar.MAY, 4),
				"Right sliding door dent awaiting mechanic review", 180.0);
		data.damageAssessments.add(assessment1);
		data.damageAssessments.add(assessment2);
		data.damageAssessments.add(unresolved);

		Invoice invoice1 = invoice(1, contract1, assessment1, 165.0, 120.0, 37.5, 0.0);
		invoice1.applyDiscount(ayse);
		invoice1.calculateTotal();
		Invoice invoice2 = invoice(2, contract2, assessment2, 700.0, 520.0, 48.0, 780.0);
		invoice2.applyDiscount(mehmet);
		invoice2.calculateTotal();
		data.invoices.add(invoice1);
		data.invoices.add(invoice2);

		addInvoicePayment(data, invoice1, completedWithRefund.getPrepayment());
		addInvoicePayment(data, invoice2, completedWithCharge.getPrepayment());
		addRefund(data, 10, contract1, invoice1, ayse, contract1.calculateDepositRefund());
		addAdditionalCharge(data, 11, contract2, invoice2, mehmet, contract2.calculateAdditionalChargeAfterDeposit());

		addCompletedRental(data, 8, 4, 3, 12, 13, 14, 0, bora, v106, ozan,
				date(2026, Calendar.MARCH, 2), date(2026, Calendar.MARCH, 5),
				279.0, 300.0, 48200, 48650, 294.0, 0.0, 15.0, 0.0,
				"", data.addons.get(1));
		addCompletedRental(data, 9, 5, 4, 15, 16, 17, 4, ceren, v107, ozan,
				date(2026, Calendar.MARCH, 8), date(2026, Calendar.MARCH, 12),
				238.0, 300.0, 36200, 37150, 248.0, 80.0, 30.0, 1100.0,
				"Lost key and deep interior cleaning", data.addons.get(0));
		addCompletedRental(data, 10, 6, 5, 18, 19, 20, 0, umut, v108, melis,
				date(2026, Calendar.MARCH, 14), date(2026, Calendar.MARCH, 18),
				724.0, 300.0, 22200, 22640, 760.0, 0.0, 48.0, 0.0,
				"", data.addons.get(2));
		addCompletedRental(data, 11, 7, 6, 21, 22, 23, 5, irem, v109, melis,
				date(2026, Calendar.MARCH, 20), date(2026, Calendar.MARCH, 24),
				438.0, 300.0, 43200, 43980, 510.0, 230.0, 24.0, 380.0,
				"Rear door paint damage after return", data.addons.get(4));
		addCompletedRental(data, 12, 8, 7, 24, 25, 26, 0, tolga, v110, can,
				date(2026, Calendar.MARCH, 25), date(2026, Calendar.MARCH, 27),
				136.0, 300.0, 7100, 7350, 136.0, 0.0, 0.0, 0.0,
				"");
		addCompletedRental(data, 13, 9, 8, 27, 28, 29, 6, asya, v111, naz,
				date(2026, Calendar.APRIL, 1), date(2026, Calendar.APRIL, 5),
				421.2, 300.0, 24800, 25540, 410.0, 150.0, 96.0, 280.0,
				"Small windshield chip", data.addons.get(2), data.addons.get(5));
		addCompletedRental(data, 14, 10, 9, 30, 31, 32, 0, deniz, v112, ece,
				date(2026, Calendar.APRIL, 6), date(2026, Calendar.APRIL, 9),
				415.5, 300.0, 67100, 67580, 495.0, 0.0, 54.0, 0.0,
				"", data.addons.get(5));
		addCompletedRental(data, 15, 11, 10, 33, 34, 35, 7, elif, v103, ece,
				date(2026, Calendar.APRIL, 20), date(2026, Calendar.APRIL, 23),
				281.7, 300.0, 86200, 87040, 315.0, 340.0, 24.0, 960.0,
				"Cargo area side panel dent", data.addons.get(6));

		MaintenanceTask completedTask = maintenance(1, v105, mert, date(2026, Calendar.APRIL, 28),
				"Oil and filter service completed", MaintenanceStatus.COMPLETED);
		v105.setLastMaintenanceMileage(v105.getCurrentMileage());
		v105.setStatus(VehicleStatus.AVAILABLE);
		MaintenanceTask scheduledTask = maintenance(2, v103, mert, date(2026, Calendar.MAY, 6),
				"Mileage based full inspection", MaintenanceStatus.SCHEDULED);
		v103.setStatus(VehicleStatus.IN_MAINTENANCE);
		MaintenanceTask oldTask = maintenance(3, v109, pinar, date(2026, Calendar.APRIL, 25),
				"Brake check completed", MaintenanceStatus.COMPLETED);
		data.maintenanceTasks.add(completedTask);
		data.maintenanceTasks.add(scheduledTask);
		data.maintenanceTasks.add(oldTask);

		BranchReport bornovaReport = selin.generateReport(bornova);
		BranchReport karsiyakaReport = hakan.generateReport(karsiyaka);
		karsiyakaReport.setReportID(2);
		data.branchReports.add(bornovaReport);
		data.branchReports.add(karsiyakaReport);
		return data;
	}

	private static void saveSeedData(FileManager fileManager, SeedData data) throws FileNotFoundException {
		fileManager.saveBranches(data.branches);
		fileManager.saveCustomers(data.customers);
		fileManager.saveEmployees(data.employees);
		fileManager.saveVehicles(data.vehicles);
		fileManager.saveMaintenanceTasks(data.maintenanceTasks);
		fileManager.saveAddons(data.addons);
		fileManager.savePayments(data.payments);
		fileManager.saveReservations(data.reservations);
		fileManager.saveRentalContracts(data.rentalContracts);
		fileManager.saveDamageAssessments(data.damageAssessments);
		fileManager.saveInvoices(data.invoices);
		fileManager.saveBranchReports(data.branchReports);
	}

	private static void verifySeedData(FileManager fileManager)
			throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<Branch> branches = fileManager.loadBranches();
		ArrayList<Customer> customers = fileManager.loadCustomers();
		ArrayList<Employee> employees = fileManager.loadEmployees(branches);
		ArrayList<Vehicle> vehicles = fileManager.loadVehicles(branches);
		ArrayList<Mechanic> mechanics = getMechanics(employees);
		fileManager.loadMaintenanceTasks(vehicles, mechanics);
		ArrayList<Addon> addons = fileManager.loadAddons();
		ArrayList<Payment> payments = fileManager.loadPayments(customers);
		ArrayList<Reservation> reservations = fileManager.loadReservations(customers, vehicles, payments, addons);
		ArrayList<RentalAgent> rentalAgents = getRentalAgents(employees);
		ArrayList<RentalContract> contracts = fileManager.loadRentalContracts(reservations, rentalAgents, payments, addons);
		ArrayList<DamageAssessment> assessments = fileManager.loadDamageAssessments(vehicles, rentalAgents);
		fileManager.loadInvoices(contracts, assessments, payments);
		fileManager.loadBranchReports(branches, getBranchManagers(employees));
	}

	private static Customer customer(int userID, String name, String surname, String email, int points, LoyaltyTier tier) {
		return new Customer(userID, name, surname, email, points, tier);
	}

	private static RentalAgent rentalAgent(int employeeID, int userID, String name, String surname, String email,
			double salary, Branch branch) {
		RentalAgent agent = new RentalAgent();
		fillEmployee(agent, employeeID, userID, name, surname, email, salary, branch);
		return agent;
	}

	private static Mechanic mechanic(int employeeID, int userID, String name, String surname, String email,
			double salary, Branch branch) {
		Mechanic mechanic = new Mechanic();
		fillEmployee(mechanic, employeeID, userID, name, surname, email, salary, branch);
		return mechanic;
	}

	private static BranchManager branchManager(int employeeID, int userID, String name, String surname, String email,
			double salary, Branch branch) {
		BranchManager manager = new BranchManager();
		fillEmployee(manager, employeeID, userID, name, surname, email, salary, branch);
		manager.setManagedBranch(branch);
		return manager;
	}

	private static void fillEmployee(Employee employee, int employeeID, int userID, String name, String surname,
			String email, double salary, Branch branch) {
		employee.setEmployeeID(employeeID);
		employee.setUserID(userID);
		employee.setName(name);
		employee.setSurname(surname);
		employee.setEmail(email);
		employee.setSalary(salary);
		employee.setBranch(branch);
	}

	private static Vehicle vehicle(Vehicle vehicle, InsuranceOption insuranceOption, MileagePolicy mileagePolicy,
			VehicleStatus status, int currentMileage, int maintenanceInterval, int lastMaintenanceMileage, Branch branch) {
		vehicle.setInsuranceOption(insuranceOption);
		vehicle.setMileagePolicy(mileagePolicy);
		vehicle.setStatus(status);
		vehicle.setCurrentMileage(currentMileage);
		vehicle.setMaintenanceInterval(maintenanceInterval);
		vehicle.setLastMaintenanceMileage(lastMaintenanceMileage);
		vehicle.setBranch(branch);
		return vehicle;
	}

	private static Reservation reservation(int reservationID, Customer customer, Vehicle vehicle, Date startDate,
			Date endDate, ReservationStatus status, double prepaymentAmount, double depositAmount, Addon... addons) {
		Reservation reservation = new Reservation(reservationID, startDate, endDate, status);
		reservation.setCustomer(customer);
		reservation.setVehicle(vehicle);
		reservation.setPrePaymentAmount(prepaymentAmount);
		reservation.setDepositAmount(depositAmount);
		for (Addon addon : addons) {
			reservation.addAddon(addon);
		}
		return reservation;
	}

	private static RentalContract contract(int contractID, Reservation reservation, RentalAgent agent, Date pickup,
			Date expectedReturn, Date actualReturn, double deposit, int initialMileage, int finalMileage,
			ContractStatus status) {
		RentalContract contract = new RentalContract(contractID, pickup, expectedReturn, status);
		contract.setReservation(reservation);
		contract.setRentalAgent(agent);
		contract.setActualReturnDate(actualReturn);
		contract.setDepositAmount(deposit);
		contract.setInitialMileage(initialMileage);
		contract.setFinalMileage(finalMileage);
		for (Addon addon : reservation.getAddons()) {
			contract.addAddon(addon);
		}
		return contract;
	}

	private static DamageAssessment damage(int id, Vehicle vehicle, RentalAgent agent, Date date, String description,
			double cost) {
		DamageAssessment assessment = new DamageAssessment(id, date, description, cost);
		assessment.setVehicle(vehicle);
		assessment.setRentalAgent(agent);
		return assessment;
	}

	private static Invoice invoice(int id, RentalContract contract, DamageAssessment assessment, double base,
			double damageFee, double addonFee, double additionalCharges) {
		Invoice invoice = new Invoice(id, base, damageFee, addonFee);
		invoice.setRentalContract(contract);
		invoice.setDamageAssessment(assessment);
		invoice.setAdditionalCharges(additionalCharges);
		return invoice;
	}

	private static MaintenanceTask maintenance(int id, Vehicle vehicle, Mechanic mechanic, Date date, String description,
			MaintenanceStatus status) {
		MaintenanceTask task = new MaintenanceTask(id, date, description, status);
		task.setVehicle(vehicle);
		task.setMechanic(mechanic);
		return task;
	}

	private static void addPrepayment(SeedData data, int paymentID, Reservation reservation) {
		Payment payment = new Payment(paymentID, reservation.getPrePaymentAmount(), reservation.getStartDate(),
				PaymentPurpose.PREPAYMENT, reservation.getCustomer().getUserID());
		payment.setCustomer(reservation.getCustomer());
		payment.setReservation(reservation);
		data.payments.add(payment);
	}

	private static void addDeposit(SeedData data, int paymentID, RentalContract contract, Customer customer) {
		Payment payment = new Payment(paymentID, contract.getDepositAmount(), contract.getPickupDate(),
				PaymentPurpose.DEPOSIT, customer.getUserID());
		payment.setCustomer(customer);
		payment.setRentalContract(contract);
		data.payments.add(payment);
	}

	private static void addInvoicePayment(SeedData data, Invoice invoice, Payment payment) {
		if (payment != null) {
			invoice.addPayment(payment);
		}
	}

	private static void addRefund(SeedData data, int paymentID, RentalContract contract, Invoice invoice,
			Customer customer, double amount) {
		if (amount <= 0) {
			return;
		}
		Payment payment = new Payment(paymentID, amount, contract.getActualReturnDate(), PaymentPurpose.REFUND,
				customer.getUserID());
		payment.setCustomer(customer);
		payment.setRentalContract(contract);
		payment.setInvoice(invoice);
		data.payments.add(payment);
	}

	private static void addAdditionalCharge(SeedData data, int paymentID, RentalContract contract, Invoice invoice,
			Customer customer, double amount) {
		if (amount <= 0) {
			return;
		}
		Payment payment = new Payment(paymentID, amount, contract.getActualReturnDate(),
				PaymentPurpose.ADDITIONAL_CHARGE, customer.getUserID());
		payment.setCustomer(customer);
		payment.setRentalContract(contract);
		payment.setInvoice(invoice);
		data.payments.add(payment);
	}

	private static void addCompletedRental(SeedData data, int reservationID, int contractID, int invoiceID,
			int prepaymentID, int depositPaymentID, int settlementPaymentID, int assessmentID, Customer customer,
			Vehicle vehicle, RentalAgent agent, Date startDate, Date endDate, double prepaymentAmount,
			double depositAmount, int initialMileage, int finalMileage, double baseAmount, double damageFee,
			double addonFee, double additionalCharges, String damageDescription, Addon... addons) {
		Reservation reservation = reservation(reservationID, customer, vehicle, startDate, endDate,
				ReservationStatus.COMPLETED, prepaymentAmount, depositAmount, addons);
		data.reservations.add(reservation);
		addPrepayment(data, prepaymentID, reservation);

		RentalContract contract = contract(contractID, reservation, agent, startDate, endDate, endDate,
				depositAmount, initialMileage, finalMileage, ContractStatus.CLOSED);
		data.rentalContracts.add(contract);
		addDeposit(data, depositPaymentID, contract, customer);

		DamageAssessment assessment = null;
		if (damageFee > 0) {
			assessment = damage(assessmentID, vehicle, agent, endDate, damageDescription, damageFee);
			data.damageAssessments.add(assessment);
		}

		Invoice invoice = invoice(invoiceID, contract, assessment, baseAmount, damageFee, addonFee, additionalCharges);
		invoice.applyDiscount(customer);
		invoice.calculateTotal();
		data.invoices.add(invoice);
		addInvoicePayment(data, invoice, reservation.getPrepayment());

		double additionalCharge = contract.calculateAdditionalChargeAfterDeposit();
		if (additionalCharge > 0) {
			addAdditionalCharge(data, settlementPaymentID, contract, invoice, customer, additionalCharge);
			return;
		}

		double refund = contract.calculateDepositRefund();
		if (refund > 0) {
			addRefund(data, settlementPaymentID, contract, invoice, customer, refund);
		}
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

	private static Date date(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month, day);
		return calendar.getTime();
	}

	private static class SeedData {
		private final ArrayList<Branch> branches = new ArrayList<>();
		private final ArrayList<Customer> customers = new ArrayList<>();
		private final ArrayList<Employee> employees = new ArrayList<>();
		private final ArrayList<Vehicle> vehicles = new ArrayList<>();
		private final ArrayList<MaintenanceTask> maintenanceTasks = new ArrayList<>();
		private final ArrayList<Addon> addons = new ArrayList<>();
		private final ArrayList<Payment> payments = new ArrayList<>();
		private final ArrayList<Reservation> reservations = new ArrayList<>();
		private final ArrayList<RentalContract> rentalContracts = new ArrayList<>();
		private final ArrayList<DamageAssessment> damageAssessments = new ArrayList<>();
		private final ArrayList<Invoice> invoices = new ArrayList<>();
		private final ArrayList<BranchReport> branchReports = new ArrayList<>();
	}
}
