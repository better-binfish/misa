package xyz.binfish.misa.database.query;

import java.util.ArrayList;
import java.util.List;

public class JoinClause implements QueryClause {

	/*
	 * The type of join being performed.
	 */
	public String type;

	/*
	 * The table the join clause is joining to.
	 */
	public String table;

	/*
	 * The clauses for the join.
	 */
	public List<Clause> clauses = new ArrayList<>();

	/*
	 * Create a new join clause instance.
	 *
	 * @param type  the type of the join.
	 * @param table the table to join.
	 */
	public JoinClause(String type, String table) {
		this.type = type;
		this.table = table;
	}

	/*
	 * Adds a comparator to the join clause using the equal operator.
	 *
	 * @param one the first field to use in the join clause.
	 * @param two the second field to use in the join clause.
	 * @return the join clause instance.
	 */
	public JoinClause on(String one, String two) {
		return on(one, "=", two);
	}

	/*
	 * Adds a comparator to the join clause using the provided operator.
	 *
	 * @param one      the first field to use in the join clause.
	 * @param operator the operator to compare the fields with.
	 * @param two      the second field to use in the fields with.
	 * @return the join clause instance.
	 */
	public JoinClause on(String one, String operator, String two) {
		clauses.add(new Clause(one, operator, two));

		return this;
	}
}
