package xyz.binfish.misa.cli;

public class Option {

	/*
	 * The name of the option.
	 */
	private final String option;
	
	/*
	 * The long representation of the option.
	 */
	private String longOption;

	/*
	 * Specifies whether the option has argument.
	 */
	private boolean hasArg;

	/*
	 * The argument value.
	 */
	private String argValue;

	/*
	 * Description of the option.
	 */
	private String description;

	/*
	 * Create an Option using the specified parameters.
	 *
	 * @param option      the short name of option.
	 * @param hasArg      the specifies whether the option has argument.
	 * @param description the describles the function of the option.
	 */
	public Option(final String option, boolean hasArg, String description) {
		this(option, null, hasArg, description);
	}

	/*
	 * Create an Option using the specified parameters.
	 *
	 * @param option      the short representation of the option.
	 * @param longOption  the long representation of the option.
	 * @param hasArg      the specifies whether the option has argument.
	 * @param description the describles the function of the option.
	 */
	public Option(final String option, String longOption, boolean hasArg, String description) {
		this.option = optionValidate(option);
		this.longOption = longOption;
		this.hasArg = hasArg;
		this.description = description;
	}

	/*
	 * Retrieve the name of this Option.
	 *
	 * @return the name of this option.
	 */
	public String getOpt() {
		return option;
	}

	/*
	 * Retrieve the long name of this Option.
	 *
	 * @return the long name of this option, or null, if there is no long name.
	 */
	public String getLongOpt() {
		return longOption;
	}

	/*
	 * Returns the 'unique' Option identifier.
	 *
	 * @return the 'unique' Option identifier.
	 */
	public String getKey() {
		return option == null ? longOption : option;
	}

	/*
	 * Retrieve the self-documenting description of this Option.
	 *
	 * @return the string description of this option.
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * Set the value of argument to this Option.
	 *
	 * @param value the value to be set to this Option.
	 */
	public void setValue(String value) {
		this.argValue = value;
	}

	/*
	 * Get the value of argument in this Option.
	 *
	 * @return value of argument in this Option.
	 */
	public String getValue() {
		return argValue;
	}

	/*
	 * Clear the Option value. After Option value be equal null.
	 */
	public void clearValue() {
		this.argValue = null;
	}

	/*
	 * Checks if this Option has a argument.
	 *
	 * @return boolean flag indicating existence of a argument.
	 */
	public boolean hasArg() {
		return hasArg;
	}

	/*
	 * Checks if this Option has a long name.
	 *
	 * @return boolean flag indicating existence of a long name.
	 */
	public boolean hasLongOpt() {
		return longOption != null;
	}

	/*
	 * Get the name of this Option.
	 *
	 * @return the name of this option.
	 */

	private String optionValidate(final String option) {
		if(option == null) {
			return null;
		}

		if(option.length() == 1) {
			final char ch = option.charAt(0);

			if(!(Character.isJavaIdentifierPart(ch) || ch == '?' || ch == '@')) {
				throw new IllegalArgumentException("Illegal option name '" + ch + "'");
			}
		} else {
			for(final char ch : option.toCharArray()) {
				if(!Character.isJavaIdentifierPart(ch)) {
					throw new IllegalArgumentException(
							String.format("The option '%s' contains an illegal character '%s'", option, ch)
					);
				}
			}
		}

		return option;
	}
}
