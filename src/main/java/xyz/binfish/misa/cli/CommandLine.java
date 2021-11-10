package xyz.binfish.misa.cli;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class CommandLine {

	/*
	 * The unrecognized options/arguments.
	 */
	private List<String> args = new LinkedList<>();

	/*
	 * The processed options.
	 */
	private List<Option> options = new ArrayList<>();

	protected CommandLine() { }

	/*
	 * Add unrecognized option/argument.
	 *
	 * @param arg the unrecognized option/argument.
	 */
	protected void addArg(String arg) {
		args.add(arg);
	}

	/*
	 * Add option to the command line.
	 *
	 * @param opt the processed option.
	 */
	protected void addOption(Option opt) {
		options.add(opt);
	}

	/*
	 * Retrieve any non-recognized options and arguments.
	 *
	 * @return passed item in but not parsed as an List.
	 */
	public List<String> getArgsList() {
		return args;
	}

	/*
	 * Retrieve any non-recognized options and arguments.
	 *
	 * @return passed item in but not parsed as an array.
	 */
	public String[] getArgs() {
		return args.toArray(new String[args.size()]);
	}


	/*
	 * Get an array of the processed options.
	 *
	 * @retrun the array of the processed options.
	 */
	public Option[] getOptions() {
		return options.toArray(new Option[options.size()]);
	}

	/*
	 * Retrieve the argument of this option, if any.
	 *
	 * @param opt the character name of the option.
	 * @return value of the argument if option is set, and has an argument, otherwise null.
	 */
	public String getOptionValue(char opt) {
		return getOptionValue(String.valueOf(opt));
	}

	/*
	 * Retrieve the argument of this option, if any.
	 *
	 * @param opt          the character name of the option.
	 * @param defaultValue the default value to be returned if the option is not set.
	 * @return value of the argument if option is set, and has an argument,
	 *         otherwise returned default value.
	 */
	public String getOptionValue(char opt, String defaultValue) {
		return getOptionValue(String.valueOf(opt), defaultValue);
	}

	/*
	 * Retrieve the argument of this option, if any.
	 *
	 * @param option the option object.
	 * @return value of the argument if option is set, and has an argument, otherwise null.
	 */
	public String getOptionValue(Option option) {
		if(option == null) {
			return null;
		}

		return option.getValue();
	}

	/*
	 * Retrieve the argument of this option, if any.
	 *
	 * @param option       the option object.
	 * @param defaultValue the default value to be returned if the option is not set.
	 * @return value of the argument if option is set, and has an argument,
	 *         otherwise returned default value.
	 */
	public String getOptionValue(Option option, String defaultValue) {
		String answer = getOptionValue(option);
		return answer != null ? answer : defaultValue;
	}

	/*
	 * Retrieve the argument of this option, if any.
	 *
	 * @param opt the name of the option
	 * @return value of the argument if option is set, and has an argument, otherwise null.
	 */
	public String getOptionValue(String opt) {
		return getOptionValue(resolveOption(opt));
	}

	/*
	 * Retrieve the argument of this option, if any.
	 *
	 * @param opt          the name of the option.
	 * @param defaultValue the default value to be returned if the option is not set.
	 * @return value of the argument if option is set, and has an argument,
	 *         otherwise returned default value.
	 */
	public String getOptionValue(String opt, String defaultValue) {
		return getOptionValue(resolveOption(opt), defaultValue);
	}

	/*
	 * Checks if an option has been set.
	 *
	 * @param opt the character name of the option.
	 * @return true if set, false if not.
	 */
	public boolean hasOption(char opt) {
		return hasOption(String.valueOf(opt));
	}

	/*
	 * Checks if an option has been set.
	 *
	 * @param opt the option object to check.
	 * @return true if set, false if not.
	 */
	public boolean hasOption(Option opt) {
		return options.contains(opt);
	}

	/*
	 * Checks if an option has been set.
	 *
	 * @param opt the name of the option.
	 * @return true if set, false if not.
	 */
	public boolean hasOption(String opt) {
		return hasOption(resolveOption(opt));
	}

	/*
	 * Retrieves the option object given the long or short option as a String.
	 *
	 * @param opt the name of the option.
	 * @return canonicalized option.
	 */
	private Option resolveOption(String opt) {
		opt = CLIUtil.stripLeadingHyphens(opt);
		for(Option option : options) {
			if(opt.equals(option.getOpt()) || opt.equals(option.getLongOpt())) {
				return option;
			}
		}

		return null;
	}
}
