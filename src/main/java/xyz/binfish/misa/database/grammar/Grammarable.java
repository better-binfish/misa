package xyz.binfish.misa.database.grammar;

import java.util.Map;

import xyz.binfish.misa.database.schema.Table;
import xyz.binfish.misa.database.query.QueryBuilder;

public interface Grammarable {

	/*
	 * Create Database.QueryType#CREATE create grammar instance with the provided settings.
	 *
	 * @param table   the table that should be used for generating the grammar query.
	 * @param options the options that should be parsed to the grammar generator.
	 * @return the database query.
	 */
	String create(Table table, Map<String, Boolean> options);

	/*
	 * Create Database.QueryType#INSERT create grammar instance with the provided settings.
	 *
	 * @param query the query builder that should be used for generating the insert query.
	 * @return the database query.
	 */
	String insert(QueryBuilder query);

	/*
	 * Create Database.QueryType#SELECT create grammar instance with the provided settings.
	 *
	 * @param query the query builder that should be used for generating the select query.
	 * @return the database query.
	 */
	String select(QueryBuilder query);

	/*
	 * Create Database.QueryType#UPDATE create grammar instance with the provided settings.
	 *
	 * @param query the query builder that should be used for generating the update query.
	 * @return the database query.
	 */
	String update(QueryBuilder query);

	/*
	 * Create Database.QueryType#DELETE create grammar instance with the provided settings.
	 *
	 * @param query the query builder that should be used for generating the delete query.
	 * @return the database query.
	 */
	String delete(QueryBuilder query);
}
