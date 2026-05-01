package core;

public class Invoice {

	private int invoiceID;
	private double baseAmount;
	private double damageFee;
	private double addonFee;
	private double discountAmount;
	private double totalAmount;
	private double refundAmount;
	private double additionalCharges;

	public double calculateTotal() {
		return 0.0;
	}

	/**
	 * 
	 * @param customer
	 */
	public void applyDiscount(Customer customer) {
	}

	public double calculateRefund() {
		return 0.0;
	}

}
