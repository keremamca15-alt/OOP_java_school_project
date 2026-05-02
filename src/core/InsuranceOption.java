package core;

public enum InsuranceOption {
	BASIC(10.0, 5000.0),
	STANDARD(20.0, 15000.0),
	PREMIUM(35.0, 50000.0);

	private final double dailyFee;
	private final double coverageLimit;

	InsuranceOption(double dailyFee, double coverageLimit) {
		this.dailyFee = dailyFee;
		this.coverageLimit = coverageLimit;
	}

	/**
	 * 
	 * @param days
	 */
	public double calculateCost(int days) {
		if (days < 0) {
			throw new IllegalArgumentException("Days cannot be negative.");
		}
		return dailyFee * days;
	}

	public double getDailyFee() {
		return dailyFee;
	}

	public double getCoverageLimit() {
		return coverageLimit;
	}

}
