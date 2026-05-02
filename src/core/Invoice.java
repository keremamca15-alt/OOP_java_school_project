package core;

import java.util.ArrayList;

public class Invoice {

	private int invoiceID;
	private double baseAmount;
	private double damageFee;
	private double addonFee;
	private double discountAmount;
	private double totalAmount;
	private double refundAmount;
	private double additionalCharges;
	private RentalContract rentalContract;
	private DamageAssessment damageAssessment;
	private ArrayList<Payment> payments = new ArrayList<>();

	public Invoice() {
	}

	public Invoice(int invoiceID, double baseAmount, double damageFee, double addonFee) {
		this.invoiceID = invoiceID;
		this.baseAmount = baseAmount;
		this.damageFee = damageFee;
		this.addonFee = addonFee;
	}

	public int getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(int invoiceID) {
		this.invoiceID = invoiceID;
	}

	public double getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(double baseAmount) {
		this.baseAmount = baseAmount;
	}

	public double getDamageFee() {
		return damageFee;
	}

	public void setDamageFee(double damageFee) {
		this.damageFee = damageFee;
	}

	public double getAddonFee() {
		return addonFee;
	}

	public void setAddonFee(double addonFee) {
		this.addonFee = addonFee;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(double refundAmount) {
		this.refundAmount = refundAmount;
	}

	public double getAdditionalCharges() {
		return additionalCharges;
	}

	public void setAdditionalCharges(double additionalCharges) {
		this.additionalCharges = additionalCharges;
	}

	public RentalContract getRentalContract() {
		return rentalContract;
	}

	public void setRentalContract(RentalContract rentalContract) {
		this.rentalContract = rentalContract;
	}

	public DamageAssessment getDamageAssessment() {
		return damageAssessment;
	}

	public void setDamageAssessment(DamageAssessment damageAssessment) {
		this.damageAssessment = damageAssessment;
	}

	public ArrayList<Payment> getPayments() {
		return payments;
	}

	public void setPayments(ArrayList<Payment> payments) {
		this.payments = payments;
	}

	public void addPayment(Payment payment) {
		if (payment != null && payment.processPayment() && !payments.contains(payment)) {
			payments.add(payment);
			payment.setInvoice(this);
		}
	}

	public double calculatePaidAmount() {
		double paidAmount = 0.0;
		for (Payment payment : payments) {
			if (payment.processPayment()) {
				paidAmount += payment.getAmount();
			}
		}
		return paidAmount;
	}

	public double calculateRemainingAmount() {
		return calculateTotal() - calculatePaidAmount();
	}

	public double calculateTotal() {
		totalAmount = baseAmount + damageFee + addonFee + additionalCharges - discountAmount - refundAmount;
		return totalAmount;
	}

	/**
	 * 
	 * @param customer
	 */
	public void applyDiscount(Customer customer) {
		if (customer == null || customer.getLoyaltyTier() == null) {
			discountAmount = 0.0;
			return;
		}
		discountAmount = baseAmount * customer.getLoyaltyTier().getDiscountRate();
	}

	public double calculateRefund() {
		return refundAmount;
	}

}
