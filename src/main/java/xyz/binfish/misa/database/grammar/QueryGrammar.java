package xyz.binfish.misa.database.grammar;

import java.sql.SQLException;

import xyz.binfish.misa.database.query.Clause;
import xyz.binfish.misa.database.query.QueryClause;
import xyz.binfish.misa.database.query.QueryBuilder;
import xyz.binfish.misa.database.query.OperatorType;
import xyz.binfish.misa.exceptions.DatabaseException;

public abstract class QueryGrammar extends Grammar {

	/*
	 * The query formatter method, this is called by the query
	 * builder when the query should be built.
	 *
	 * @param builder the query builder to format.
	 * @return the formatter query.
	 */
	public abstract String format(QueryBuilder builder);

	/*
	 * Adds the last few touches the query needs to be ready to be executed.
	 *
	 * @param builder the query builder to finalize.
	 * @return the finalized query.
	 */
	protected abstract String finalize(QueryBuilder builder);

	/*
	 * Build the where clauses for the provided query builder.
	 *
	 * @param builder the query builder to build the where clauses from.
	 */
	protected void buildWhereClause(QueryBuilder builder) {
		if(builder.getWhereClauses().isEmpty()) {
			return;
		}

		addPart(" WHERE ");

		boolean first = true;

		for(QueryClause obj : builder.getWhereClauses()) {
			if(obj instanceof Clause) {
				Clause clause = (Clause) obj;

				addClause(clause, first);
				first = false;

				continue;
			}
		}
	}

	private void addClause(Clause clause, boolean exemptOperator) {
		if(clause.getOrder() == null) {
			clause.setOrder(OperatorType.AND);
		}

		if(clause.getTwo() == null) {
			throw new DatabaseException("Invalid 2nd clause given, the clause comparator can not be NULL! Query so far: " + getQuery(),
				new SQLException("Invalid 2nd clause given, the clause comparator can not be NULL!")
			);
		}

		String field = clause.getTwo().toString();
		if(!isNumeric(field)) {
			field = String.format("'%s'", field);
		}

		String stringClause = String.format("%s %s %s", formatField(clause.getOne()), clause.getIdentifier(), field);

		String operator = "";
		if(!exemptOperator) {
			operator = clause.getOrder().getOperator() + " ";
		}

		addRawPart(String.format("%s%s ", operator, stringClause));
	}
}
