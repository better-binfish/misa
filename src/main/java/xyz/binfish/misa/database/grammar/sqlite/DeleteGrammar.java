package xyz.binfish.misa.database.grammar.sqlite;

import xyz.binfish.misa.database.grammar.QueryGrammar;
import xyz.binfish.misa.database.query.QueryBuilder;

public class DeleteGrammar extends QueryGrammar {

	@Override
	public String format(QueryBuilder builder) {
		query = "DELETE FROM ";

		addPart(String.format(" %s", formatField(builder.getTable())));

		buildWhereClause(builder);

		return finalize(builder);
	}

	@Override
	protected String finalize(QueryBuilder builder) {
		addPart(";");

		return query;
	}
}
