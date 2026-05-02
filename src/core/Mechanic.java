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
		this.maintenanceTasks = new ArrayList<>();
		if (maintenanceTasks != null) {
			for (MaintenanceTask task : maintenanceTasks) {
				performMaintenance(task);
			}
		}
	}

	public ArrayList<MaintenanceTask> viewMaintenanceQueue() {
		return maintenanceTasks;
	}

	/**
	 * 
	 * @param record
	 */
	public void performMaintenance(MaintenanceTask task) {
		if (task != null) {
			task.setMechanic(this);
			if (!maintenanceTasks.contains(task)) {
				maintenanceTasks.add(task);
			}
		}
	}

}
