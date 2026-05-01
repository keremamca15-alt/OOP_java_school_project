package core;

import java.util.Date;

public class DamageAssessment {

	private int assessmentID;
	private Date assessmentDate;
	private String description;
	private double damageCost;
	private Vehicle vehicle;
	private RentalAgent rentalAgent;
	private Invoice invoice;

	public DamageAssessment() {
	}

	public DamageAssessment(int assessmentID, Date assessmentDate, String description, double damageCost) {
		this.assessmentID = assessmentID;
		this.assessmentDate = assessmentDate;
		this.description = description;
		this.damageCost = damageCost;
	}

	public int getAssessmentID() {
		return assessmentID;
	}

	public void setAssessmentID(int assessmentID) {
		this.assessmentID = assessmentID;
	}

	public Date getAssessmentDate() {
		return assessmentDate;
	}

	public void setAssessmentDate(Date assessmentDate) {
		this.assessmentDate = assessmentDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getDamageCost() {
		return damageCost;
	}

	public void setDamageCost(double damageCost) {
		this.damageCost = damageCost;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
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
	}

}
