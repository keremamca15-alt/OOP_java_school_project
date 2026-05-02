package core;

import java.util.Date;
import java.util.ArrayList;

public class Reservation {

	private int reservationID;
	private Date startDate;
	private Date endDate;
	private ReservationStatus status;
	private double prePaymentAmount;
	private double depositAmount;
	private Customer customer;
	private Vehicle vehicle;
	private Payment prepayment;
	private RentalContract rentalContract;
	private ArrayList<Addon> addons = new ArrayList<>();

	public Reservation() {
	}

	public Reservation(int reservationID, Date startDate, Date endDate, ReservationStatus status) {
		setReservationID(reservationID);
		setStartDate(startDate);
		setEndDate(endDate);
		setStatus(status);
	}

	public int getReservationID() {
		return reservationID;
	}

	public void setReservationID(int reservationID) {
		if (reservationID < 0) {
			throw new IllegalArgumentException("Reservation ID cannot be negative.");
		}
		this.reservationID = reservationID;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	public double getPrePaymentAmount() {
		return prePaymentAmount;
	}

	public void setPrePaymentAmount(double prePaymentAmount) {
		if (prePaymentAmount < 0) {
			throw new IllegalArgumentException("Prepayment amount cannot be negative.");
		}
		this.prePaymentAmount = prePaymentAmount;
	}

	public double getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(double depositAmount) {
		if (depositAmount < 0) {
			throw new IllegalArgumentException("Deposit amount cannot be negative.");
		}
		this.depositAmount = depositAmount;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
		if (customer != null && !customer.getReservations().contains(this)) {
			customer.getReservations().add(this);
		}
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
		if (vehicle != null && !vehicle.getReservations().contains(this)) {
			vehicle.getReservations().add(this);
		}
	}

	public Payment getPrepayment() {
		return prepayment;
	}

	public void setPrepayment(Payment prepayment) {
		this.prepayment = prepayment;
		if (prepayment != null && prepayment.getReservation() != this) {
			prepayment.setReservation(this);
		}
	}

	public RentalContract getRentalContract() {
		return rentalContract;
	}

	public void setRentalContract(RentalContract rentalContract) {
		this.rentalContract = rentalContract;
		if (rentalContract != null && rentalContract.getReservation() != this) {
			rentalContract.setReservation(this);
		}
	}

	public ArrayList<Addon> getAddons() {
		return addons;
	}

	public void setAddons(ArrayList<Addon> addons) {
		this.addons = new ArrayList<>();
		if (addons != null) {
			for (Addon addon : addons) {
				addAddon(addon);
			}
		}
	}

	public void confirmReservation() {
		status = ReservationStatus.CONFIRMED;
	}

	public void cancelReservation() {
		status = ReservationStatus.CANCELLED;
	}

	public int calculateDuration() {
		if (startDate == null || endDate == null) {
			throw new IllegalStateException("Reservation dates cannot be empty.");
		}
		if (!endDate.after(startDate)) {
			throw new IllegalStateException("Reservation end date must be after start date.");
		}
		long difference = endDate.getTime() - startDate.getTime();
		long dayInMillis = 24L * 60 * 60 * 1000;
		return (int) (difference / dayInMillis);
	}

	/**
	 * 
	 * @param addon
	 */
	public void addAddon(Addon addon) {
		if (addon != null && !addons.contains(addon)) {
			addons.add(addon);
		}
	}

}
