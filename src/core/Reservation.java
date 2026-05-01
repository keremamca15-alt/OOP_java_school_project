package core;

import java.util.Date;

public class Reservation {

	private int reservationID;
	private Date startDate;
	private Date endDate;
	private ReservationStatus status;
	private double prePaymentAmount;
	private double depositAmount;

	public void confirmReservation() {
	}

	public void cancelReservation() {
	}

	public int calculateDuration() {
		return 0;
	}

	/**
	 * 
	 * @param addon
	 */
	public void addAddon(Addon addon) {
	}

}
