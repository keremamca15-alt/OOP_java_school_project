package gui;

public class Session {

    public static final String ROLE_CUSTOMER = "Customer";
    public static final String ROLE_AGENT    = "Rental Agent";
    public static final String ROLE_MANAGER  = "Branch Manager";
    public static final String ROLE_MECHANIC = "Mechanic";
    public static final String ROLE_GUEST    = "Guest";

    private String username;
    private String role;

    public String getUsername() { return username; }
    public String getRole()     { return role; }

    public void setUsername(String username) { this.username = username; }
    public void setRole(String role)         { this.role = role; }

    public void clear() {
        username = null;
        role     = null;
    }
}
