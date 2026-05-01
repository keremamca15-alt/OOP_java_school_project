import java.util.ArrayList;

public class Customer extends User {

	private int loyaltyPoints;
	private LoyaltyTier loyaltyTier;

	public ArrayList<Vehicle> searchAvailableVehicles() {
		return new ArrayList<>();
	}

	public Reservation makeReservation() {
		return null;
	}

	public ArrayList<Reservation> viewMyReservations() {
		return new ArrayList<>();
	}

	/**
	 * 
	 * @param amount
	 */
	public void earnPoints(double amount) {
	}

}
