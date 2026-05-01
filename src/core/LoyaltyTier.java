package core;

public enum LoyaltyTier {
	BRONZE(0, 0.0),
	SILVER(500, 0.05),
	GOLD(1500, 0.10);

	private final double discountRate;
	private final int minPoints;

	LoyaltyTier(int minPoints, double discountRate) {
		this.minPoints = minPoints;
		this.discountRate = discountRate;
	}

	/**
	 * 
	 * @param points
	 */
	public static LoyaltyTier fromPoints(int points) {
		LoyaltyTier result = BRONZE;
		for (LoyaltyTier tier : values()) {
			if (points >= tier.minPoints) {
				result = tier;
			}
		}
		return result;
	}

	public double getDiscountRate() {
		return discountRate;
	}

	public int getMinPoints() {
		return minPoints;
	}

}
