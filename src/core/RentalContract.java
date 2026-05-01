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
	}

	public int calculateUsedMileage() {
		return 0;
	}

	public void closeContract() {
	}

}
