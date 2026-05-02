package core;

import java.util.ArrayList;
import java.util.Date;

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
		if (reservation == null || reservation.getVehicle() == null) {
			return;
		}

		RentalContract contract = new RentalContract(
				rentalContracts.size() + 1,
				reservation.getStartDate(),
				reservation.getEndDate(),
				ContractStatus.ACTIVE);
		contract.setReservation(reservation);
		contract.setRentalAgent(this);
		contract.setInitialMileage(reservation.getVehicle().getCurrentMileage());

		reservation.setRentalContract(contract);
		reservation.getVehicle().setStatus(VehicleStatus.RENTED);
		rentalContracts.add(contract);
	}

	/**
	 * 
	 * @param reservation
	 */
	public Invoice processReturn(Reservation reservation) {
		return processReturn(reservation, null);
	}

	public Invoice processReturn(Reservation reservation, DamageAssessment damageAssessment) {
		if (reservation == null || reservation.getVehicle() == null || reservation.getRentalContract() == null) {
			return null;
		}

		RentalContract contract = reservation.getRentalContract();
		Vehicle vehicle = reservation.getVehicle();
		int days = reservation.calculateDuration();

		double baseAmount = vehicle.calculateRentalCost(days);
		double addonFee = 0.0;
		for (Addon addon : contract.getAddons()) {
			addonFee += addon.calculateCost(days);
		}

		double damageFee = 0.0;
		if (damageAssessment != null) {
			damageFee = damageAssessment.getDamageCost();
		}

		Invoice invoice = new Invoice(rentalContracts.size(), baseAmount, damageFee, addonFee);
		invoice.setRentalContract(contract);
		if (damageAssessment != null) {
			invoice.setDamageAssessment(damageAssessment);
			damageAssessment.setInvoice(invoice);
		}
		contract.setInvoice(invoice);
		contract.closeContract();
		vehicle.setStatus(VehicleStatus.AVAILABLE);
		return invoice;
	}

	/**
	 * 
	 * @param vehicle
	 */
	public DamageAssessment assessDamage(Vehicle vehicle) {
		return assessDamage(vehicle, "Assessment pending", 0.0);
	}

	public DamageAssessment assessDamage(Vehicle vehicle, String description, double damageCost) {
		if (vehicle == null) {
			return null;
		}

		DamageAssessment assessment = new DamageAssessment(
				damageAssessments.size() + 1,
				new Date(),
				description,
				damageCost);
		assessment.setVehicle(vehicle);
		assessment.setRentalAgent(this);
		damageAssessments.add(assessment);
		vehicle.getDamageAssessments().add(assessment);
		return assessment;
	}

}
