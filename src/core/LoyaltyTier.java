package core;

public enum LoyaltyTier {
	BRONZE,
	SILVER,
	GOLD;

	private double discountRate;
	private int minPoints;

	/**
	 * 
	 * @param points
	 */
	public LoyaltyTier fromPoints(int points) {
		return null;
	}

}
