package core;

public class Addon {

	private int addonID;
	private String name;
	private String description;
	private double dailyPrice;

	public Addon() {
	}

	public Addon(int addonID, String name, String description, double dailyPrice) {
		this.addonID = addonID;
		this.name = name;
		this.description = description;
		this.dailyPrice = dailyPrice;
	}

	public int getAddonID() {
		return addonID;
	}

	public void setAddonID(int addonID) {
		this.addonID = addonID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getDailyPrice() {
		return dailyPrice;
	}

	public void setDailyPrice(double dailyPrice) {
		this.dailyPrice = dailyPrice;
	}

	/**
	 * 
	 * @param days
	 */
	public double calculateCost(int days) {
		return dailyPrice * days;
	}

}
