package xyz.binfish.misa.exceptions;

public class DatabaseException extends RuntimeException {

	/*
	 * Constructs a new runtime exception with the specified detail message.
	 *
	 * @param message the detail message.
	 */
	public DatabaseException(String message) {
		super(message);
	}

	/*
	 * Constructs a new runtime exception with the specified detail message
	 * and cause.
	 * 
	 * @param message the detail message.
	 * @param cause   the cause.
	 */
	public DatabaseException(String message, Exception e) {
		super(message, e);
	}
}
