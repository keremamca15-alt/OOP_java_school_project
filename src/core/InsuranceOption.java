package core;

public enum InsuranceOption {
	BASIC,
	STANDARD,
	PREMIUM;

	private double dailyFee;
	private double coverageLimit;

	/**
	 * 
	 * @param days
	 */
	public double calculateCost(int days) {
		return 0.0;
	}

}
