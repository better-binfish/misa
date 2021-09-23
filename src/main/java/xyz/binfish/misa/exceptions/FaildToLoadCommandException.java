package xyz.binfish.misa.exceptions;

public class FaildToLoadCommandException extends RuntimeException {

	/*
	 * Constructs a new runtime exception with the specified detail message.
	 *
	 * @param message the detail message.
	 */
	public FaildToLoadCommandException(String message) {
		super(message);
	}

	/*
	 * Constructs a new runtime exception with the specified detail message
	 * and cause.
	 *
	 * @param message the detail message.
	 * @prarm cause   the cause.
	 */
	public FaildToLoadCommandException(String message, Throwable cause) {
		super(message, cause);
	}
}
