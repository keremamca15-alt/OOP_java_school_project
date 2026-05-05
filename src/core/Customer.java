package core;

import java.util.ArrayList;
import java.util.Calendar;
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
		setLoyaltyPoints(loyaltyPoints);
		setLoyaltyTier(loyaltyTier);
	}

	public int getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(int loyaltyPoints) {
		if (loyaltyPoints < 0) {
			throw new IllegalArgumentException("Loyalty points cannot be negative.");
		}
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
		this.reservations = new ArrayList<>();
		if (reservations != null) {
			for (Reservation reservation : reservations) {
				if (reservation != null && !this.reservations.contains(reservation)) {
					this.reservations.add(reservation);
					reservation.setCustomer(this);
				}
			}
		}
	}

	public ArrayList<Payment> getPayments() {
		return payments;
	}

	public void setPayments(ArrayList<Payment> payments) {
		this.payments = new ArrayList<>();
		if (payments != null) {
			for (Payment payment : payments) {
				if (payment != null && !this.payments.contains(payment)) {
					this.payments.add(payment);
					payment.setCustomer(this);
				}
			}
		}
	}

	public ArrayList<Vehicle> searchAvailableVehicles(Branch branch, Date startDate, Date endDate)
			throws BranchNotFoundException {
		if (branch == null) {
			throw new BranchNotFoundException("Branch cannot be found.");
		}
		if (!isValidDateRange(startDate, endDate)) {
			return new ArrayList<>();
		}
		return branch.findAvailableVehicles(startDate, endDate);
	}

	public Reservation makeReservation(int reservationID, Vehicle vehicle, Date startDate, Date endDate)
			throws InvalidReservationException, VehicleNotAvailableException {
		validateReservationRequest(vehicle, startDate, endDate);

		Reservation reservation = new Reservation(reservationID, startDate, endDate, ReservationStatus.PENDING);
		reservation.setCustomer(this);
		reservation.setVehicle(vehicle);
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
		if (amount < 0) {
			throw new IllegalArgumentException("Amount cannot be negative.");
		}
		int earnedPoints = (int) (amount / 10);
		loyaltyPoints += earnedPoints;
		loyaltyTier = LoyaltyTier.fromPoints(loyaltyPoints);
	}

	private void validateReservationRequest(Vehicle vehicle, Date startDate, Date endDate)
			throws InvalidReservationException, VehicleNotAvailableException {
		if (vehicle == null) {
			throw new InvalidReservationException("Reservation vehicle cannot be empty.");
		}
		if (!isValidDateRange(startDate, endDate)) {
			throw new InvalidReservationException("Reservation dates are not valid.");
		}
		if (!vehicle.isAvailable(startDate, endDate)) {
			throw new VehicleNotAvailableException("Vehicle is not available for selected dates.");
		}
	}

	private boolean isValidDateRange(Date startDate, Date endDate) {
		return startDate != null && endDate != null && endDate.after(startDate)
				&& !startOfDay(startDate).before(startOfDay(new Date()));
	}

	private Date startOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

}
