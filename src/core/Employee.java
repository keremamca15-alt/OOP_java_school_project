package core;

public abstract class Employee extends User {

	private int employeeID;
	private double salary;
	private Branch branch;

	public Employee() {
	}

	public Employee(int userID, String name, String surname, String email, int employeeID, double salary) {
		super(userID, name, surname, email);
		this.employeeID = employeeID;
		this.salary = salary;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

}
