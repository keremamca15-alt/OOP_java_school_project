package core;

public class Addon {

	private int addonID;
	private String name;
	private String description;
	private double dailyPrice;

	public Addon() {
	}

	public Addon(int addonID, String name, String description, double dailyPrice) {
		setAddonID(addonID);
		this.name = name;
		this.description = description;
		setDailyPrice(dailyPrice);
	}

	public int getAddonID() {
		return addonID;
	}

	public void setAddonID(int addonID) {
		if (addonID < 0) {
			throw new IllegalArgumentException("Addon ID cannot be negative.");
		}
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
		if (dailyPrice < 0) {
			throw new IllegalArgumentException("Daily price cannot be negative.");
		}
		this.dailyPrice = dailyPrice;
	}

	/**
	 * 
	 * @param days
	 */
	public double calculateCost(int days) {
		if (days < 0) {
			throw new IllegalArgumentException("Days cannot be negative.");
		}
		return dailyPrice * days;
	}

}
