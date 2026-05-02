package core;

import java.util.ArrayList;
import java.util.Date;

public class Customer extends User {

	private int loyaltyPoints;
	private LoyaltyTier loyaltyTier;
	private ArrayList<Reservation> reservations = new ArrayList<>();
	private ArrayList<Payment> payments = new ArrayList<>();

	public Customer() {
	}

	public Customer(int userID, String name, String surname, String email, int loyaltyPoints, LoyaltyTier loyaltyTier) {
		super(userID, name, surname, email);
		this.loyaltyPoints = loyaltyPoints;
		this.loyaltyTier = loyaltyTier;
	}

	public int getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(int loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
	}

	public LoyaltyTier getLoyaltyTier() {
		return loyaltyTier;
	}

	public void setLoyaltyTier(LoyaltyTier loyaltyTier) {
		this.loyaltyTier = loyaltyTier;
	}

	public ArrayList<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(ArrayList<Reservation> reservations) {
		this.reservations = reservations;
	}

	public ArrayList<Payment> getPayments() {
		return payments;
	}

	public void setPayments(ArrayList<Payment> payments) {
		this.payments = payments;
	}

	public ArrayList<Vehicle> searchAvailableVehicles() {
		return new ArrayList<>();
	}

	public Reservation makeReservation() {
		return new Reservation();
	}

	public Reservation makeReservation(int reservationID, Vehicle vehicle, Date startDate, Date endDate) {
		Reservation reservation = new Reservation(reservationID, startDate, endDate, ReservationStatus.PENDING);
		reservation.setCustomer(this);
		reservation.setVehicle(vehicle);
		reservations.add(reservation);
		if (vehicle != null) {
			vehicle.getReservations().add(reservation);
		}
		return reservation;
	}

	public ArrayList<Reservation> viewMyReservations() {
		return reservations;
	}

	/**
	 * 
	 * @param amount
	 */
	public void earnPoints(double amount) {
		int earnedPoints = (int) (amount / 10);
		loyaltyPoints += earnedPoints;
		loyaltyTier = LoyaltyTier.fromPoints(loyaltyPoints);
	}

}
