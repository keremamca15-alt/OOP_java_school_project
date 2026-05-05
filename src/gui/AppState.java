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
import core.VehicleStatus;

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
        applyMaintenanceStatusRules();
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

    public int createNextRentalContractID() {
        int maxID = 0;
        for (RentalContract contract : rentalContracts) {
            if (contract.getContractID() > maxID) {
                maxID = contract.getContractID();
            }
        }
        return maxID + 1;
    }

    public int createNextInvoiceID() {
        int maxID = 0;
        for (Invoice invoice : invoices) {
            if (invoice.getInvoiceID() > maxID) {
                maxID = invoice.getInvoiceID();
            }
        }
        return maxID + 1;
    }

    public int createNextDamageAssessmentID() {
        int maxID = 0;
        for (DamageAssessment assessment : damageAssessments) {
            if (assessment.getAssessmentID() > maxID) {
                maxID = assessment.getAssessmentID();
            }
        }
        return maxID + 1;
    }

    public int createNextPaymentID() {
        int maxID = 0;
        for (Payment payment : payments) {
            if (payment.getPaymentID() > maxID) {
                maxID = payment.getPaymentID();
            }
        }
        return maxID + 1;
    }

    public int createNextMaintenanceID() {
        int maxID = 0;
        for (MaintenanceTask task : maintenanceTasks) {
            if (task.getMaintenanceID() > maxID) {
                maxID = task.getMaintenanceID();
            }
        }
        return maxID + 1;
    }

    public int createNextEmployeeID() {
        int maxID = 0;
        for (Employee employee : employees) {
            if (employee.getEmployeeID() > maxID) {
                maxID = employee.getEmployeeID();
            }
        }
        return maxID + 1;
    }

    public int createNextUserID() {
        int maxID = 0;
        for (Customer customer : customers) {
            if (customer.getUserID() > maxID) {
                maxID = customer.getUserID();
            }
        }
        for (Employee employee : employees) {
            if (employee.getUserID() > maxID) {
                maxID = employee.getUserID();
            }
        }
        return maxID + 1;
    }

    public void addRentalContractIfMissing(RentalContract contract) {
        if (contract != null && !rentalContracts.contains(contract)) {
            rentalContracts.add(contract);
        }
    }

    public void addInvoiceIfMissing(Invoice invoice) {
        if (invoice != null && !invoices.contains(invoice)) {
            invoices.add(invoice);
        }
    }

    public void addDamageAssessmentIfMissing(DamageAssessment assessment) {
        if (assessment != null && !damageAssessments.contains(assessment)) {
            damageAssessments.add(assessment);
        }
    }

    public void addPaymentIfMissing(Payment payment) {
        if (payment != null && !payments.contains(payment)) {
            payments.add(payment);
        }
    }

    public void addMaintenanceTaskIfMissing(MaintenanceTask task) {
        if (task != null && !maintenanceTasks.contains(task)) {
            maintenanceTasks.add(task);
        }
    }

    public void addBranchReportIfMissing(BranchReport report) {
        if (report != null && !branchReports.contains(report)) {
            branchReports.add(report);
        }
    }

    public void addCustomerIfMissing(Customer customer) {
        if (customer != null && !customers.contains(customer)) {
            customers.add(customer);
        }
    }

    public void addEmployeeIfMissing(Employee employee) {
        if (employee != null && !employees.contains(employee)) {
            employees.add(employee);
            refreshEmployeeRoleLists();
        }
    }

    public void applyMaintenanceStatusRules() {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getStatus() == VehicleStatus.AVAILABLE && vehicle.needsMaintenance()) {
                vehicle.setStatus(VehicleStatus.OUT_OF_SERVICE);
            }
        }
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
