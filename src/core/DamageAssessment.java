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
		if (assessmentID < 0) {
			throw new IllegalArgumentException("Assessment ID cannot be negative.");
		}
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
		if (damageCost < 0) {
			throw new IllegalArgumentException("Damage cost cannot be negative.");
		}
		this.damageCost = damageCost;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
		if (vehicle != null && !vehicle.getDamageAssessments().contains(this)) {
			vehicle.getDamageAssessments().add(this);
		}
	}

	public RentalAgent getRentalAgent() {
		return rentalAgent;
	}

	public void setRentalAgent(RentalAgent rentalAgent) {
		this.rentalAgent = rentalAgent;
		if (rentalAgent != null && !rentalAgent.getDamageAssessments().contains(this)) {
			rentalAgent.getDamageAssessments().add(this);
		}
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
		if (invoice != null && invoice.getDamageAssessment() != this) {
			invoice.setDamageAssessment(this);
		}
	}

}
