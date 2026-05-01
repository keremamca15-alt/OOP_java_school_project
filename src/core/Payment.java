package core;

import java.util.Date;

public class Payment {

	private int paymentID;
	private double amount;
	private Date paymentDate;
	private PaymentPurpose paymentPurpose;
	private int customerID;

	public boolean processPayment() {
		return false;
	}

}
