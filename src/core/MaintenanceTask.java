package core;

import java.util.Date;

public class MaintenanceTask {

	private int maintenanceID;
	private Date maintenanceDate;
	private String description;
	private MaintenanceStatus status;
	private Vehicle vehicle;
	private Mechanic mechanic;

	public MaintenanceTask() {
	}

	public MaintenanceTask(int maintenanceID, Date maintenanceDate, String description, MaintenanceStatus status) {
		this.maintenanceID = maintenanceID;
		this.maintenanceDate = maintenanceDate;
		this.description = description;
		this.status = status;
	}

	public int getMaintenanceID() {
		return maintenanceID;
	}

	public void setMaintenanceID(int maintenanceID) {
		this.maintenanceID = maintenanceID;
	}

	public Date getMaintenanceDate() {
		return maintenanceDate;
	}

	public void setMaintenanceDate(Date maintenanceDate) {
		this.maintenanceDate = maintenanceDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MaintenanceStatus getStatus() {
		return status;
	}

	public void setStatus(MaintenanceStatus status) {
		this.status = status;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Mechanic getMechanic() {
		return mechanic;
	}

	public void setMechanic(Mechanic mechanic) {
		this.mechanic = mechanic;
	}

	public void scheduleMaintenance() {
	}

	public void completeMaintenance() {
	}

}
