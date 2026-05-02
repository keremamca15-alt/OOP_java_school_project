package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class FileManager {
	private static final String BRANCHES_FILE_PATH = "data/branches.txt";
	private static final String CUSTOMERS_FILE_PATH = "data/customers.txt";
	private static final String EMPLOYEES_FILE_PATH = "data/employees.txt";
	private static final String VEHICLES_FILE_PATH = "data/vehicles.txt";
	private static final String MAINTENANCE_TASKS_FILE_PATH = "data/maintenance_tasks.txt";
	private static final String ADDONS_FILE_PATH = "data/addons.txt";
	private static final String PAYMENTS_FILE_PATH = "data/payments.txt";
	private static final String RESERVATIONS_FILE_PATH = "data/reservations.txt";
	private static final String RENTAL_CONTRACTS_FILE_PATH = "data/rental_contracts.txt";
	private static final String DAMAGE_ASSESSMENTS_FILE_PATH = "data/damage_assessments.txt";
	private static final String INVOICES_FILE_PATH = "data/invoices.txt";
	private static final String BRANCH_REPORTS_FILE_PATH = "data/branch_reports.txt";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	static {
		DATE_FORMAT.setLenient(false);
	}

	public ArrayList<Branch> loadBranches() throws FileNotFoundException, InvalidFileFormatException {
		return loadBranches(BRANCHES_FILE_PATH);
	}

	public ArrayList<Branch> loadBranches(String filePath) throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<Branch> branches = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 3);
			int branchID = parseInt(filePath, line, parts[0]);
			String name = parts[1];
			String address = parts[2];
			branches.add(new Branch(branchID, name, address));
		}
		scanner.close();
		return branches;
	}

	public ArrayList<Customer> loadCustomers() throws FileNotFoundException, InvalidFileFormatException {
		return loadCustomers(CUSTOMERS_FILE_PATH);
	}

	public ArrayList<Customer> loadCustomers(String filePath) throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<Customer> customers = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 6);
			int userID = parseInt(filePath, line, parts[0]);
			String name = parts[1];
			String surname = parts[2];
			String email = parts[3];
			int loyaltyPoints = parseInt(filePath, line, parts[4]);
			LoyaltyTier loyaltyTier = LoyaltyTier.valueOf(parts[5]);
			customers.add(new Customer(userID, name, surname, email, loyaltyPoints, loyaltyTier));
		}
		scanner.close();
		return customers;
	}

	public ArrayList<Employee> loadEmployees(ArrayList<Branch> branches)
			throws FileNotFoundException, InvalidFileFormatException {
		return loadEmployees(EMPLOYEES_FILE_PATH, branches);
	}

	public ArrayList<Employee> loadEmployees(String filePath, ArrayList<Branch> branches)
			throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<Employee> employees = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 9);
			Employee employee = createEmployee(filePath, line, parts, branches);
			int branchID = parseInt(filePath, line, parts[7]);
			Branch branch = findBranchByID(branches, branchID);
			if (branch != null) {
				branch.getEmployees().add(employee);
				employee.setBranch(branch);
			}
			employees.add(employee);
		}
		scanner.close();
		return employees;
	}

	public ArrayList<Vehicle> loadVehicles(ArrayList<Branch> branches)
			throws FileNotFoundException, InvalidFileFormatException {
		return loadVehicles(VEHICLES_FILE_PATH, branches);
	}

	public ArrayList<Vehicle> loadVehicles(String filePath, ArrayList<Branch> branches)
			throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<Vehicle> vehicles = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 14);
			Vehicle vehicle = createVehicle(filePath, line, parts);
			int branchID = parseInt(filePath, line, parts[13]);
			Branch branch = findBranchByID(branches, branchID);
			if (branch != null) {
				branch.addVehicle(vehicle);
			}
			vehicles.add(vehicle);
		}
		scanner.close();
		return vehicles;
	}

	public ArrayList<MaintenanceTask> loadMaintenanceTasks(ArrayList<Vehicle> vehicles, ArrayList<Mechanic> mechanics)
			throws FileNotFoundException, InvalidFileFormatException {
		return loadMaintenanceTasks(MAINTENANCE_TASKS_FILE_PATH, vehicles, mechanics);
	}

	public ArrayList<MaintenanceTask> loadMaintenanceTasks(String filePath, ArrayList<Vehicle> vehicles,
			ArrayList<Mechanic> mechanics) throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<MaintenanceTask> maintenanceTasks = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 6);
			MaintenanceTask task = new MaintenanceTask(
					parseInt(filePath, line, parts[0]),
					parseDate(filePath, line, parts[3]),
					parts[4],
					MaintenanceStatus.valueOf(parts[5]));

			Vehicle vehicle = findVehicleByID(vehicles, parseInt(filePath, line, parts[1]));
			if (vehicle != null) {
				task.setVehicle(vehicle);
				vehicle.getMaintenanceTasks().add(task);
			}

			Mechanic mechanic = findMechanicByID(mechanics, parseInt(filePath, line, parts[2]));
			if (mechanic != null) {
				task.setMechanic(mechanic);
				mechanic.getMaintenanceTasks().add(task);
			}
			maintenanceTasks.add(task);
		}
		scanner.close();
		return maintenanceTasks;
	}

	public ArrayList<Addon> loadAddons() throws FileNotFoundException, InvalidFileFormatException {
		return loadAddons(ADDONS_FILE_PATH);
	}

	public ArrayList<Addon> loadAddons(String filePath) throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<Addon> addons = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 4);
			int addonID = parseInt(filePath, line, parts[0]);
			String name = parts[1];
			String description = parts[2];
			double dailyPrice = parseDouble(filePath, line, parts[3]);
			addons.add(new Addon(addonID, name, description, dailyPrice));
		}
		scanner.close();
		return addons;
	}

	public ArrayList<Payment> loadPayments(ArrayList<Customer> customers)
			throws FileNotFoundException, InvalidFileFormatException {
		return loadPayments(PAYMENTS_FILE_PATH, customers);
	}

	public ArrayList<Payment> loadPayments(String filePath, ArrayList<Customer> customers)
			throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<Payment> payments = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 8);
			Payment payment = new Payment(
					parseInt(filePath, line, parts[0]),
					parseDouble(filePath, line, parts[1]),
					parseDate(filePath, line, parts[2]),
					PaymentPurpose.valueOf(parts[3]),
					parseInt(filePath, line, parts[4]));
			Customer customer = findCustomerByID(customers, payment.getCustomerID());
			if (customer != null) {
				payment.setCustomer(customer);
				customer.getPayments().add(payment);
			}
			payments.add(payment);
		}
		scanner.close();
		return payments;
	}

	public ArrayList<Reservation> loadReservations(ArrayList<Customer> customers, ArrayList<Vehicle> vehicles,
			ArrayList<Payment> payments, ArrayList<Addon> addons) throws FileNotFoundException, InvalidFileFormatException {
		return loadReservations(RESERVATIONS_FILE_PATH, customers, vehicles, payments, addons);
	}

	public ArrayList<Reservation> loadReservations(String filePath, ArrayList<Customer> customers,
			ArrayList<Vehicle> vehicles, ArrayList<Payment> payments, ArrayList<Addon> addons)
			throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<Reservation> reservations = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 11);
			Reservation reservation = new Reservation(
					parseInt(filePath, line, parts[0]),
					parseDate(filePath, line, parts[3]),
					parseDate(filePath, line, parts[4]),
					ReservationStatus.valueOf(parts[5]));
			reservation.setPrePaymentAmount(parseDouble(filePath, line, parts[6]));
			reservation.setDepositAmount(parseDouble(filePath, line, parts[7]));

			Customer customer = findCustomerByID(customers, parseInt(filePath, line, parts[1]));
			if (customer != null) {
				reservation.setCustomer(customer);
				customer.getReservations().add(reservation);
			}

			Vehicle vehicle = findVehicleByID(vehicles, parseInt(filePath, line, parts[2]));
			if (vehicle != null) {
				reservation.setVehicle(vehicle);
				vehicle.getReservations().add(reservation);
			}

			Payment prepayment = findPaymentByID(payments, parseInt(filePath, line, parts[8]));
			if (prepayment != null) {
				reservation.setPrepayment(prepayment);
				prepayment.setReservation(reservation);
			}

			for (int addonID : parseIDList(filePath, line, parts[10])) {
				Addon addon = findAddonByID(addons, addonID);
				if (addon != null) {
					reservation.addAddon(addon);
				}
			}
			reservations.add(reservation);
		}
		scanner.close();
		return reservations;
	}

	public ArrayList<RentalContract> loadRentalContracts(ArrayList<Reservation> reservations,
			ArrayList<RentalAgent> rentalAgents, ArrayList<Payment> payments, ArrayList<Addon> addons)
			throws FileNotFoundException, InvalidFileFormatException {
		return loadRentalContracts(RENTAL_CONTRACTS_FILE_PATH, reservations, rentalAgents, payments, addons);
	}

	public ArrayList<RentalContract> loadRentalContracts(String filePath, ArrayList<Reservation> reservations,
			ArrayList<RentalAgent> rentalAgents, ArrayList<Payment> payments, ArrayList<Addon> addons)
			throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<RentalContract> rentalContracts = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 13);
			RentalContract contract = new RentalContract(
					parseInt(filePath, line, parts[0]),
					parseDate(filePath, line, parts[3]),
					parseDate(filePath, line, parts[4]),
					ContractStatus.valueOf(parts[9]));
			contract.setActualReturnDate(parseOptionalDate(filePath, line, parts[5]));
			contract.setDepositAmount(parseDouble(filePath, line, parts[6]));
			contract.setInitialMileage(parseInt(filePath, line, parts[7]));
			contract.setFinalMileage(parseInt(filePath, line, parts[8]));

			Reservation reservation = findReservationByID(reservations, parseInt(filePath, line, parts[1]));
			if (reservation != null) {
				contract.setReservation(reservation);
				reservation.setRentalContract(contract);
			}

			RentalAgent rentalAgent = findRentalAgentByID(rentalAgents, parseInt(filePath, line, parts[2]));
			if (rentalAgent != null) {
				contract.setRentalAgent(rentalAgent);
				rentalAgent.getRentalContracts().add(contract);
			}

			Payment pickupPayment = findPaymentByID(payments, parseInt(filePath, line, parts[11]));
			if (pickupPayment != null) {
				contract.setPickupPayment(pickupPayment);
				pickupPayment.setRentalContract(contract);
			}

			for (int addonID : parseIDList(filePath, line, parts[12])) {
				Addon addon = findAddonByID(addons, addonID);
				if (addon != null) {
					contract.addAddon(addon);
				}
			}
			rentalContracts.add(contract);
		}
		scanner.close();
		return rentalContracts;
	}

	public ArrayList<DamageAssessment> loadDamageAssessments(ArrayList<Vehicle> vehicles,
			ArrayList<RentalAgent> rentalAgents) throws FileNotFoundException, InvalidFileFormatException {
		return loadDamageAssessments(DAMAGE_ASSESSMENTS_FILE_PATH, vehicles, rentalAgents);
	}

	public ArrayList<DamageAssessment> loadDamageAssessments(String filePath, ArrayList<Vehicle> vehicles,
			ArrayList<RentalAgent> rentalAgents) throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<DamageAssessment> assessments = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 7);
			DamageAssessment assessment = new DamageAssessment(
					parseInt(filePath, line, parts[0]),
					parseDate(filePath, line, parts[4]),
					parts[5],
					parseDouble(filePath, line, parts[6]));

			Vehicle vehicle = findVehicleByID(vehicles, parseInt(filePath, line, parts[1]));
			if (vehicle != null) {
				assessment.setVehicle(vehicle);
				vehicle.getDamageAssessments().add(assessment);
			}

			RentalAgent rentalAgent = findRentalAgentByID(rentalAgents, parseInt(filePath, line, parts[2]));
			if (rentalAgent != null) {
				assessment.setRentalAgent(rentalAgent);
				rentalAgent.getDamageAssessments().add(assessment);
			}
			assessments.add(assessment);
		}
		scanner.close();
		return assessments;
	}

	public ArrayList<Invoice> loadInvoices(ArrayList<RentalContract> rentalContracts,
			ArrayList<DamageAssessment> assessments, ArrayList<Payment> payments)
			throws FileNotFoundException, InvalidFileFormatException {
		return loadInvoices(INVOICES_FILE_PATH, rentalContracts, assessments, payments);
	}

	public ArrayList<Invoice> loadInvoices(String filePath, ArrayList<RentalContract> rentalContracts,
			ArrayList<DamageAssessment> assessments, ArrayList<Payment> payments)
			throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<Invoice> invoices = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 11);
			Invoice invoice = new Invoice(
					parseInt(filePath, line, parts[0]),
					parseDouble(filePath, line, parts[3]),
					parseDouble(filePath, line, parts[4]),
					parseDouble(filePath, line, parts[5]));
			invoice.setDiscountAmount(parseDouble(filePath, line, parts[6]));
			invoice.setTotalAmount(parseDouble(filePath, line, parts[7]));
			invoice.setRefundAmount(parseDouble(filePath, line, parts[8]));
			invoice.setAdditionalCharges(parseDouble(filePath, line, parts[9]));

			RentalContract contract = findRentalContractByID(rentalContracts, parseInt(filePath, line, parts[1]));
			if (contract != null) {
				invoice.setRentalContract(contract);
				contract.setInvoice(invoice);
			}

			DamageAssessment assessment = findDamageAssessmentByID(assessments, parseInt(filePath, line, parts[2]));
			if (assessment != null) {
				invoice.setDamageAssessment(assessment);
				assessment.setInvoice(invoice);
			}

			for (int paymentID : parseIDList(filePath, line, parts[10])) {
				Payment payment = findPaymentByID(payments, paymentID);
				if (payment != null) {
					invoice.addPayment(payment);
				}
			}
			invoices.add(invoice);
		}
		scanner.close();
		return invoices;
	}

	public ArrayList<BranchReport> loadBranchReports(ArrayList<Branch> branches, ArrayList<BranchManager> branchManagers)
			throws FileNotFoundException, InvalidFileFormatException {
		return loadBranchReports(BRANCH_REPORTS_FILE_PATH, branches, branchManagers);
	}

	public ArrayList<BranchReport> loadBranchReports(String filePath, ArrayList<Branch> branches,
			ArrayList<BranchManager> branchManagers) throws FileNotFoundException, InvalidFileFormatException {
		ArrayList<BranchReport> branchReports = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = splitLine(filePath, line, 9);
			BranchReport report = new BranchReport(
					parseInt(filePath, line, parts[0]),
					parseDate(filePath, line, parts[3]));
			report.setTotalVehicles(parseInt(filePath, line, parts[4]));
			report.setTotalReservations(parseInt(filePath, line, parts[5]));
			report.setAvailableVehicles(parseInt(filePath, line, parts[6]));
			report.setRentedVehicles(parseInt(filePath, line, parts[7]));
			report.setTotalRevenue(parseDouble(filePath, line, parts[8]));

			Branch branch = findBranchByID(branches, parseInt(filePath, line, parts[1]));
			if (branch != null) {
				report.setBranch(branch);
				branch.getBranchReports().add(report);
			}

			BranchManager branchManager = findBranchManagerByID(branchManagers, parseInt(filePath, line, parts[2]));
			if (branchManager != null) {
				report.setGeneratedBy(branchManager);
				branchManager.getGeneratedReports().add(report);
			}
			branchReports.add(report);
		}
		scanner.close();
		return branchReports;
	}

	public void saveBranches(ArrayList<Branch> branches) throws FileNotFoundException {
		saveBranches(branches, BRANCHES_FILE_PATH);
	}

	public void saveBranches(ArrayList<Branch> branches, String filePath) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (Branch branch : branches) {
			writer.println(branch.getBranchID() + "|" + branch.getName() + "|" + branch.getAddress());
		}
		writer.close();
	}

	public void saveCustomers(ArrayList<Customer> customers) throws FileNotFoundException {
		saveCustomers(customers, CUSTOMERS_FILE_PATH);
	}

	public void saveCustomers(ArrayList<Customer> customers, String filePath) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (Customer customer : customers) {
			writer.println(customer.getUserID() + "|" + customer.getName() + "|" + customer.getSurname() + "|"
					+ customer.getEmail() + "|" + customer.getLoyaltyPoints() + "|" + customer.getLoyaltyTier());
		}
		writer.close();
	}

	public void saveEmployees(ArrayList<Employee> employees) throws FileNotFoundException {
		saveEmployees(employees, EMPLOYEES_FILE_PATH);
	}

	public void saveEmployees(ArrayList<Employee> employees, String filePath) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (Employee employee : employees) {
			writer.println(employee.getEmployeeID() + "|" + getEmployeeType(employee) + "|" + employee.getUserID()
					+ "|" + employee.getName() + "|" + employee.getSurname() + "|" + employee.getEmail() + "|"
					+ employee.getSalary() + "|" + getBranchID(employee.getBranch()) + "|"
					+ getManagedBranchID(employee));
		}
		writer.close();
	}

	public void saveVehicles(ArrayList<Vehicle> vehicles) throws FileNotFoundException {
		saveVehicles(vehicles, VEHICLES_FILE_PATH);
	}

	public void saveVehicles(ArrayList<Vehicle> vehicles, String filePath) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (Vehicle vehicle : vehicles) {
			int branchID = 0;
			if (vehicle.getBranch() != null) {
				branchID = vehicle.getBranch().getBranchID();
			}
			writer.println(vehicle.getVehicleID() + "|" + getVehicleType(vehicle) + "|" + vehicle.getPlateNumber()
					+ "|" + vehicle.getBrand() + "|" + vehicle.getModel() + "|" + vehicle.getYear() + "|"
					+ vehicle.getDailyRate() + "|" + vehicle.getInsuranceOption() + "|" + vehicle.getMileagePolicy()
					+ "|" + vehicle.getStatus() + "|" + vehicle.getCurrentMileage() + "|"
					+ vehicle.getMaintenanceInterval() + "|" + vehicle.getLastMaintenanceMileage() + "|" + branchID);
		}
		writer.close();
	}

	public void saveMaintenanceTasks(ArrayList<MaintenanceTask> maintenanceTasks) throws FileNotFoundException {
		saveMaintenanceTasks(maintenanceTasks, MAINTENANCE_TASKS_FILE_PATH);
	}

	public void saveMaintenanceTasks(ArrayList<MaintenanceTask> maintenanceTasks, String filePath)
			throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (MaintenanceTask task : maintenanceTasks) {
			writer.println(task.getMaintenanceID() + "|" + getVehicleID(task.getVehicle()) + "|"
					+ getMechanicID(task.getMechanic()) + "|" + formatDate(task.getMaintenanceDate()) + "|"
					+ task.getDescription() + "|" + task.getStatus());
		}
		writer.close();
	}

	public void saveAddons(ArrayList<Addon> addons) throws FileNotFoundException {
		saveAddons(addons, ADDONS_FILE_PATH);
	}

	public void saveAddons(ArrayList<Addon> addons, String filePath) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (Addon addon : addons) {
			writer.println(addon.getAddonID() + "|" + addon.getName() + "|" + addon.getDescription() + "|"
					+ addon.getDailyPrice());
		}
		writer.close();
	}

	public void savePayments(ArrayList<Payment> payments) throws FileNotFoundException {
		savePayments(payments, PAYMENTS_FILE_PATH);
	}

	public void savePayments(ArrayList<Payment> payments, String filePath) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (Payment payment : payments) {
			writer.println(payment.getPaymentID() + "|" + payment.getAmount() + "|"
					+ formatDate(payment.getPaymentDate()) + "|" + payment.getPaymentPurpose() + "|"
					+ getCustomerID(payment) + "|" + getReservationID(payment.getReservation()) + "|"
					+ getContractID(payment.getRentalContract()) + "|" + getInvoiceID(payment.getInvoice()));
		}
		writer.close();
	}

	public void saveReservations(ArrayList<Reservation> reservations) throws FileNotFoundException {
		saveReservations(reservations, RESERVATIONS_FILE_PATH);
	}

	public void saveReservations(ArrayList<Reservation> reservations, String filePath) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (Reservation reservation : reservations) {
			writer.println(reservation.getReservationID() + "|" + getCustomerID(reservation.getCustomer()) + "|"
					+ getVehicleID(reservation.getVehicle()) + "|" + formatDate(reservation.getStartDate()) + "|"
					+ formatDate(reservation.getEndDate()) + "|" + reservation.getStatus() + "|"
					+ reservation.getPrePaymentAmount() + "|" + reservation.getDepositAmount() + "|"
					+ getPaymentID(reservation.getPrepayment()) + "|"
					+ getContractID(reservation.getRentalContract()) + "|" + formatAddonIDs(reservation.getAddons()));
		}
		writer.close();
	}

	public void saveRentalContracts(ArrayList<RentalContract> rentalContracts) throws FileNotFoundException {
		saveRentalContracts(rentalContracts, RENTAL_CONTRACTS_FILE_PATH);
	}

	public void saveRentalContracts(ArrayList<RentalContract> rentalContracts, String filePath)
			throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (RentalContract contract : rentalContracts) {
			writer.println(contract.getContractID() + "|" + getReservationID(contract.getReservation()) + "|"
					+ getRentalAgentID(contract.getRentalAgent()) + "|" + formatDate(contract.getPickupDate()) + "|"
					+ formatDate(contract.getExpectedReturnDate()) + "|" + formatDate(contract.getActualReturnDate())
					+ "|" + contract.getDepositAmount() + "|" + contract.getInitialMileage() + "|"
					+ contract.getFinalMileage() + "|" + contract.getStatus() + "|" + getInvoiceID(contract.getInvoice())
					+ "|" + getPaymentID(contract.getPickupPayment()) + "|" + formatAddonIDs(contract.getAddons()));
		}
		writer.close();
	}

	public void saveDamageAssessments(ArrayList<DamageAssessment> assessments) throws FileNotFoundException {
		saveDamageAssessments(assessments, DAMAGE_ASSESSMENTS_FILE_PATH);
	}

	public void saveDamageAssessments(ArrayList<DamageAssessment> assessments, String filePath)
			throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (DamageAssessment assessment : assessments) {
			writer.println(assessment.getAssessmentID() + "|" + getVehicleID(assessment.getVehicle()) + "|"
					+ getRentalAgentID(assessment.getRentalAgent()) + "|" + getInvoiceID(assessment.getInvoice()) + "|"
					+ formatDate(assessment.getAssessmentDate()) + "|" + assessment.getDescription() + "|"
					+ assessment.getDamageCost());
		}
		writer.close();
	}

	public void saveInvoices(ArrayList<Invoice> invoices) throws FileNotFoundException {
		saveInvoices(invoices, INVOICES_FILE_PATH);
	}

	public void saveInvoices(ArrayList<Invoice> invoices, String filePath) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (Invoice invoice : invoices) {
			writer.println(invoice.getInvoiceID() + "|" + getContractID(invoice.getRentalContract()) + "|"
					+ getDamageAssessmentID(invoice.getDamageAssessment()) + "|" + invoice.getBaseAmount() + "|"
					+ invoice.getDamageFee() + "|" + invoice.getAddonFee() + "|" + invoice.getDiscountAmount() + "|"
					+ invoice.getTotalAmount() + "|" + invoice.getRefundAmount() + "|"
					+ invoice.getAdditionalCharges() + "|" + formatPaymentIDs(invoice.getPayments()));
		}
		writer.close();
	}

	public void saveBranchReports(ArrayList<BranchReport> branchReports) throws FileNotFoundException {
		saveBranchReports(branchReports, BRANCH_REPORTS_FILE_PATH);
	}

	public void saveBranchReports(ArrayList<BranchReport> branchReports, String filePath) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(filePath);
		for (BranchReport report : branchReports) {
			writer.println(report.getReportID() + "|" + getBranchID(report.getBranch()) + "|"
					+ getBranchManagerID(report.getGeneratedBy()) + "|" + formatDate(report.getGeneratedDate()) + "|"
					+ report.getTotalVehicles() + "|" + report.getTotalReservations() + "|"
					+ report.getAvailableVehicles() + "|" + report.getRentedVehicles() + "|"
					+ report.getTotalRevenue());
		}
		writer.close();
	}

	private Vehicle createVehicle(String filePath, String line, String[] parts) throws InvalidFileFormatException {
		int vehicleID = parseInt(filePath, line, parts[0]);
		String type = parts[1];
		String plateNumber = parts[2];
		String brand = parts[3];
		String model = parts[4];
		int year = parseInt(filePath, line, parts[5]);
		double dailyRate = parseDouble(filePath, line, parts[6]);

		Vehicle vehicle;
		if (type.equals("SUV")) {
			vehicle = new SUV(vehicleID, plateNumber, brand, model, year, dailyRate);
		} else if (type.equals("VAN")) {
			vehicle = new Van(vehicleID, plateNumber, brand, model, year, dailyRate);
		} else if (type.equals("LUXURY")) {
			vehicle = new Luxury(vehicleID, plateNumber, brand, model, year, dailyRate);
		} else {
			vehicle = new Economy(vehicleID, plateNumber, brand, model, year, dailyRate);
		}

		vehicle.setInsuranceOption(InsuranceOption.valueOf(parts[7]));
		vehicle.setMileagePolicy(MileagePolicy.valueOf(parts[8]));
		vehicle.setStatus(VehicleStatus.valueOf(parts[9]));
		vehicle.setCurrentMileage(parseInt(filePath, line, parts[10]));
		vehicle.setMaintenanceInterval(parseInt(filePath, line, parts[11]));
		vehicle.setLastMaintenanceMileage(parseInt(filePath, line, parts[12]));
		return vehicle;
	}

	private Employee createEmployee(String filePath, String line, String[] parts, ArrayList<Branch> branches)
			throws InvalidFileFormatException {
		String type = parts[1];
		Employee employee;
		if (type.equals("BRANCH_MANAGER")) {
			BranchManager branchManager = new BranchManager();
			Branch managedBranch = findBranchByID(branches, parseInt(filePath, line, parts[8]));
			branchManager.setManagedBranch(managedBranch);
			if (managedBranch != null) {
				managedBranch.setBranchManager(branchManager);
			}
			employee = branchManager;
		} else if (type.equals("MECHANIC")) {
			employee = new Mechanic();
		} else {
			employee = new RentalAgent();
		}

		employee.setEmployeeID(parseInt(filePath, line, parts[0]));
		employee.setUserID(parseInt(filePath, line, parts[2]));
		employee.setName(parts[3]);
		employee.setSurname(parts[4]);
		employee.setEmail(parts[5]);
		employee.setSalary(parseDouble(filePath, line, parts[6]));
		return employee;
	}

	private Branch findBranchByID(ArrayList<Branch> branches, int branchID) {
		for (Branch branch : branches) {
			if (branch.getBranchID() == branchID) {
				return branch;
			}
		}
		return null;
	}

	private Customer findCustomerByID(ArrayList<Customer> customers, int customerID) {
		for (Customer customer : customers) {
			if (customer.getUserID() == customerID) {
				return customer;
			}
		}
		return null;
	}

	private Vehicle findVehicleByID(ArrayList<Vehicle> vehicles, int vehicleID) {
		for (Vehicle vehicle : vehicles) {
			if (vehicle.getVehicleID() == vehicleID) {
				return vehicle;
			}
		}
		return null;
	}

	private Addon findAddonByID(ArrayList<Addon> addons, int addonID) {
		for (Addon addon : addons) {
			if (addon.getAddonID() == addonID) {
				return addon;
			}
		}
		return null;
	}

	private Payment findPaymentByID(ArrayList<Payment> payments, int paymentID) {
		if (paymentID == 0) {
			return null;
		}
		for (Payment payment : payments) {
			if (payment.getPaymentID() == paymentID) {
				return payment;
			}
		}
		return null;
	}

	private Reservation findReservationByID(ArrayList<Reservation> reservations, int reservationID) {
		if (reservationID == 0) {
			return null;
		}
		for (Reservation reservation : reservations) {
			if (reservation.getReservationID() == reservationID) {
				return reservation;
			}
		}
		return null;
	}

	private RentalContract findRentalContractByID(ArrayList<RentalContract> rentalContracts, int contractID) {
		if (contractID == 0) {
			return null;
		}
		for (RentalContract contract : rentalContracts) {
			if (contract.getContractID() == contractID) {
				return contract;
			}
		}
		return null;
	}

	private RentalAgent findRentalAgentByID(ArrayList<RentalAgent> rentalAgents, int rentalAgentID) {
		if (rentalAgentID == 0) {
			return null;
		}
		for (RentalAgent rentalAgent : rentalAgents) {
			if (rentalAgent.getEmployeeID() == rentalAgentID) {
				return rentalAgent;
			}
		}
		return null;
	}

	private Mechanic findMechanicByID(ArrayList<Mechanic> mechanics, int mechanicID) {
		if (mechanicID == 0) {
			return null;
		}
		for (Mechanic mechanic : mechanics) {
			if (mechanic.getEmployeeID() == mechanicID) {
				return mechanic;
			}
		}
		return null;
	}

	private BranchManager findBranchManagerByID(ArrayList<BranchManager> branchManagers, int branchManagerID) {
		if (branchManagerID == 0) {
			return null;
		}
		for (BranchManager branchManager : branchManagers) {
			if (branchManager.getEmployeeID() == branchManagerID) {
				return branchManager;
			}
		}
		return null;
	}

	private DamageAssessment findDamageAssessmentByID(ArrayList<DamageAssessment> assessments, int assessmentID) {
		if (assessmentID == 0) {
			return null;
		}
		for (DamageAssessment assessment : assessments) {
			if (assessment.getAssessmentID() == assessmentID) {
				return assessment;
			}
		}
		return null;
	}

	private String getEmployeeType(Employee employee) {
		if (employee instanceof BranchManager) {
			return "BRANCH_MANAGER";
		}
		if (employee instanceof Mechanic) {
			return "MECHANIC";
		}
		return "RENTAL_AGENT";
	}

	private String getVehicleType(Vehicle vehicle) {
		if (vehicle instanceof SUV) {
			return "SUV";
		}
		if (vehicle instanceof Van) {
			return "VAN";
		}
		if (vehicle instanceof Luxury) {
			return "LUXURY";
		}
		return "ECONOMY";
	}

	private String[] splitLine(String filePath, String line, int expectedPartCount) throws InvalidFileFormatException {
		String[] parts = line.split("\\|", -1);
		validatePartCount(filePath, line, parts, expectedPartCount);
		return parts;
	}

	private void validatePartCount(String filePath, String line, String[] parts, int expectedPartCount)
			throws InvalidFileFormatException {
		if (parts.length < expectedPartCount) {
			throw new InvalidFileFormatException(
					"Pattern incorrect in " + filePath + ": expected " + expectedPartCount
							+ " parts but found " + parts.length + " in line: " + line);
		}
	}

	private int parseInt(String filePath, String line, String value) throws InvalidFileFormatException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException exception) {
			throw new InvalidFileFormatException("Invalid integer in " + filePath + " line: " + line, exception);
		}
	}

	private double parseDouble(String filePath, String line, String value) throws InvalidFileFormatException {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException exception) {
			throw new InvalidFileFormatException("Invalid double in " + filePath + " line: " + line, exception);
		}
	}

	private Date parseDate(String filePath, String line, String value) throws InvalidFileFormatException {
		if (value.isEmpty()) {
			throw new InvalidFileFormatException("Missing date in " + filePath + " line: " + line);
		}
		return parseOptionalDate(filePath, line, value);
	}

	private Date parseOptionalDate(String filePath, String line, String value) throws InvalidFileFormatException {
		if (value.isEmpty()) {
			return null;
		}
		try {
			return DATE_FORMAT.parse(value);
		} catch (ParseException exception) {
			throw new InvalidFileFormatException("Invalid date in " + filePath + " line: " + line, exception);
		}
	}

	private String formatDate(Date date) {
		if (date == null) {
			return "";
		}
		return DATE_FORMAT.format(date);
	}

	private ArrayList<Integer> parseIDList(String filePath, String line, String value) throws InvalidFileFormatException {
		ArrayList<Integer> ids = new ArrayList<>();
		if (value.isEmpty()) {
			return ids;
		}
		String[] values = value.split(",");
		for (String id : values) {
			ids.add(parseInt(filePath, line, id));
		}
		return ids;
	}

	private String formatAddonIDs(ArrayList<Addon> addons) {
		String result = "";
		for (Addon addon : addons) {
			if (!result.isEmpty()) {
				result += ",";
			}
			result += addon.getAddonID();
		}
		return result;
	}

	private String formatPaymentIDs(ArrayList<Payment> payments) {
		String result = "";
		for (Payment payment : payments) {
			if (!result.isEmpty()) {
				result += ",";
			}
			result += payment.getPaymentID();
		}
		return result;
	}

	private int getCustomerID(Customer customer) {
		if (customer == null) {
			return 0;
		}
		return customer.getUserID();
	}

	private int getCustomerID(Payment payment) {
		if (payment.getCustomer() != null) {
			return payment.getCustomer().getUserID();
		}
		return payment.getCustomerID();
	}

	private int getVehicleID(Vehicle vehicle) {
		if (vehicle == null) {
			return 0;
		}
		return vehicle.getVehicleID();
	}

	private int getBranchID(Branch branch) {
		if (branch == null) {
			return 0;
		}
		return branch.getBranchID();
	}

	private int getManagedBranchID(Employee employee) {
		if (employee instanceof BranchManager) {
			Branch managedBranch = ((BranchManager) employee).getManagedBranch();
			return getBranchID(managedBranch);
		}
		return 0;
	}

	private int getReservationID(Reservation reservation) {
		if (reservation == null) {
			return 0;
		}
		return reservation.getReservationID();
	}

	private int getContractID(RentalContract contract) {
		if (contract == null) {
			return 0;
		}
		return contract.getContractID();
	}

	private int getRentalAgentID(RentalAgent rentalAgent) {
		if (rentalAgent == null) {
			return 0;
		}
		return rentalAgent.getEmployeeID();
	}

	private int getMechanicID(Mechanic mechanic) {
		if (mechanic == null) {
			return 0;
		}
		return mechanic.getEmployeeID();
	}

	private int getBranchManagerID(BranchManager branchManager) {
		if (branchManager == null) {
			return 0;
		}
		return branchManager.getEmployeeID();
	}

	private int getInvoiceID(Invoice invoice) {
		if (invoice == null) {
			return 0;
		}
		return invoice.getInvoiceID();
	}

	private int getPaymentID(Payment payment) {
		if (payment == null) {
			return 0;
		}
		return payment.getPaymentID();
	}

	private int getDamageAssessmentID(DamageAssessment assessment) {
		if (assessment == null) {
			return 0;
		}
		return assessment.getAssessmentID();
	}
}
