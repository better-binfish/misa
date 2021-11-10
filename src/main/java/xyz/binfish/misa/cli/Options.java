package xyz.binfish.misa.cli;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

public class Options {

	/*
	 * The map of the options with the character key.
	 */
	private final Map<String, Option> shortOpts = new LinkedHashMap<>();

	/*
	 * The map of the options with the long key.
	 */
	private final Map<String, Option> longOpts = new LinkedHashMap<>();

	/*
	 * Add an option instance.
	 *
	 * @param opt the option that is to be added.
	 * @return the resulting Options instance.
	 */
	public Options addOption(Option opt) {
		String key = opt.getKey();

		if(opt.hasLongOpt()) {
			longOpts.put(opt.getLongOpt(), opt);
		}

		shortOpts.put(key, opt);

		return this;
	}

	/*
	 * Add an option that contains a short-name and long-name.
	 *
	 * @param opt         the short name from single-character of the option.
	 * @param longOpt     the long name from multi-character of the option.
	 * @param hasArg      the flag signalling if an argument is required after option.
	 * @param description the self-documenting description.
	 */
	public Options addOption(final String opt, String longOpt, boolean hasArg, String description) {
		addOption(new Option(opt, longOpt, hasArg, description));
		return this;
	}

	/*
	 * Get the Option matching the long or short name specified.
	 *
	 * @param opt the short or long name of the Option.
	 * @return the option represented by opt.
	 */
	public Option getOption(String opt) {
		opt = CLIUtil.stripLeadingHyphens(opt);

		if(hasShortOption(opt)) {
			return shortOpts.get(opt);
		}

		return longOpts.get(opt);
	}

	/*
	 * Returns whether the named Option is a member of this Options.
	 *
	 * @param opt the short or long name of the Option.
	 * @return true if the named Option is a member of this Options, false otherwise.
	 */
	public boolean hasOption(String opt) {
		opt = CLIUtil.stripLeadingHyphens(opt);
		return shortOpts.containsKey(opt) || longOpts.containsKey(opt);
	}

	/*
	 * Return whether the named Option is a member of this Options.
	 *
	 * @param opt the long name of the Option.
	 * @return true if the named Option is a member of this Options, false otherwise.
	 */
	public boolean hasLongOption(String opt) {
		return longOpts.containsKey(CLIUtil.stripLeadingHyphens(opt));
	}

	/*
	 * Return whether the named Option is a member of this Options.
	 *
	 * @param opt the short name of the Option.
	 * @return true if the named Option is a member of this Options, false otherwise.
	 */
	public boolean hasShortOption(String opt) {
		return shortOpts.containsKey(CLIUtil.stripLeadingHyphens(opt));
	}
	
	/*
	 * Return the List object of Options.
	 *
	 * @return the List of Options.
	 */
	public List<Option> getOptions() {
		return new ArrayList<>(shortOpts.values());
	}

	/*
	 * For debugging.
	 *
	 * @return stringified form of this object.
	 */
	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();

		buff.append("[ Options: [ short ")
			.append(shortOpts.toString())
			.append(" ] [ long ")
			.append(longOpts)
			.append(" ]");

		return buff.toString();
	}
}
