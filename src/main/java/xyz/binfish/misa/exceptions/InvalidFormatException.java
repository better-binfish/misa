package xyz.binfish.misa.exceptions;

public class InvalidFormatException extends RuntimeException {

	public InvalidFormatException(String message, String time) {
		super(String.format(message, time));
	}
}
