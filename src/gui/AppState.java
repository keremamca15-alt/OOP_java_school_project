package gui;

import core.Addon;
import core.Branch;
import core.BranchManager;
import core.BranchReport;
import core.Customer;
import core.DamageAssessment;
import core.Employee;
import core.FileManager;
import core.InvalidFileFormatException;
import core.Invoice;
import core.MaintenanceTask;
import core.Mechanic;
import core.Payment;
import core.RentalAgent;
import core.RentalContract;
import core.Reservation;
import core.Vehicle;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AppState {

    private final FileManager fileManager = new FileManager();

    private ArrayList<Branch> branches = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Employee> employees = new ArrayList<>();
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<MaintenanceTask> maintenanceTasks = new ArrayList<>();
    private ArrayList<Addon> addons = new ArrayList<>();
    private ArrayList<Payment> payments = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();
    private ArrayList<RentalContract> rentalContracts = new ArrayList<>();
    private ArrayList<DamageAssessment> damageAssessments = new ArrayList<>();
    private ArrayList<Invoice> invoices = new ArrayList<>();
    private ArrayList<BranchReport> branchReports = new ArrayList<>();

    private ArrayList<RentalAgent> rentalAgents = new ArrayList<>();
    private ArrayList<Mechanic> mechanics = new ArrayList<>();
    private ArrayList<BranchManager> branchManagers = new ArrayList<>();

    public void loadAll() throws FileNotFoundException, InvalidFileFormatException {
        branches = fileManager.loadBranches();
        customers = fileManager.loadCustomers();
        employees = fileManager.loadEmployees(branches);
        vehicles = fileManager.loadVehicles(branches);

        refreshEmployeeRoleLists();

        maintenanceTasks = fileManager.loadMaintenanceTasks(vehicles, mechanics);
        addons = fileManager.loadAddons();
        payments = fileManager.loadPayments(customers);
        reservations = fileManager.loadReservations(customers, vehicles, payments, addons);
        rentalContracts = fileManager.loadRentalContracts(reservations, rentalAgents, payments, addons);
        damageAssessments = fileManager.loadDamageAssessments(vehicles, rentalAgents);
        invoices = fileManager.loadInvoices(rentalContracts, damageAssessments, payments);
        branchReports = fileManager.loadBranchReports(branches, branchManagers);
    }

    public void saveAll() throws FileNotFoundException {
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
    }

    public ArrayList<Branch> getBranches() {
        return branches;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public ArrayList<MaintenanceTask> getMaintenanceTasks() {
        return maintenanceTasks;
    }

    public ArrayList<Addon> getAddons() {
        return addons;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    public ArrayList<RentalContract> getRentalContracts() {
        return rentalContracts;
    }

    public ArrayList<DamageAssessment> getDamageAssessments() {
        return damageAssessments;
    }

    public ArrayList<Invoice> getInvoices() {
        return invoices;
    }

    public ArrayList<BranchReport> getBranchReports() {
        return branchReports;
    }

    public ArrayList<RentalAgent> getRentalAgents() {
        return rentalAgents;
    }

    public ArrayList<Mechanic> getMechanics() {
        return mechanics;
    }

    public ArrayList<BranchManager> getBranchManagers() {
        return branchManagers;
    }

    public Customer findCustomerForLogin(String input) {
        String normalizedInput = normalize(input);
        for (Customer customer : customers) {
            if (normalize(customer.getEmail()).equals(normalizedInput)
                    || normalize(customer.getName() + " " + customer.getSurname()).equals(normalizedInput)) {
                return customer;
            }
        }
        return null;
    }

    public Employee findEmployeeForLogin(String input) {
        String normalizedInput = normalize(input);
        for (Employee employee : employees) {
            if (normalize(employee.getEmail()).equals(normalizedInput)
                    || normalize(employee.getName() + " " + employee.getSurname()).equals(normalizedInput)) {
                return employee;
            }
        }
        return null;
    }

    public int createNextReservationID() {
        int maxID = 0;
        for (Reservation reservation : reservations) {
            if (reservation.getReservationID() > maxID) {
                maxID = reservation.getReservationID();
            }
        }
        return maxID + 1;
    }

    private void refreshEmployeeRoleLists() {
        rentalAgents = new ArrayList<>();
        mechanics = new ArrayList<>();
        branchManagers = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee instanceof RentalAgent) {
                rentalAgents.add((RentalAgent) employee);
            }
            if (employee instanceof Mechanic) {
                mechanics.add((Mechanic) employee);
            }
            if (employee instanceof BranchManager) {
                branchManagers.add((BranchManager) employee);
            }
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}
