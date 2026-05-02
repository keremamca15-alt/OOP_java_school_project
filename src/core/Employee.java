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
		if (employeeID < 0) {
			throw new IllegalArgumentException("Employee ID cannot be negative.");
		}
		this.employeeID = employeeID;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		if (salary < 0) {
			throw new IllegalArgumentException("Salary cannot be negative.");
		}
		this.salary = salary;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
		if (branch != null && !branch.getEmployees().contains(this)) {
			branch.getEmployees().add(this);
		}
	}

}
