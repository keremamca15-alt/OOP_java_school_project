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
		setInvoiceID(invoiceID);
		setBaseAmount(baseAmount);
		setDamageFee(damageFee);
		setAddonFee(addonFee);
	}

	public int getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(int invoiceID) {
		if (invoiceID < 0) {
			throw new IllegalArgumentException("Invoice ID cannot be negative.");
		}
		this.invoiceID = invoiceID;
	}

	public double getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(double baseAmount) {
		if (baseAmount < 0) {
			throw new IllegalArgumentException("Base amount cannot be negative.");
		}
		this.baseAmount = baseAmount;
	}

	public double getDamageFee() {
		return damageFee;
	}

	public void setDamageFee(double damageFee) {
		if (damageFee < 0) {
			throw new IllegalArgumentException("Damage fee cannot be negative.");
		}
		this.damageFee = damageFee;
	}

	public double getAddonFee() {
		return addonFee;
	}

	public void setAddonFee(double addonFee) {
		if (addonFee < 0) {
			throw new IllegalArgumentException("Addon fee cannot be negative.");
		}
		this.addonFee = addonFee;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		if (discountAmount < 0) {
			throw new IllegalArgumentException("Discount amount cannot be negative.");
		}
		this.discountAmount = discountAmount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		if (totalAmount < 0) {
			throw new IllegalArgumentException("Total amount cannot be negative.");
		}
		this.totalAmount = totalAmount;
	}

	public double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(double refundAmount) {
		if (refundAmount < 0) {
			throw new IllegalArgumentException("Refund amount cannot be negative.");
		}
		this.refundAmount = refundAmount;
	}

	public double getAdditionalCharges() {
		return additionalCharges;
	}

	public void setAdditionalCharges(double additionalCharges) {
		if (additionalCharges < 0) {
			throw new IllegalArgumentException("Additional charges cannot be negative.");
		}
		this.additionalCharges = additionalCharges;
	}

	public RentalContract getRentalContract() {
		return rentalContract;
	}

	public void setRentalContract(RentalContract rentalContract) {
		this.rentalContract = rentalContract;
		if (rentalContract != null && rentalContract.getInvoice() != this) {
			rentalContract.setInvoice(this);
		}
	}

	public DamageAssessment getDamageAssessment() {
		return damageAssessment;
	}

	public void setDamageAssessment(DamageAssessment damageAssessment) {
		this.damageAssessment = damageAssessment;
		if (damageAssessment != null && damageAssessment.getInvoice() != this) {
			damageAssessment.setInvoice(this);
		}
	}

	public ArrayList<Payment> getPayments() {
		return payments;
	}

	public void setPayments(ArrayList<Payment> payments) {
		this.payments = new ArrayList<>();
		if (payments != null) {
			for (Payment payment : payments) {
				addPayment(payment);
			}
		}
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
			if (payment.processPayment()
					&& payment.getPaymentPurpose() != PaymentPurpose.REFUND
					&& payment.getPaymentPurpose() != PaymentPurpose.DEPOSIT) {
				paidAmount += payment.getAmount();
			}
		}
		return paidAmount;
	}

	public double calculateRemainingAmount() {
		double remainingAmount = calculateTotal() - calculatePaidAmount();
		if (remainingAmount < 0) {
			return 0.0;
		}
		return remainingAmount;
	}

	public double calculateReturnExtraCost() {
		return damageFee + additionalCharges;
	}

	public double calculateTotal() {
		totalAmount = calculateTotalBeforeRefund() - refundAmount;
		if (totalAmount < 0) {
			totalAmount = 0.0;
		}
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
		double overpaidAmount = calculatePaidAmount() - calculateTotalBeforeRefund();
		if (overpaidAmount > 0) {
			refundAmount = overpaidAmount;
		}
		return refundAmount;
	}

	public void addAdditionalCharge(double amount) {
		if (amount > 0) {
			additionalCharges += amount;
		}
	}

	public void applyRefund(double amount) {
		if (amount > 0) {
			refundAmount += amount;
		}
	}

	private double calculateTotalBeforeRefund() {
		double amount = baseAmount + damageFee + addonFee + additionalCharges - discountAmount;
		if (amount < 0) {
			return 0.0;
		}
		return amount;
	}

}
