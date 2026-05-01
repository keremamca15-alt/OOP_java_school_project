package core;

public class BranchNotFoundException extends Exception {

	public BranchNotFoundException() {
		super();
	}

	public BranchNotFoundException(String message) {
		super(message);
	}

	public BranchNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BranchNotFoundException(Throwable cause) {
		super(cause);
	}
}
