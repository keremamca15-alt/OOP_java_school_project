package core;

import java.util.ArrayList;

public class Mechanic extends Employee {

	private ArrayList<MaintenanceTask> maintenanceTasks = new ArrayList<>();

	public Mechanic() {
	}

	public ArrayList<MaintenanceTask> getMaintenanceTasks() {
		return maintenanceTasks;
	}

	public void setMaintenanceTasks(ArrayList<MaintenanceTask> maintenanceTasks) {
		this.maintenanceTasks = maintenanceTasks;
	}

	public ArrayList<MaintenanceTask> viewMaintenanceQueue() {
		return maintenanceTasks;
	}

	/**
	 * 
	 * @param record
	 */
	public void performMaintenance(MaintenanceTask task) {
		maintenanceTasks.add(task);
	}

}
