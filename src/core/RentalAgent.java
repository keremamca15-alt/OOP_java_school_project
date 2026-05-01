package core;

import java.util.ArrayList;

public class RentalAgent extends Employee {

	private ArrayList<DamageAssessment> damageAssessments = new ArrayList<>();
	private ArrayList<RentalContract> rentalContracts = new ArrayList<>();

	public RentalAgent() {
	}

	public ArrayList<DamageAssessment> getDamageAssessments() {
		return damageAssessments;
	}

	public void setDamageAssessments(ArrayList<DamageAssessment> damageAssessments) {
		this.damageAssessments = damageAssessments;
	}

	public ArrayList<RentalContract> getRentalContracts() {
		return rentalContracts;
	}

	public void setRentalContracts(ArrayList<RentalContract> rentalContracts) {
		this.rentalContracts = rentalContracts;
	}

	/**
	 * 
	 * @param reservation
	 */
	public void processPickup(Reservation reservation) {
	}

	/**
	 * 
	 * @param reservation
	 */
	public Invoice processReturn(Reservation reservation) {
		return null;
	}

	/**
	 * 
	 * @param vehicle
	 */
	public DamageAssessment assessDamage(Vehicle vehicle) {
		return null;
	}

}
