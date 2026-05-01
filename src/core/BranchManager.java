package core;

import java.util.ArrayList;

public class BranchManager extends Employee {

	private Branch managedBranch;
	private ArrayList<BranchReport> generatedReports = new ArrayList<>();

	public BranchManager() {
	}

	public BranchManager(Branch managedBranch) {
		this.managedBranch = managedBranch;
	}

	public Branch getManagedBranch() {
		return managedBranch;
	}

	public void setManagedBranch(Branch managedBranch) {
		this.managedBranch = managedBranch;
	}

	public ArrayList<BranchReport> getGeneratedReports() {
		return generatedReports;
	}

	public void setGeneratedReports(ArrayList<BranchReport> generatedReports) {
		this.generatedReports = generatedReports;
	}

	/**
	 * 
	 * @param employee
	 */
	public void addEmployee(Employee employee) {
	}

	/**
	 * 
	 * @param employeeID
	 */
	public void removeEmployee(int employeeID) {
	}

	/**
	 * 
	 * @param branch
	 */
	public BranchReport generateReport(Branch branch) {
		return null;
	}

}
