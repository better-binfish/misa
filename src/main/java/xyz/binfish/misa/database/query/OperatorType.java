package xyz.binfish.misa.database.query;

public enum OperatorType {
	
	/*
	 * The common SQL AND operator.
	 */
	AND("AND"),

	/*
	 * The common SQL OR operator.
	 */
	OR("OR");

	/*
	 * The operators string value.
	 */
	private final String operator;

	/*
	 * Create new operator type with the provided operator string value.
	 *
	 * @param operator the operator string value to use.
	 */
	private OperatorType(String operator) {
		this.operator = operator;
	}

	/*
	 * Gets the operator string value.
	 *
	 * @return the operator string value.
	 */
	public String getOperator() {
		return operator;
	}
}
