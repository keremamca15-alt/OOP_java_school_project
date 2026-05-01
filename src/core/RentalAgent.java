package core;

public class RentalAgent extends Employee {

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
