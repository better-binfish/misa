package xyz.binfish.misa.cli;

public class CLIUtil {

	/*
	 * Remove the hyphens from the beginning of string and return the new String.
	 *
	 * @param str the string from which the hyphens should be removed.
	 * @return the new String with hyphens removed.
	 */
	public static String stripLeadingHyphens(String str) {
		if(str == null) {
			return null;
		}
		if(str.startsWith("--")) {
			return str.substring(2);
		}
		if(str.startsWith("-")) {
			return str.substring(1);
		}

		return str;
	}
}
