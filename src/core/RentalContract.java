package core;

import java.util.Date;
import java.util.ArrayList;

public class RentalContract {

	private int contractID;
	private Date pickupDate;
	private Date expectedReturnDate;
	private Date actualReturnDate;
	private double depositAmount;
	private int initialMileage;
	private int finalMileage;
	private ContractStatus status;
	private Reservation reservation;
	private RentalAgent rentalAgent;
	private Invoice invoice;
	private Payment pickupPayment;
	private ArrayList<Addon> addons = new ArrayList<>();

	public RentalContract() {
	}

	public RentalContract(int contractID, Date pickupDate, Date expectedReturnDate, ContractStatus status) {
		setContractID(contractID);
		setPickupDate(pickupDate);
		setExpectedReturnDate(expectedReturnDate);
		setStatus(status);
	}

	public int getContractID() {
		return contractID;
	}

	public void setContractID(int contractID) {
		if (contractID < 0) {
			throw new IllegalArgumentException("Contract ID cannot be negative.");
		}
		this.contractID = contractID;
	}

	public Date getPickupDate() {
		return pickupDate;
	}

	public void setPickupDate(Date pickupDate) {
		this.pickupDate = pickupDate;
	}

	public Date getExpectedReturnDate() {
		return expectedReturnDate;
	}

	public void setExpectedReturnDate(Date expectedReturnDate) {
		this.expectedReturnDate = expectedReturnDate;
	}

	public Date getActualReturnDate() {
		return actualReturnDate;
	}

	public void setActualReturnDate(Date actualReturnDate) {
		this.actualReturnDate = actualReturnDate;
	}

	public double getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(double depositAmount) {
		if (depositAmount < 0) {
			throw new IllegalArgumentException("Deposit amount cannot be negative.");
		}
		this.depositAmount = depositAmount;
	}

	public int getInitialMileage() {
		return initialMileage;
	}

	public void setInitialMileage(int initialMileage) {
		if (initialMileage < 0) {
			throw new IllegalArgumentException("Initial mileage cannot be negative.");
		}
		this.initialMileage = initialMileage;
	}

	public int getFinalMileage() {
		return finalMileage;
	}

	public void setFinalMileage(int finalMileage) {
		if (finalMileage < 0) {
			throw new IllegalArgumentException("Final mileage cannot be negative.");
		}
		this.finalMileage = finalMileage;
	}

	public ContractStatus getStatus() {
		return status;
	}

	public void setStatus(ContractStatus status) {
		this.status = status;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
		if (reservation != null && reservation.getRentalContract() != this) {
			reservation.setRentalContract(this);
		}
	}

	public RentalAgent getRentalAgent() {
		return rentalAgent;
	}

	public void setRentalAgent(RentalAgent rentalAgent) {
		this.rentalAgent = rentalAgent;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
		if (invoice != null && invoice.getRentalContract() != this) {
			invoice.setRentalContract(this);
		}
	}

	public Payment getPickupPayment() {
		return pickupPayment;
	}

	public void setPickupPayment(Payment pickupPayment) {
		this.pickupPayment = pickupPayment;
		if (pickupPayment != null && pickupPayment.getRentalContract() != this) {
			pickupPayment.setRentalContract(this);
		}
	}

	public ArrayList<Addon> getAddons() {
		return addons;
	}

	public void setAddons(ArrayList<Addon> addons) {
		this.addons = new ArrayList<>();
		if (addons != null) {
			for (Addon addon : addons) {
				addAddon(addon);
			}
		}
	}

	/**
	 * 
	 * @param addon
	 */
	public void addAddon(Addon addon) {
		if (addon != null && !addons.contains(addon)) {
			addons.add(addon);
		}
	}

	public int calculateUsedMileage() {
		if (finalMileage < initialMileage) {
			return 0;
		}
		return finalMileage - initialMileage;
	}

	public void closeContract() {
		status = ContractStatus.CLOSED;
		actualReturnDate = new Date();
	}

	public double calculateDepositRefund() {
		if (invoice == null) {
			return depositAmount;
		}
		double returnExtraCost = invoice.calculateReturnExtraCost();
		if (returnExtraCost <= 0) {
			return depositAmount;
		}
		if (returnExtraCost >= depositAmount) {
			return 0.0;
		}
		return depositAmount - returnExtraCost;
	}

	public double calculateDepositUsedForExtras() {
		if (invoice == null) {
			return 0.0;
		}
		double returnExtraCost = invoice.calculateReturnExtraCost();
		if (returnExtraCost <= 0) {
			return 0.0;
		}
		if (returnExtraCost > depositAmount) {
			return depositAmount;
		}
		return returnExtraCost;
	}

	public double calculateAdditionalChargeAfterDeposit() {
		if (invoice == null) {
			return 0.0;
		}
		double returnExtraCost = invoice.calculateReturnExtraCost();
		if (returnExtraCost <= depositAmount) {
			return 0.0;
		}
		return returnExtraCost - depositAmount;
	}

	public double calculateRemainingAmountAfterDeposit() {
		if (invoice == null) {
			return 0.0;
		}
		double remainingAmount = invoice.calculateRemainingAmount() - calculateDepositUsedForExtras();
		if (remainingAmount < 0) {
			return 0.0;
		}
		return remainingAmount;
	}

}
