package xyz.binfish.misa.cli;

import xyz.binfish.misa.exceptions.ParseException;

public class CommandLineParser {

	/*
	 * The command line instance.
	 */
	protected CommandLine cmd;

	/*
	 * The current options.
	 */
	protected Options options;

	/*
	 * Handles an unknown token. If the token starts with a dash an ParseException is thrown.
	 * Otherwise the token is added to the arguments of the command line.
	 *
	 * @param token the command line token to handle.
	 */
	private void handleUnknownToken(String token) throws ParseException {
		if(token.startsWith("-")) {
			throw new ParseException("Unrecognized option: " + token);
		}

		cmd.addArg(token);
	}

	/*
	 * Handle short and long option tokens with one hyphen.
	 *
	 * @param token the command line token to handle.
	 */
	private void handleShortAndLongOption(String token) throws ParseException {
		String t = CLIUtil.stripLeadingHyphens(token);
		int ePos = t.indexOf('=');

		if(t.length() == 1) {
			if(options.hasShortOption(t)) {
				cmd.addOption(options.getOption(t));
			} else {
				handleUnknownToken(token);
			}
		} else if(ePos == -1) { // no equal sign found
			if(options.hasShortOption(t)) {
				cmd.addOption(options.getOption(t));
			} else if(options.hasLongOption(t)) {
				handleLongOptionWithoutEqual(t);
			}
		} else { // equal sign found
			String opt = t.substring(0, ePos);
			String value = t.substring(ePos + 1);

			if(opt.length() == 1) {
				Option option = options.getOption(opt);
				if(option != null) {
					option.setValue(value);
					cmd.addOption(option);
				}
			} else {
				handleLongOptionWithEqual(t);
			}
		}
	}

	/*
	 * Handle long option tokens with arguments.
	 *
	 * @param token the command line token to handle.
	 */
	private void handleLongOptionWithEqual(String token) throws ParseException {
		int pos = token.indexOf('=');

		String value = token.substring(pos + 1);
		String opt = token.substring(0, pos);

		if(!options.hasLongOption(token) || !options.hasOption(token)) {
			handleUnknownToken(token);
		}
		Option option = options.getOption(
			options.hasLongOption(opt) ? opt : options.getOption(opt).getOpt()
		);

		if(option != null) {
			option.setValue(value);
			cmd.addOption(option);
		}
	}

	/*
	 * Handle long option tokens without arguments.
	 *
	 * @param token the command line token to handle.
	 */
	private void handleLongOptionWithoutEqual(String token) throws ParseException {
		if(!options.hasLongOption(token) && !options.hasOption(token)) {
			handleUnknownToken(token);
			return;
		}

		cmd.addOption(options.getOption(
			options.hasLongOption(token) ? token : options.getOption(token).getOpt()
		));
	}

	/*
	 * Handle long option tokens to reference #handleLongOptionWihtEqual when
	 * long option token hava argumen, otherwise #handleLongOptionWithoutEqual.
	 *
	 * @param token the command line token to handle.
	 */
	private void handleLongOption(String token) throws ParseException {
		token = CLIUtil.stripLeadingHyphens(token);

		if(token.indexOf('=') == -1) {
			handleLongOptionWithoutEqual(token);
		} else {
			handleLongOptionWithEqual(token);
		}
	}

	/*
	 * Handles any command line token.
	 *
	 * @param token the command line token to handle.
	 * @throws ParseException.
	 */
	private void handleToken(String token) throws ParseException {
		if(token.startsWith("--") && !"--".equals(token)) {
			handleLongOption(token);
		} else if(token.startsWith("-") && !"-".equals(token)) {
			handleShortAndLongOption(token);
		} else {
			handleUnknownToken(token);
		}
	}

	/*
	 * Parses the arguments according to the specified options.
	 * 
	 * @param options   the specified Options object.
	 * @param arguments the command line arguments.
	 * @return the prepared CommandLine object. 
	 * @throws ParseException if there are any problems encountered while parsing the command line token.
	 */
	public CommandLine parse(Options options, String[] arguments) throws ParseException {
		this.options = options;
		this.cmd = new CommandLine();

		if(arguments != null) {
			for(String argument : arguments) {
				handleToken(argument);
			}
		}

		return cmd;
	}
}
