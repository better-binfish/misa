package xyz.binfish.misa.util;

import java.util.HashSet;
import java.util.Arrays;

public class ComparatorUtil {

	private final static HashSet<String> fuzzyTrue = new HashSet<>(
			Arrays.asList("yes", "y", "on", "enable", "true", "confirm", "1"));
	private final static HashSet<String> fuzzyFalse = new HashSet<>(
			Arrays.asList("no", "n", "off", "disable", "false", "reset", "0"));

	/*
	 * Check if the given string matches a true statement using the fuzzyTrue hash set.
	 *
	 * @param string the string that should be checked.
	 * @return true if the given string can be considered true.
	 */
	public static boolean isFuzzyTrue(String string) {
		return string != null && fuzzyTrue.contains(string.toLowerCase());
	}

	/*
	 * Check if the given string matches a false statement using the fuzzyFalse hash set.
	 *
	 * @param string the string that shoudl be checked.
	 * @return true if the given string can be considered false.
	 */
	public static boolean isFuzzyFalse(String string) {
		return string != null && fuzzyFalse.contains(string.toLowerCase());
	}

	/*
	 * Get the boolean value from the given fuzzy string, if the string doesn't contain
	 * any valid fuzzy types, false will be returned.
	 *
	 * @param string the string that should be checked.
	 * @return the boolean value matching the given string, or false if there were no match.
	 */
	public static boolean getFuzzyType(String string) {
		if(isFuzzyTrue(string)) {
			return true;
		}

		if(isFuzzyFalse(string)) {
			return false;
		}

		return false;
	}
}
