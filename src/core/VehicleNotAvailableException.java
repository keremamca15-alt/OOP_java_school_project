package core;

public class VehicleNotAvailableException extends Exception {

	public VehicleNotAvailableException() {
		super();
	}

	public VehicleNotAvailableException(String message) {
		super(message);
	}

	public VehicleNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public VehicleNotAvailableException(Throwable cause) {
		super(cause);
	}
}
