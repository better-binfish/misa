package xyz.binfish.misa.database.grammar;

import java.util.Map;

import xyz.binfish.misa.database.schema.Table;

public abstract class AlterGrammar extends Grammar {

	/*
	 * The query formatter method, this is called by the query
	 * builder when the query should be built.
	 *
	 * @param table   the table to build the query from.
	 * @param options the options provided to build the query.
	 * @return the formatted SQL qeury.
	 */
	public abstract String format(Table table, Map<String, Boolean> options);

	/*
	 * Add the last few touches the query needs to be ready to be executed.
	 *
	 * @param table the query table to finalize;
	 * @return the finalized SQL query.
	 */
	protected abstract String finalize(Table table);
}
