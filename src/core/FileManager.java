package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {
	private static final String BRANCHES_FILE_PATH = "data/branches.txt";
	private static final String CUSTOMERS_FILE_PATH = "data/customers.txt";
	private static final String VEHICLES_FILE_PATH = "data/vehicles.txt";

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
			String[] parts = line.split("\\|");
			validatePartCount(filePath, line, parts, 3);
			int branchID = Integer.parseInt(parts[0]);
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
			String[] parts = line.split("\\|");
			validatePartCount(filePath, line, parts, 6);
			int userID = Integer.parseInt(parts[0]);
			String name = parts[1];
			String surname = parts[2];
			String email = parts[3];
			int loyaltyPoints = Integer.parseInt(parts[4]);
			LoyaltyTier loyaltyTier = LoyaltyTier.valueOf(parts[5]);
			customers.add(new Customer(userID, name, surname, email, loyaltyPoints, loyaltyTier));
		}
		scanner.close();
		return customers;
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
			String[] parts = line.split("\\|");
			validatePartCount(filePath, line, parts, 14);
			Vehicle vehicle = createVehicle(parts);
			int branchID = Integer.parseInt(parts[13]);
			Branch branch = findBranchByID(branches, branchID);
			if (branch != null) {
				branch.addVehicle(vehicle);
			}
			vehicles.add(vehicle);
		}
		scanner.close();
		return vehicles;
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

	private Vehicle createVehicle(String[] parts) {
		int vehicleID = Integer.parseInt(parts[0]);
		String type = parts[1];
		String plateNumber = parts[2];
		String brand = parts[3];
		String model = parts[4];
		int year = Integer.parseInt(parts[5]);
		double dailyRate = Double.parseDouble(parts[6]);

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
		vehicle.setCurrentMileage(Integer.parseInt(parts[10]));
		vehicle.setMaintenanceInterval(Integer.parseInt(parts[11]));
		vehicle.setLastMaintenanceMileage(Integer.parseInt(parts[12]));
		return vehicle;
	}

	private Branch findBranchByID(ArrayList<Branch> branches, int branchID) {
		for (Branch branch : branches) {
			if (branch.getBranchID() == branchID) {
				return branch;
			}
		}
		return null;
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

	private void validatePartCount(String filePath, String line, String[] parts, int expectedPartCount)
			throws InvalidFileFormatException {
		if (parts.length < expectedPartCount) {
			throw new InvalidFileFormatException(
					"Pattern incorrect in " + filePath + ": expected " + expectedPartCount
							+ " parts but found " + parts.length + " in line: " + line);
		}
	}
}
