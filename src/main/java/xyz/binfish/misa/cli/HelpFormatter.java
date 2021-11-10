package xyz.binfish.misa.cli;

import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class HelpFormatter {

	/*
	 * This class implements the Comparator interface for comparing Options.
	 */
	private static class OptionComparator implements Comparator<Option> {

		/*
		 * Compares two argument for order. Return negative integer, zero, or a positive
		 * integer as the first argument is less than, equal to, or greater than the second.
		 *
		 * @param opt1 the first Option to be compared.
		 * @param opt2 the second Option to be compared.
		 * @return a negative integer, zero, or positive integer as the first argument
		 *         is less than, equal to, or greater than the second.
		 */
		public int compare(Option opt1, Option opt2) {
			return opt1.getKey().compareToIgnoreCase(opt2.getKey());
		}
	}

	/*
	 * Default number of character per line.
	 */
	public static final int DEFAULT_WIDTH = 74;

	/*
	 * Default padding to the left of each line.
	 */
	public static final int DEFAULT_LEFT_PAD = 1;

	/*
	 * Default number of space characters to be prefixed to each description line.
	 */
	public static final int DEFAULT_DESC_PAD = 3;

	/*
	 *
	 */
	public static final String DEFAULT_NEW_LINE = System.getProperty("line.separator");

	/*
	 * Default string to display at the beginning of the usage statement.
	 */
	public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

	/*
	 * Default prefix for short options.
	 */
	public static final String DEFAULT_OPT_PREFIX = "-";

	/*
	 * Default prefix for long options.
	 */
	public static final String DEFAULT_LONG_OPT_PREFIX = "--";

	/*
	 * Default name for an argument.
	 */
	public static final String DEFAULT_ARG_NAME = "arg";

	/*
	 * Comparator used to sort the options when they output in help text.
	 *
	 * Defaults to case-insensitive alphabetical sorting by option key.
	 */
	protected Comparator<Option> optionComparator = new OptionComparator();

	/*
	 * Return a String of padding of specified length.
	 * 
	 * @param len the length of the String of padding to create.
	 * @return the new String object of padding.
	 */
	protected String createPadding(int len) {
		char[] padding = new char[len];
		Arrays.fill(padding, ' ');

		return new String(padding);
	}

	/*
	 * Find the next text wrap position after starter position for the text with the column
	 * width. The wrap point is the last position before starting position having a whitespace
	 * character ( space, \n, \r ).
	 *
	 * @param text     the text being searched for the wrap position.
	 * @param startPos the position from which to start the lookup whitespace character.
	 * @return position on which the text must be wrapped or -1 if the wrap position is at the
	 *         end of the text.
	 */
	protected int findWrapPos(String text, int startPos) {
		int pos = text.indexOf('\n', startPos);
		if(pos != -1 && pos <= DEFAULT_WIDTH) {
			return pos + 1;
		}

		pos = text.indexOf('\t', startPos);
		if(pos != -1 && pos <= DEFAULT_WIDTH) {
			return pos + 1;
		}

		if(startPos + DEFAULT_WIDTH >= text.length()) {
			return -1;
		}

		for(pos = startPos + DEFAULT_WIDTH; pos >= startPos; --pos) {
			char c = text.charAt(pos);
			if(c == ' ' || c == '\n' || c == '\r') {
				break;
			}
		}

		if(pos > startPos) {
			return pos;
		}

		pos = startPos + DEFAULT_WIDTH;

		return pos == text.length() ? -1 : pos;
	}

	/*
	 * Remove the trailing whitespace from the specified string.
	 *
	 * @param s the string to remove the trailing padding from.
	 * @return the string of without the trailing padding.
	 */
	protected String rtrim(String s) {
		if(s == null || s.isEmpty()) {
			return s;
		}

		int pos = s.length();

		while(pos > 0 && Character.isWhitespace(s.charAt(pos - 1))) {
			--pos;
		}

		return s.substring(0, pos);
	}

	/*
	 * Render the specified text and return the rendered options in a StringBuffer.
	 *
	 * @param sb              the StringBuffer to place the rendered text into.
	 * @param nextLineTabStop the position on the next line for the first tab.
	 * @param text            the text to be rendered.
	 * @return the StringBuffer object with the rendered options contents.
	 */
	protected StringBuffer renderWrappedText(StringBuffer sb, int nextLineTabStop, String text) {
		int pos = findWrapPos(text, 0);

		if(pos == -1) {
			sb.append(rtrim(text));
			return sb;
		}

		sb.append(rtrim(text.substring(0, pos))).append(DEFAULT_NEW_LINE);

		if(nextLineTabStop >= DEFAULT_WIDTH) {
			nextLineTabStop = 1;
		}

		String padding = createPadding(nextLineTabStop);

		while(true) {
			text = padding + text.substring(pos).trim();
			pos = findWrapPos(text, 0);

			if(pos == -1) {
				sb.append(text);
				return sb;
			}

			if(text.length() > DEFAULT_WIDTH && pos == nextLineTabStop - 1) {
				pos = DEFAULT_WIDTH;
			}

			sb.append(rtrim(text.substring(0, pos))).append(DEFAULT_NEW_LINE);
		}
	}

	/*
	 * Print the help for options. This method prints help information to System.out.
	 *
	 * @param options the Options instance.
	 */
	public void printHelp(Options options) {
		PrintWriter pw = new PrintWriter(System.out);

		pw.println(DEFAULT_SYNTAX_PREFIX + DEFAULT_NEW_LINE);
		printOptions(pw, options);

		pw.flush();
	}

	/*
	 * Print the help for the specified options to the specified writer.
	 *
	 * @param pw      the PrintWriter to write the help to.
	 * @param options the command line options.
	 */
	public void printOptions(PrintWriter pw, Options options) {
		StringBuffer buff = new StringBuffer();

		String lpad = createPadding(DEFAULT_LEFT_PAD);
		String dpad = createPadding(DEFAULT_DESC_PAD);

		int max = 0;
		List<StringBuffer> prefixList = new ArrayList<>();
		List<Option> optList = new ArrayList<>(options.getOptions());

		Collections.sort(optList, optionComparator);

		for(Option option : optList) {
			StringBuffer optBuff = new StringBuffer();

			optBuff.append(lpad).append(DEFAULT_OPT_PREFIX).append(option.getOpt());

			if(option.hasLongOpt()) {
				optBuff.append(',').append(DEFAULT_LONG_OPT_PREFIX).append(option.getLongOpt());
			}

			if(option.hasArg()) {
				optBuff.append(" <").append(DEFAULT_ARG_NAME).append(">");
			}

			prefixList.add(optBuff);
			max = optBuff.length() > max ? optBuff.length() : max;
		}

		int x = 0;

		for(Iterator<Option> it = optList.iterator(); it.hasNext(); ) {
			Option option = it.next();
			StringBuilder optBuff = new StringBuilder(prefixList.get(x++).toString());

			if(optBuff.length() < max) {
				optBuff.append(createPadding(max - optBuff.length()));
			}

			optBuff.append(dpad);

			int nextLineTabStop = max + DEFAULT_DESC_PAD;

			if(option.getDescription().length() != 0) {
				optBuff.append(option.getDescription());
			}

			renderWrappedText(buff, nextLineTabStop, optBuff.toString());

			if(it.hasNext()) {
				buff.append(DEFAULT_NEW_LINE);
			}
		}

		pw.println(buff.toString());
	}
}
