package xyz.binfish.misa.database.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Arrays;
import java.util.Collections;

import java.sql.SQLException;

import xyz.binfish.misa.database.collection.Collection;
import xyz.binfish.misa.database.DatabaseManager;
import xyz.binfish.logger.Logger;

public class QueryBuilder {

	private static Logger logger = Logger.getLogger();

	/*
	 * The instance of the DatabaseManager
	 */
	private DatabaseManager dbm;

	/*
	 * The table the query builder to used.
	 */
	private String table = null;

	/*
	 * The query type that's being preformed.
	 */
	private QueryType type;

	/*
	 * The amount of rows to take(LIMIT), if the value is set
	 * to -1 it should be ignored.
	 */
	private int take = -1;

	/*
	 * The amount of rows to skip(OFFSET), if the value is set
	 * to -1 it should be ignored.
	 */
	private int skip = -1;

	/*
	 * The list of QueryOrder clauses that should be used by the generator.
	 */
	private final List<QueryOrder> order = new ArrayList<>();

	/*
	 * The list of Clause clauses that should be used by the generator.
	 */
	private final List<QueryClause> wheres = new ArrayList<>();

	/*
	 * The list of String clauses that should be used by the generator.
	 */
	private final List<String> columns = new ArrayList<>();

	/*
	 * The list of JoinClause objects that should be used by the generator.
	 */
	private final List<JoinClause> joins = new ArrayList<>();

	/*
	 * The list of Map objects that should be used by the generator, containing
	 * the column name as the key, and value for the column as a map value.
	 */
	private final List<Map<String, Object>> items = new ArrayList<>();

	/*
	 * Create new QueryBuilder instance.
	 *
	 * @param dbm the database manager instance.
	 */
	public QueryBuilder(DatabaseManager dbm) {
		this.dbm = dbm;

		table(table);
	}

	/*
	 * Create new QuaryBuilder instance.
	 *
	 * @param dbm the database manager instance.
	 * @param table the table the query builder should be generated for.
	 */
	public QueryBuilder(DatabaseManager dbm, String table) {
		this.dbm = dbm;

		table(table);
	}

	/*
	 * Sets the table that the query builder should be using, and sets the select state to ALL.
	 *
	 * @param table the table the query builder should be using.
	 * @return the query builder instance.
	 */
	public QueryBuilder table(String table) {
		return selectAll().from(table);
	}

	/*
	 * Sets the table the query should be generated for.
	 *
	 * @param table the table the query should be generated for.
	 * @return the query builder instance.
	 */
	public QueryBuilder from(String table) {
		this.table = table;

		return this;
	}

	/*
	 * Gets the table the query should be generated for.
	 *
	 * @return the table the query should be generated for.
	 */
	public String getTable() {
		return table;
	}

	/*
	 * Sets the select state to ALL using the star (*) symbol.
	 *
	 * @return the query builder instance.
	 */
	public QueryBuilder selectAll() {
		return select("*");
	}

	/*
	 * Selects the provided columns from the varargs columns object, columns
	 * parsed will automatically be formatted to SQL fields using the grave
	 * accent character (`), using the keyword AS will use the SQL AS to
	 * rename the output the collection will end up using.
	 *
	 * Example calling: select("username as name", "email", ...)
	 *
	 * @param columns the varargs list of columns that should be selected.
	 * @return the query builder instance.
	 */
	public QueryBuilder select(String ...colums) {
		type = QueryType.SELECT;

		for(String column : columns) {
			addColumn(column);
		}

		return this;
	}

	/*
	 * Create raw select statement, allowing you to parse in raw SQL that
	 * the generator won't modify or affect in any way.
	 *
	 * @param select the raw SQL select statement.
	 * @return the query builder instance.
	 */
	public QueryBuilder selectRaw(String select) {
		type = QueryType.SELECT;

		columns.clear();
		columns.add("RAW:" + select.trim());

		return this;
	}

	/*
	 * Add a column that should be selected.
	 *
	 * @param column the column that should be selected.
	 */
	protected void addColumn(String column) {
		if(!column.equals("*")) {
			columns.remove("*");

			if(!columns.contains(column)) {
				columns.add(column);
			}

			return;
		}

		columns.clear();
		columns.add("*");
	}

	/*
	 * Gets the columns that should be selected in a request.
	 *
	 * @return the columns that should be selected.
	 */
	public List<String> getColumns() {
		return columns;
	}

	/*
	 * Sets the amount of rows to skip using the SQL OFFSET.
	 *
	 * @param skip the amount of rows to skip.
	 * @return the query builder instance.
	 */
	public QueryBuilder skip(int skip) {
		this.skip = Math.max(skip, 0);

		return this;
	}

	/*
	 * Removes the skip (SQL OFFSET) from the query builder.
	 *
	 * @return the query builder instance.
	 */
	public QueryBuilder removeSkip() {
		this.skip = -1;

		return this;
	}

	/*
	 * Gets the amount to skip (SQL OFFSET) in the SQL query.
	 *
	 * @return the amount to skip.
	 */
	public int getSkip() {
		return skip;
	}

	/*
	 * Sets the amount of rows to take using the SQL LIMIT.
	 *
	 * @param take the amount of rows to take.
	 * @return the query builder instance.
	 */
	public QueryBuilder take(int take) {
		this.take = Math.max(take, 0);

		return this;
	}

	/*
	 * Removes the take (SQL LIMIT) from the query builder.
	 *
	 * @return the query builder instance.
	 */
	public QueryBuilder removeTake() {
		this.take = -1;

		return this;
	}

	/*
	 * Gets the amount to take (SQL LIMIT) in the SQL query.
	 *
	 * @return the amount to take.
	 */
	public int getTake() {
		return take;
	}

	/*
	 * Create a SQL WHERE clause with an equal operator.
	 *
	 * @param column the column to use in the clause.
	 * @param value  the value to compare the column to.
	 * @return the query builder instance.
	 */
	public QueryBuilder where(String column, Object value) {
		return where(column, "=", value);
	}

	/*
	 * Create a SQL WHERE clause with the provided operator.
	 *
	 * @param column   the column to use in the clause.
	 * @param operator the operator to compare with.
	 * @param value    the value to compare the column to.
	 * @return the query builder instance.
	 */
	public QueryBuilder where(String column, String operator, Object value) {
		wheres.add(new Clause(column, operator, value));

		return this;
	}

	/*
	 * Create a SQL AND WHERE clause with an equal operator.
	 *
	 * @param column the column to use in the clause.
	 * @param value  the value to compare the column to.
	 * @return the query builder instance.
	 */
	public QueryBuilder andWhere(String column, Object value) {
		return andWhere(column, "=", value);
	}

	/*
	 * Create a SQL AND WHERE clause with the provided operator.
	 *
	 * @param column   the column to use in the clause.
	 * @param operator the operator to compare with.
	 * @param value    the value to compare the column to.
	 * @return the query builder instance.
	 */
	public QueryBuilder andWhere(String column, String operator, Object value) {
		wheres.add(new Clause(column, operator, value, OperatorType.AND));

		return this;
	}

	/*
	 * Create a SQL OR WHERE clause with an equal operator.
	 *
	 * @param column the column to use in the clause.
	 * @param value  the value to compare the column to.
	 * @return the query builder instance.
	 */
	public QueryBuilder orWhere(String column, Object value) {
		return orWhere(column, "=", value);
	}

	/*
	 * Create a SQL OR WHERE clause with the provided operator.
	 *
	 * @param column   the column to use in the clause.
	 * @param operator the operator to compare with.
	 * @param value    the value to compare the column to.
	 * @return the query builder instance.
	 */
	public QueryBuilder orWhere(String column, String operator, Object value) {
		wheres.add(new Clause(column, operator, value, OperatorType.OR));

		return this;
	}

	/*
	 * Gets the list where clauses that should be generated.
	 *
	 * @return the list of where clauses that should be generated.
	 */
	public List<QueryClause> getWhereClauses() {
		return wheres;
	}

	/*
	 * Create a SQL ORDER BY clause, ordering by ascending order.
	 *
	 * @param field the field the query should be ordered by.
	 * @return the query builder instance.
	 */
	public QueryBuilder orderBy(String field) {
		return orderBy(field, "ASC");
	}

	/*
	 * Create a SQL ORDER BY clause, ordering by the provided type.
	 *
	 * @param field the field the query should be ordered by.
	 * @param type  the type to order the query by.
	 * @return the query builder instance.
	 */
	public QueryBuilder orderBy(String field, String type) {
		order.add(new QueryOrder(field, type));

		return this;
	}

	/*
	 * Create random query order statement.
	 *
	 * @return the query builder instance.
	 */
	public QueryBuilder inRandomOrder() {
		order.add(new QueryOrder("RAND()", null, true));

		return this;
	}

	/*
	 * Gets the list of order clauses that should be used in the generated query.
	 *
	 * @return the list of order clauses.
	 */
	public List<QueryOrder> getOrder() {
		return order;
	}

	/*
	 * Create JOIN clause on the provided table of the given type, once the join
	 * clause has been created, a JoinClause object will be returned to help specify
	 * what the clause should be bound to.
	 *
	 * @param table the table the join clause should be used on.
	 * @param type  the type of the join clause.
	 * @return the join clause that was created.
	 */
	public JoinClause join(String table, String type) {
		JoinClause join = new JoinClause(type, table);

		joins.add(join);

		return join;
	}

	/*
	 * Create LEFT JOIN clause on the provided table, once the join clause has been
	 * created, a JoinClause object will be returned to help specify what the
	 * clause should be bound to.
	 *
	 * @param table the table the join clause should be used on.
	 * @return the join clause that was created.
	 */
	public JoinClause leftJoin(String table) {
		return join(table, "left");
	}

	/*
	 * Create LEFT JOIN clause on the provided table, using the equal operator.
	 *
	 * @param table the table the join clause should be used on.
	 * @param one   the first field to bind on.
	 * @param two   the second field to bind on.
	 * @return the query builder instance.
	 */
	public QueryBuilder leftJoin(String table, String one, String two) {
		JoinClause join = leftJoin(table);

		join.on(one, two);

		return this;
	}

	/*
	 * Create LEFT JOIN clause on the provided table, using the provided operator.
	 *
	 * @param table    the table the join clause should be used on.
	 * @param one      the first field to bind on.
	 * @param operator the operator to compare with.
	 * @param two      the second field to bind on.
	 * @return the query builder instance.
	 */
	public QueryBuilder leftJoin(String table, String one, String operator, String two) {
		JoinClause join = leftJoin(table);

		join.on(one, operator, two);

		return this;
	}

	/*
	 * Create RIGHT JOIN clause on the provided table, once the join clause has been
	 * created, a JoinClause object will be returned to help specify what the clause
	 * should be bound to.
	 *
	 * @param table the table the join clause should be used on.
	 * @return the join clause that was created.
	 */
	public JoinClause rightJoin(String table) {
		return join(table, "right");
	}

	/*
	 * Create RIGHT JOIN clause on the provided table, using the equal operator.
	 *
	 * @param table the table the join clause should be used on.
	 * @param one   the first field to bind on.
	 * @param two   the second field to bind on.
	 * @return the query builder instance.
	 */
	public QueryBuilder rightJoin(String table, String one, String two) {
		JoinClause join = rightJoin(table);

		join.on(one, two);

		return this;
	}

	/*
	 * Create RIGHT JOIN clause on the provided table, using the provided operator.
	 *
	 * @param table    the table the join clause should be used on.
	 * @param one      the first field to bind on.
	 * @param operator the operator to compare with.
	 * @param two      the second field to bind on.
	 * @return the query builder instance.
	 */
	public QueryBuilder rightJoin(String table, String one, String operator, String two) {
		JoinClause join = rightJoin(table);

		join.on(one, operator, two);

		return this;
	}

	/*
	 * Create INNER JOIN clause on the provided table, once the join clause has been
	 * created, a JoinClause object will be returned to help specify
	 * what the clause should be bound to.
	 *
	 * @param table the table the join clause should be used on.
	 * @return the join clause that was created.
	 */
	public JoinClause innerJoin(String table) {
		return join(table, "inner");
	}

	/*
	 * Create INNER JOIN clause on the provided table, using the equal operator.
	 *
	 * @param table the table the join clause should be used on.
	 * @param one   the first field to bind on.
	 * @param two   the second field to bind on.
	 * @return the query builder instance.
	 */
	public QueryBuilder innerJoin(String table, String one, String two) {
		JoinClause join = innerJoin(table);

		join.on(one, two);

		return this;
	}

	/*
	 * Create INNER JOIN clause on the provided table, using the provided operator.
	 *
	 * @param table    the table the join clause should be used on.
	 * @param one      the first field to bind on.
	 * @param operator the operator to compare with.
	 * @param two      the second field to bind on.
	 * @return the query builder instance.
	 */
	public QueryBuilder innerJoin(String table, String one, String operator, String two) {
		JoinClause join = innerJoin(table);

		join.on(one, operator, two);

		return this;
	}

	/*
	 * Create OUTER JOIN claus eon the provided table, once the join clause has been
	 * created, a JoinClause object will be returned to help specify
	 * what the clause should be bound to.
	 *
	 * @param table the table the join clause should be used on.
	 * @return the join clause that was created.
	 */
	public JoinClause outerJoin(String table) {
		return join(table, "outer");
	}

	/*
	 * Creates OUTER JOIN clause on the provided table, using the equal operator.
	 *
	 * @param table the table the join clause should be used on.
	 * @param one   the first field to bind on.
	 * @param two   the second field to bind on.
	 * @return the query builder instance.
	 */
	public QueryBuilder outerJoin(String table, String one, String two) {
		 JoinClause join = outerJoin(table);

		 join.on(one, two);

		 return this;
	}

	/*
	 * Creates OUTER JOIN clause on the provided table, using the provided operator.
	 *
	 * @param table    the table the join clause should be used on.
	 * @param one      the first field to bind on.
	 * @param operator the operator to compare with.
	 * @param two      the second field to bind on.
	 * @return the query builder instance.
	 */
	public QueryBuilder outerJoin(String table, String one, String operator, String two) {
		JoinClause join = outerJoin(table);

		join.on(one, operator, two);

		return this;
	}

	/*
	 * Create FULL JOIN clause on the provided table, once the join clause has been
	 * created, a JoinClause object will be returned to help specify
	 * what the clause should be bound to.
	 *
	 * @param table the table the join clause should be used on.
	 * @return the join clause that was created.
	 */
	public JoinClause fullJoin(String table) {
		return join(table, "full");
	}

	/*
	 * Create FULL JOIN clause on the provided table, using the equal operator.
	 *
	 * @param table the table the join clause should be used on.
	 * @param one the first field to bind on.
	 * @param two the second field to bind on.
	 * @return the query builder instance.
	 */
	public QueryBuilder fullJoin(String table, String one, String two) {
		JoinClause join = fullJoin(table);

		join.on(one, two);

		return this;
	}

	/*
	 * Create FULL JOIN clause on the provided table, using the provided operator.
	 *
	 * @param table    the table the join clause should be used on.
	 * @param one      the first field to bind on.
	 * @param operator the operator to compare with.
	 * @param two      the second field to bind on.
	 * @return the query builder instance.
	 */
	public QueryBuilder fullJoin(String table, String one, String operator, String two) {
		JoinClause join = fullJoin(table);

		join.on(one, operator, two);

		return this;
	}

	/*
	 * Gets a list of all the JOIN clause.
	 *
	 * @return a list of all the JOIN clauses.
	 */
	public List<JoinClause> getJoins() {
		return joins;
	}

	/*
	 * Build SQL query, if an error occurs while building 
	 * the query NULL will be returned instead.
	 *
	 * @return the generated SQL query or NULL if an error occurred.
	 */
	public String toSQL() {
		return toSQL(type);
	}

	/*
	 * Build SQL query using the given query type, if an
	 * error occurs while building the query NULL will be returned instead.
	 *
	 * @return the generated SQL query or NULL if an error occurred.
	 */
	public String toSQL(QueryType type) {
		try {
			switch(type) {
				case SELECT:
					return dbm.getDatabase().select(this);
				case INSERT:
					return dbm.getDatabase().insert(this);
				case UPDATE:
					return dbm.getDatabase().update(this);
				case DELETE:
					return dbm.getDatabase().delete(this);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * Runs the Database#query method with the generated query.
	 *
	 * @return Collection object that contains the data produced
	 * by the given query.
	 * @throws SQLException if a database access error occurs.
	 */
	public Collection get() throws SQLException {
		String query = toSQL();

		return new Collection(dbm.getDatabase().query(query));
	}

	/*
	 * Run the DatabaseManager#queryUpdate method with the current
	 * instance of the query builder, and the given items from the changeable closure.
	 *
	 * @param closure the changeable closure that should be run.
	 * @return the row count for SQL Data Manipulation Language statements
	 * or 0 for SQL statements that return nothing.
	 * @throws SQLException if a database access error occurs.
	 */
	public int update(ChangeableClosure closure) throws SQLException {
		type = QueryType.UPDATE;

		ChangeableStatement statement = new ChangeableStatement(this);
		closure.run(statement);

		this.items.addAll(Collections.singletonList(statement.getItems()));

		try {
			return dbm.queryUpdate(this);
		} catch(SQLException e) {
			dbm.logger.error("Error thrown during update query: " + toSQL(), e);
		}

		return 0;
	}

	/*
	 * Run the DatabaseManager#queryUpdate method with current
	 * instance of the query builder.
	 *
	 * @param arrays the list of items that should be updated.
	 * @return the row count for SQL Data Manipulation Language statements
	 * or 0 for SQL statements that return nothing.
	 * @throws SQLException if a database access error occurs.
	 */
	public int update(List<String>... arrays) throws SQLException {
		return update(buildMapFromArrays(arrays));
	}

	/*
	 * Run the DatabaseManager#queryUpdate method with current
	 * instance of the query builder.
	 *
	 * @param items the map of items that should be updated.
	 * @return the row count for SQL Data Manipulation Language statements
	 * or 0 for SQL statements that return nothing.
	 * @throws SQLException if a database access error occurs.
	 */
	public int update(Map<String, Object>... items) throws SQLException {
		type = QueryType.UPDATE;

		this.items.addAll(Arrays.asList(items));

		try {
			return dbm.queryUpdate(this);
		} catch(SQLException e) {
			dbm.logger.error("Error thrown during update query: " + toSQL(), e);
		}

		return 0;
	}

	/*
	 * Run the DatabaseManager#queryInsert method with the current
	 * instance of the query builder, and the given items from the changeable closure.
	 *
	 * @param closure the changeable closure that should be run.
	 * @return Collection of the generated IDs.
	 * @throws SQLException if a database access error occurs.
	 */
	public Collection insert(ChangeableClosure closure) throws SQLException {
		type = QueryType.INSERT;

		ChangeableStatement statement = new ChangeableStatement(this);
		closure.run(statement);

		this.items.addAll(Collections.singletonList(statement.getItems()));

		try {
			return runInsertQuery();
		} catch(SQLException e) {
			dbm.logger.error("Error thrown during insert query: " + toSQL(), e);
		}

		return new Collection();
	}

	/*
	 * Run the DatabaseManager#queryInsert method with the current
	 * instance of the query builder.
	 *
	 * @param arrays the list of items that should be inserted.
	 * @return Collection.
	 * @throws SQLException if a database access error occurs.
	 */
	public Collection insert(List<String>... arrays) throws SQLException {
		return insert(buildMapFromArrays(arrays));
	}

	/*
	 * Run the DatabaseManager#queryInsert method with the current
	 * instance of the query builder.
	 *
	 * @param items the map of items that should be inserted.
	 * @return Collection of the generated IDs.
	 * @throws SQLException if a database access error occurs;
	 */
	public Collection insert(Map<String, Object>... items) throws SQLException {
		type = QueryType.INSERT;

		this.items.addAll(Arrays.asList(items));

		try {
			return runInsertQuery();
		} catch(SQLException e) {
			dbm.logger.error("Error thrown during insert query: " + toSQL(), e);
		}

		return new Collection();
	}

	/*
	 * Run the DatabaseManager#queryUpdate method with the current
	 * instance of the query builder.
	 *
	 * @return the row count for SQL Data Manipulation Language statements
	 * or 0 for SQL statements that return nothing.
	 * @throws SQLException if a database access error occurs.
	 */
	public int delete() throws SQLException {
		type = QueryType.DELETE;

		try {
			return dbm.queryUpdate(this);
		} catch(SQLException e) {
			dbm.logger.error("Error thrown during delete query: " + toSQL(), e);
		}

		return 0;
	}

	/*
	 * Builds a Map from a List object.
	 *
	 * @param arrays the list to build the map from.
	 * @return the map that was build from the list.
	 */
	private Map<String, Object> buildMapFromArrays(List<String>... arrays) {
		Map<String, Object> map = new HashMap<>();

		for(List<String> array : arrays) {
			if(array.size() != 2) {
				continue;
			}

			map.put(array.get(0), array.get(1));
		}

		return map;
	}

	/*
	 * Run the insert query and builds a collection of IDs for
	 * all the new rows that was created by the query.
	 *
	 * @return the collection of IDs for the created rows.
	 * @throws SQLException if a database access error occurs.
	 */
	private Collection runInsertQuery() throws SQLException {
		Set<Integer> keys = dbm.queryInsert(this);
		List<Map<String, Object>> collectionItems = new ArrayList<>();

		for(int id : keys) {
			Map<String, Object> row = new HashMap<>();
			row.put("id", id);
			collectionItems.add(row);
		}

		return new Collection(collectionItems);
	}

	/*
	 * Get the list of item maps for the query builder.
	 *
	 * @return the list of item maps for the query builder.
	 */
	public List<Map<String, Object>> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return toSQL();
	}
}
