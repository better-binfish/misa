package xyz.binfish.misa.util;

import javax.annotation.Nonnull;
import java.security.SecureRandom;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class RandomUtil {

	/*
	 * A list of characters that can be used to generate the string randomly.
	 */
	private static final List<String> characterSet = Arrays.asList(
			"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m",
			"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M",
			"0", "1", "2", "3", "4", "5", "6", "7", "9", "8", "!", "%", "&", "(", ")", "[", "]", "{", "}"
	);

	/*
	 * The globally used random instance that is used for all randomized things.
	 */
	private static final SecureRandom random = new SecureRandom();

	/*
	 * Return true or false randomly.
	 *
	 * @return true or false.
	 */
	public static boolean getBoolean() {
		return random.nextBoolean();
	}

	/*
	 * Get a random integer within the bounds given, so the
	 * integer returned will be a random integer between
	 * 0 and the given bound integer - 1.
	 *
	 * @param bound the max bound for the random integer.
	 * @return the random integer.
	 */
	public static int getInteger(int bound) {
		if(bound <= 0) {
			return 0;
		}

		return random.nextInt(bound);
	}

	/*
	 * Gets a sRGB color with a random red, green, blue, and alpha component.
	 *
	 * @return the random color.
	 */
	public static Color getRandomColor() {
		return new Color(
			random.nextInt(255) / 255F, // Red
			random.nextInt(255) / 255F, // Green
			random.nextInt(255) / 255F, // Blue
			random.nextInt(100) / 100F  // Alpha
		);
	}

	/*
	 * Pick one random string from the given list of strings.
	 *
	 * @param strings the strings that should be randomized.
	 * @return the random string picked from the list of given strings.
	 */
	public static String pickRandom(@Nonnull String... strings) {
		return strings[random.nextInt(strings.length)];
	}

	/*
	 * Pick one random string from the given list of strings.
	 *
	 * @param strings the list of strings that should be used to pick a random string.
	 * @return the random string picked from the list of given strings.
	 */
	public static Object pickRandom(@Nonnull List<?> strings) {
		return strings.get(random.nextInt(strings.size()));
	}

	/*
	 * Generate a randomly generated string with the given length.
	 *
	 * @param length the length of the randomly generated string.
	 * @return the randomly generated string.
	 */
	public static String generateString(int length) {
		StringBuilder tokenBuilder = new StringBuilder();
		for(int i = 0; i < length; i++) {
			tokenBuilder.append(RandomUtil.pickRandom(characterSet));
		}
		return tokenBuilder.toString();
	}
}
