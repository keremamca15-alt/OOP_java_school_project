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
		this.reservationID = reservationID;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
	}

	public int getReservationID() {
		return reservationID;
	}

	public void setReservationID(int reservationID) {
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
		this.prePaymentAmount = prePaymentAmount;
	}

	public double getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(double depositAmount) {
		this.depositAmount = depositAmount;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Payment getPrepayment() {
		return prepayment;
	}

	public void setPrepayment(Payment prepayment) {
		this.prepayment = prepayment;
	}

	public RentalContract getRentalContract() {
		return rentalContract;
	}

	public void setRentalContract(RentalContract rentalContract) {
		this.rentalContract = rentalContract;
	}

	public ArrayList<Addon> getAddons() {
		return addons;
	}

	public void setAddons(ArrayList<Addon> addons) {
		this.addons = addons;
	}

	public void confirmReservation() {
		status = ReservationStatus.CONFIRMED;
	}

	public void cancelReservation() {
		status = ReservationStatus.CANCELLED;
	}

	public int calculateDuration() {
		if (startDate == null || endDate == null || !endDate.after(startDate)) {
			return 0;
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
		addons.add(addon);
	}

}
