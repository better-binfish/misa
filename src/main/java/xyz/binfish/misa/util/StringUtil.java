package xyz.binfish.misa.util;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class StringUtil {

	private static final Pattern ID_PATTERN = Pattern.compile(
			"^[0-9]{18}$"
	);

	private static final Pattern LANGUAGE_STRING = Pattern.compile(
			"[A-Za-z\\.]+"
	);

	/*
	 * Checks if a string is an discord identifier by pattern.
	 *
	 * @param supposedId the supposed identifier.
	 * @return true if string is id, false otherwise.
	 */
	public static boolean isIdentifier(@Nonnull String supposedId) {
		return ID_PATTERN.matcher(supposedId).matches();
	}

	/*
	 * Checks if a string is a language string using a pattern
	 * and checking for whitespace contents.
	 *
	 * Note: A string containing spaces or not matching 
	 * the pattern is not a language string.
	 *
	 * @param string the string.
	 * @return true if string is language string, flase otherwise.
	 */
	public static boolean isLanguageString(String string) {
		return !string.contains(" ")
			&& LANGUAGE_STRING.matcher(string).matches();
	}

	/*
	 * Capitalizes only the first character.
	 *
	 * @param str the string.
	 * @return string with the first uppercase character.
	 */
	public static String capitalizeOnlyFirstChar(String str) {
		if(str == null || str.length() == 0) {
			return "";
		}

		if(str.length() == 1) {
			return str.toUpperCase();
		}

		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	/*
	 * Converts the first character of string to lowercase.
	 *
	 * @param str the string.
	 * @return string with the first lowercase character.
	 */
	public static String firstCharToLowerCase(String str) {
		if(str == null || str.length() == 0) {
			return "";
		}

		char c[] = str.toCharArray();
		c[0] = Character.toLowerCase(c[0]);

		return new String(c);
	}

	/*
	 * Parses the string argument as boolean, if the string
	 * not a valid boolean false will be returned.
	 *
	 * @param string the string boolean that should be parsed.
	 * @return the boolean represented by the string argument.
	 */
	public static boolean parseBoolean(@Nonnull String string) {
		return Boolean.parseBoolean(string);
	}
}
