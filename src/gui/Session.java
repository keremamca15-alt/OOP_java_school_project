package gui;

import core.BranchManager;
import core.Customer;
import core.Mechanic;
import core.RentalAgent;

public class Session {

    public static final String ROLE_CUSTOMER = "Customer";
    public static final String ROLE_AGENT    = "Rental Agent";
    public static final String ROLE_MANAGER  = "Branch Manager";
    public static final String ROLE_MECHANIC = "Mechanic";
    public static final String ROLE_GUEST    = "Guest";

    private String username;
    private String role;
    private Customer customer;
    private RentalAgent rentalAgent;
    private BranchManager branchManager;
    private Mechanic mechanic;

    public String getUsername() { return username; }
    public String getRole()     { return role; }
    public Customer getCustomer() { return customer; }
    public RentalAgent getRentalAgent() { return rentalAgent; }
    public BranchManager getBranchManager() { return branchManager; }
    public Mechanic getMechanic() { return mechanic; }

    public void setUsername(String username) { this.username = username; }
    public void setRole(String role)         { this.role = role; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setRentalAgent(RentalAgent rentalAgent) { this.rentalAgent = rentalAgent; }
    public void setBranchManager(BranchManager branchManager) { this.branchManager = branchManager; }
    public void setMechanic(Mechanic mechanic) { this.mechanic = mechanic; }

    public void clear() {
        username      = null;
        role          = null;
        customer      = null;
        rentalAgent   = null;
        branchManager = null;
        mechanic      = null;
    }
}
