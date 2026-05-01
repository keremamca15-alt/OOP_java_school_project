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
		// TODO - implement Invoice.calculateTotal
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param customer
	 */
	public void applyDiscount(Customer customer) {
		// TODO - implement Invoice.applyDiscount
		throw new UnsupportedOperationException();
	}

	public double calculateRefund() {
		// TODO - implement Invoice.calculateRefund
		throw new UnsupportedOperationException();
	}

}