package core;

public class InvalidReservationException extends Exception {

	public InvalidReservationException() {
		super();
	}

	public InvalidReservationException(String message) {
		super(message);
	}

	public InvalidReservationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidReservationException(Throwable cause) {
		super(cause);
	}
}
