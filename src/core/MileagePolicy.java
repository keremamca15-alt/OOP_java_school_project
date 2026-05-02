package core;

public enum MileagePolicy {
	LIMITED(100, 3.0),
	STANDARD(200, 2.0),
	UNLIMITED(Integer.MAX_VALUE, 0.0);

	private final int dailyKmLimit;
	private final double extraKmFee;

	MileagePolicy(int dailyKmLimit, double extraKmFee) {
		this.dailyKmLimit = dailyKmLimit;
		this.extraKmFee = extraKmFee;
	}

	/**
	 * 
	 * @param days
	 * @param km
	 */
	public double calculateExtraCharge(int days, int km) {
		if (days < 0) {
			throw new IllegalArgumentException("Days cannot be negative.");
		}
		if (km < 0) {
			throw new IllegalArgumentException("Kilometers cannot be negative.");
		}
		if (this == UNLIMITED) {
			return 0.0;
		}
		int includedKm = dailyKmLimit * days;
		int extraKm = km - includedKm;
		if (extraKm <= 0) {
			return 0.0;
		}
		return extraKm * extraKmFee;
	}

	public int getDailyKmLimit() {
		return dailyKmLimit;
	}

	public double getExtraKmFee() {
		return extraKmFee;
	}

}
