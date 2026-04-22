public class SessionManager {
    private String selectedRole;
    private String selectedUser;

    public String getSelectedRole() {
        return selectedRole;
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void selectRole(String role) {
        selectedRole = role;
        selectedUser = null;
    }

    public void loginAs(String user) {
        selectedUser = user;
    }

    public void logout() {
        selectedRole = null;
        selectedUser = null;
    }
}
