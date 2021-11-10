package xyz.binfish.misa.util;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class NumberUtil {

	private static final Pattern NUMBER_PATTERN = Pattern.compile("[-+]?\\d*\\.?\\d+");

	/*
	 * Parses the string argument as a signed integer, if the string argument
	 * if not a valid integer 0 will be returned as the default instead.
	 *
	 * @param string the string integer that should be parsed.
	 * @return the integer represented by the string argument.
	 */
	public static int parseInt(@Nonnull String string) {
		return parseInt(string, 0);
	}

	/*
	 * Parses the string argument as a signed integer, if the string argument
	 * is not a valid integer, the default will be returned instead.
	 *
	 * @param string the string integer that should be parsed.
	 * @param def    the default integer if the string argument is not a valid integer.
	 * @return the integer represented by the string argument.
	 */
	public static int parseInt(@Nonnull String string, int def) {
		return parseInt(string, def, 10);
	}

	/*
	 * Parses the string argument as a signed integer, if the string argument
	 * is not a valid integer, the default will be returned instead, the
	 * integer will be parsed using the given radix.
	 *
	 * @param string the string integer that should be parsed.
	 * @param def    the default integer if the string argument is not a valid integer.
	 * @param radix  the radix to be used while parsing.
	 * @return the integer represented by the string argument.
	 */
	public static int parseInt(@Nonnull String string, int def, int radix) {
		try {
			return Integer.parseInt(string, radix);
		} catch(NumberFormatException e) {
			return def;
		}
	}

	/*
	 * Checks if the given string is a numeric string, only containing numbers.
	 *
	 * @param string the string that should be checked if it is numeric.
	 * @return true if the string is numeric, false otherwise.
	 */
	public static boolean isNumeric(@Nonnull String string) {
		return NUMBER_PATTERN.matcher(string).matches();
	}

	/*
	 * Parses the given number, making sure the number is greater than
	 * the minimum number given, and less than the max number given.
	 *
	 * @param number the number that should be parsed.
	 * @param min    the max value number can be.
	 * @param max    the minimum value the number can be.
	 * @return get the number that is greater that the minimum and less than the maximum.
	 */
	public static int getBetween(int number, int min, int max) {
		return Math.min(max, Math.max(min, number));
	}
}
