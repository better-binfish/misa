package xyz.binfish.misa.database.grammar;

import java.util.Arrays;
import java.util.List;

public abstract class Grammar {

	/*
	 * A list a SQL operators, this is used to compare and
	 * validate operators to make sure they're valid.
	 */
	protected final List<String> operators = Arrays.asList(
			"=", "<", ">", "<=", ">=", "<>", "!=",
			"LIKE", "LIKE BINARY", "NOT LIKE", "BETWEEN", "ILIKE",
			"&", "|", "^", "<<", ">>",
			"RLIKE", "REGEXP", "NOT REGEXP",
			"~", "~*", "!~*", "SIMILAR TO",
			"NOT SIMILAR TO"
	);

	/*
	 * A list of SQL order operators, this is used to compare
	 * and validate operators to make sure they're valid.
	 */
	protected final List<String> orderOperators = Arrays.asList("ASC", "DESC");

	/*
	 * The query SQL string, this string will be appended to
	 * and formatted by the addPart and removeList methods.
	 */
	protected String query;

	public String getQuery() {
		return query;
	}

	protected boolean isNumeric(String string) {
		return string.matches("[-+]?\\d*\\.?\\d+");
	}

	/*
	 * Adds the given part to the query.
	 *
	 * @param part the string to add.
	 * @return Grammar.
	 */
	public Grammar addPart(String part) {
		query = query.trim() + part;

		return this;
	}

	public Grammar addPart(String part, Object... params) {
		query = query.trim() + String.format(part, params);

		return this;
	}

	public Grammar addRawPart(String part) {
		query += part;

		return this;
	}

	public Grammar addRawPart(String part, Object... params) {
		query += String.format(part, params);

		return this;
	}

	/*
	 * Removes the given number of characters
	 * from the end of the query string.
	 *
	 * @param characters the amount of characters to remove.
	 * @return Grammar.
	 */
	protected Grammar removeLast(int characters) {
		query = query.substring(0, query.length() - characters);

		return this;
	}

	/*
	 * Formats a query field, splitting it up using dot-notation.
	 *
	 * @param field the field to format.
	 * @return the formated field.
	 */
	protected String formatField(String field) {
		field = field.trim();

		if(field.contains(" ")) {
			String[] both = field.split(" ");

			if(both.length == 3 && both[1].equalsIgnoreCase("as")) {
				return String.format("%s AS '%s'", formatField(both[0]), both[2]);
			}
		}

		if(field.contains(".")) {
			String[] both = field.split("\\.");
			String table = both[0];

			if(both.length == 2) {
				if(both[1].trim().equals("*")) {
					return String.format("`%s`.*", table);
				}

				return String.format("`%s`.`%s`", table, both[1]);
			}
		}

		return String.format("`%s`", field);
	}
}
