package core;

import java.util.ArrayList;
import java.util.Date;

public class RentalAgent extends Employee {

	private static final double DEFAULT_DEPOSIT_AMOUNT = 300.0;

	private ArrayList<DamageAssessment> damageAssessments = new ArrayList<>();
	private ArrayList<RentalContract> rentalContracts = new ArrayList<>();

	public RentalAgent() {
	}

	public ArrayList<DamageAssessment> getDamageAssessments() {
		return damageAssessments;
	}

	public void setDamageAssessments(ArrayList<DamageAssessment> damageAssessments) {
		this.damageAssessments = new ArrayList<>();
		if (damageAssessments != null) {
			for (DamageAssessment assessment : damageAssessments) {
				if (assessment != null && !this.damageAssessments.contains(assessment)) {
					this.damageAssessments.add(assessment);
					assessment.setRentalAgent(this);
				}
			}
		}
	}

	public ArrayList<RentalContract> getRentalContracts() {
		return rentalContracts;
	}

	public void setRentalContracts(ArrayList<RentalContract> rentalContracts) {
		this.rentalContracts = new ArrayList<>();
		if (rentalContracts != null) {
			for (RentalContract contract : rentalContracts) {
				if (contract != null && !this.rentalContracts.contains(contract)) {
					this.rentalContracts.add(contract);
					contract.setRentalAgent(this);
				}
			}
		}
	}

	/**
	 * 
	 * @param reservation
	 */
	public void processPickup(Reservation reservation) throws InvalidReservationException, VehicleNotAvailableException {
		validatePickup(reservation);

		RentalContract contract = reservation.getRentalContract();
		if (contract == null) {
			contract = new RentalContract(
					rentalContracts.size() + 1,
					reservation.getStartDate(),
					reservation.getEndDate(),
					ContractStatus.ACTIVE);
			contract.setReservation(reservation);
			contract.setRentalAgent(this);
			contract.setInitialMileage(reservation.getVehicle().getCurrentMileage());
			reservation.setRentalContract(contract);
			rentalContracts.add(contract);
		} else {
			contract.setStatus(ContractStatus.ACTIVE);
			contract.setRentalAgent(this);
			if (!rentalContracts.contains(contract)) {
				rentalContracts.add(contract);
			}
		}

		preparePickupDeposit(reservation, contract);
		reservation.getVehicle().setStatus(VehicleStatus.RENTED);
	}

	/**
	 * 
	 * @param reservation
	 */
	public Invoice processReturn(Reservation reservation) throws InvalidReservationException {
		return processReturn(reservation, null);
	}

	public Invoice processReturn(Reservation reservation, DamageAssessment damageAssessment)
			throws InvalidReservationException {
		validateReturn(reservation);
		return createReturnInvoice(reservation, damageAssessment);
	}

	public Invoice processReturn(Reservation reservation, DamageAssessment damageAssessment, int finalMileage)
			throws InvalidReservationException {
		validateReturn(reservation);
		RentalContract contract = reservation.getRentalContract();
		if (finalMileage < contract.getInitialMileage()) {
			throw new InvalidReservationException("Final mileage cannot be lower than initial mileage.");
		}
		contract.setFinalMileage(finalMileage);
		return createReturnInvoice(reservation, damageAssessment);
	}

	private Invoice createReturnInvoice(Reservation reservation, DamageAssessment damageAssessment) {
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

		Invoice invoice = new Invoice(createNextInvoiceID(), baseAmount, damageFee, addonFee);
		invoice.setRentalContract(contract);
		if (damageAssessment != null) {
			invoice.setDamageAssessment(damageAssessment);
			damageAssessment.setInvoice(invoice);
		}

		if (vehicle.getMileagePolicy() != null) {
			double extraKmCharge = vehicle.getMileagePolicy()
					.calculateExtraCharge(days, contract.calculateUsedMileage());
			invoice.addAdditionalCharge(extraKmCharge);
		}

		invoice.calculateTotal();

		contract.setInvoice(invoice);
		contract.closeContract();
		vehicle.setStatus(VehicleStatus.AVAILABLE);
		reservation.setStatus(ReservationStatus.COMPLETED);
		return invoice;
	}

	private void preparePickupDeposit(Reservation reservation, RentalContract contract) {
		double depositAmount = contract.getDepositAmount();
		if (depositAmount <= 0 && reservation.getDepositAmount() > 0) {
			depositAmount = reservation.getDepositAmount();
		}
		if (depositAmount <= 0) {
			depositAmount = DEFAULT_DEPOSIT_AMOUNT;
		}
		contract.setDepositAmount(depositAmount);
		reservation.setDepositAmount(depositAmount);
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
			throw new IllegalArgumentException("Damage assessment vehicle cannot be empty.");
		}

		DamageAssessment assessment = new DamageAssessment(
				damageAssessments.size() + 1,
				new Date(),
				description,
				damageCost);
		assessment.setVehicle(vehicle);
		assessment.setRentalAgent(this);
		return assessment;
	}

	private void validatePickup(Reservation reservation)
			throws InvalidReservationException, VehicleNotAvailableException {
		if (reservation == null) {
			throw new InvalidReservationException("Reservation cannot be empty.");
		}
		if (reservation.getVehicle() == null) {
			throw new InvalidReservationException("Reservation vehicle cannot be empty.");
		}
		if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
			throw new InvalidReservationException("Only confirmed reservations can be picked up.");
		}
		Vehicle vehicle = reservation.getVehicle();
		if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
			throw new VehicleNotAvailableException("Vehicle is not available for pickup.");
		}
	}

	private void validateReturn(Reservation reservation) throws InvalidReservationException {
		if (reservation == null || reservation.getVehicle() == null || reservation.getRentalContract() == null) {
			throw new InvalidReservationException("Reservation return information is incomplete.");
		}
		if (reservation.getRentalContract().getStatus() != ContractStatus.ACTIVE) {
			throw new InvalidReservationException("Only active contracts can be returned.");
		}
	}

	private int createNextInvoiceID() {
		int maxInvoiceID = 0;
		for (RentalContract contract : rentalContracts) {
			if (contract.getInvoice() != null && contract.getInvoice().getInvoiceID() > maxInvoiceID) {
				maxInvoiceID = contract.getInvoice().getInvoiceID();
			}
		}
		return maxInvoiceID + 1;
	}

}
