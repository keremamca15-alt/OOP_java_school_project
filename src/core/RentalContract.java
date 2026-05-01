import java.util.Date;

public class RentalContract {

	private int contractID;
	private Date pickupDate;
	private Date expectedReturnDate;
	private Date actualReturnDate;
	private double depositAmount;
	private int initialMileage;
	private int finalMileage;
	private ContractStatus status;

	/**
	 * 
	 * @param addon
	 */
	public void addAddon(Addon addon) {
		// TODO - implement RentalContract.addAddon
		throw new UnsupportedOperationException();
	}

	public int calculateUsedMileage() {
		// TODO - implement RentalContract.calculateUsedMileage
		throw new UnsupportedOperationException();
	}

	public void closeContract() {
		// TODO - implement RentalContract.closeContract
		throw new UnsupportedOperationException();
	}

}
