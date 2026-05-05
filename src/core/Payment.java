package core;

import java.util.Date;

public class Payment {

	private int paymentID;
	private double amount;
	private Date paymentDate;
	private PaymentPurpose paymentPurpose;
	private int customerID;
	private Customer customer;
	private Reservation reservation;
	private RentalContract rentalContract;
	private Invoice invoice;

	public Payment() {
	}

	public Payment(int paymentID, double amount, Date paymentDate, PaymentPurpose paymentPurpose, int customerID) {
		setPaymentID(paymentID);
		setAmount(amount);
		setPaymentDate(paymentDate);
		setPaymentPurpose(paymentPurpose);
		setCustomerID(customerID);
	}

	public int getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(int paymentID) {
		if (paymentID < 0) {
			throw new IllegalArgumentException("Payment ID cannot be negative.");
		}
		this.paymentID = paymentID;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Payment amount cannot be negative.");
		}
		this.amount = amount;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public PaymentPurpose getPaymentPurpose() {
		return paymentPurpose;
	}

	public void setPaymentPurpose(PaymentPurpose paymentPurpose) {
		this.paymentPurpose = paymentPurpose;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		if (customerID < 0) {
			throw new IllegalArgumentException("Customer ID cannot be negative.");
		}
		this.customerID = customerID;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
		if (customer != null) {
			customerID = customer.getUserID();
			if (!customer.getPayments().contains(this)) {
				customer.getPayments().add(this);
			}
		}
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
		if (reservation != null && paymentPurpose == PaymentPurpose.PREPAYMENT) {
			reservation.setPrepayment(this);
		}
	}

	public RentalContract getRentalContract() {
		return rentalContract;
	}

	public void setRentalContract(RentalContract rentalContract) {
		this.rentalContract = rentalContract;
		if (rentalContract != null && rentalContract.getPickupPayment() != this
				&& paymentPurpose == PaymentPurpose.DEPOSIT) {
			rentalContract.setPickupPayment(this);
		}
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
		if (invoice != null && rentalContract == null && invoice.getRentalContract() != null) {
			setRentalContract(invoice.getRentalContract());
		}
		if (invoice != null && !invoice.getPayments().contains(this)) {
			invoice.addPayment(this);
		}
	}

	public boolean processPayment() {
		return amount > 0 && paymentDate != null && paymentPurpose != null
				&& (customer != null || customerID > 0);
	}

}
