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
		this.paymentID = paymentID;
		this.amount = amount;
		this.paymentDate = paymentDate;
		this.paymentPurpose = paymentPurpose;
		this.customerID = customerID;
	}

	public int getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(int paymentID) {
		this.paymentID = paymentID;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
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
		this.customerID = customerID;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public RentalContract getRentalContract() {
		return rentalContract;
	}

	public void setRentalContract(RentalContract rentalContract) {
		this.rentalContract = rentalContract;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public boolean processPayment() {
		return false;
	}

}
