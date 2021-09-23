package xyz.binfish.misa.exception;

public class FailedToLoadResourceException extends IllegalArgumentException {

	/*
	 * Constructs a new illegal argument exception with the specified detail message.
	 *
	 * @param message the detail message.
	 */
	public FailedToLoadResourceException(String message) {
		super(message);
	}

	/*
	 * Constructs a new illegal argument exception with the specified detail message
	 * and cause.
	 *
	 * @param message the detail message.
	 * @param cause   the cause.
	 */
	public FailedToLoadResourceException(String message, Throwable cause) {
		super(message, cause);
	}
}
