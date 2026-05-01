public enum MileagePolicy {
	;

	private int dailyKmLimit;
	private double extraKmFee;

	/**
	 * 
	 * @param days
	 * @param km
	 */
	public double calculateExtraCharge(int days, int km) {
		return 0.0;
	}

}
