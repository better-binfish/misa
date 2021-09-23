package xyz.binfish.misa.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import xyz.binfish.misa.database.query.QueryBuilder;
import xyz.binfish.misa.database.collection.Collection;
import xyz.binfish.misa.database.grammar.Grammarable;
import xyz.binfish.misa.database.grammar.QueryGrammar;
import xyz.binfish.misa.database.grammar.AlterGrammar;
import xyz.binfish.misa.database.schema.Table;
import xyz.binfish.misa.database.StatementInterface;
import xyz.binfish.misa.exceptions.UnimplementedParameterException;

public abstract class Database implements DatabaseConnection, Grammarable {

	/*
	 * The main database manager instance
	 */
	protected DatabaseManager dbm = null;

	/*
	 * Represents our current database connection, this is
	 * used to send queries to the database, as well as
	 * fetch, and persist data.
	 */
	protected Connection connection;

	/*
	 * Sets the DatabaseManager instance to the database.
	 *
	 * @param dbm the database manager class instance.
	 */
	public Database(DatabaseManager dbm) {
		this.dbm = dbm;
	}

	/*
	 * Initialize the database abstraction, this should be called
	 * by the open method if it's necessary.
	 *
	 * @return TRUE if the initialization didn't throw any errors or exceptions
	 *         or FALSE if something happened during the initialization.
	 */
	protected abstract boolean initialize();

	/*
	 * Check a statement for faults, issues, overlaps,
	 * deprecated calls and other issues.
	 *
	 * @param paramStatement the statement to check.
	 * @throws SQLException if a database access error occurs.
	 */
	protected abstract void queryValidation(StatementInterface paramStatement) throws SQLException;

	/*
	 * Attempts to close the database connection.
	 *
	 * @return true if the database connection was closed successfully
	 * or false if the connection is alrady close, or an exception was thrown.
	 * @throws SQLException.
	 */
	public final boolean close() throws SQLException {
		if(connection == null) {
			dbm.logger.warn("Could not close connection, it is null.");
			return false;
		}

		try {
			connection.close();
			return true;
		} catch(SQLException e) {
			dbm.logger.warn("Could not close connection, SQLException: " + e.getMessage());
		}

		return false;
	}

	/*
	 * Returns the current database connection, if the connection is not open/active,
	 * it will attempt to open the connection.
	 */
	public Connection getConnection() throws SQLException {
		if(!isOpen()) {
			open();
		}

		return connection;
	}

	/*
	 * Check to see if the database connection is still valid.
	 *
	 * @return true if the database connection is open
	 * or false if the database connection is closed.
	 */
	public final boolean isOpen() {
		if(connection != null) {
			try {
				if(connection.isClosed()) {
					return false;
				}
			} catch(SQLException e) {
				dbm.logger.warn("Failed to check if the database connection is open due to a non transient connection exception!");
			}

			return true;
		}

		return false;
	}

	/*
	 * Queries the database with the given query, the
	 * query should be a SELECT query.
	 *
	 * @param sql the sql query to run.
	 * @return the result as a ResultSet object or null.
	 * @throws SQLException if a database access error occurs.
	 */
	public final ResultSet query(String sql) throws SQLException {
		queryValidation(getStatement(sql));

		Statement stmt = prepare(sql);
		stmt.closeOnCompletion();

		if(stmt instanceof PreparedStatement) {
			return ((PreparedStatement) stmt).executeQuery();
		}

		if(stmt.execute(sql)) {
			return stmt.getResultSet();
		}

		throw new SQLException("The query failed to execute successfully: " + sql);
	}

	/*
	 * Queries the database with the given query, the
	 * query should be a SELECT query.
	 *
	 * @param query the sql query to run.
	 * @return the result as a ResultSet object or null.
	 * @throws SQLException if a database access error occurs.
	 */
	public final ResultSet query(QueryBuilder query) throws SQLException {
		return query(query.toSQL());
	}

	/*
	 * Queries the database with the given prepared statement.
	 *
	 * @param query     the prepared statement to run.
	 * @param statement the query statement.
	 * @return result as a ResultSet object or null.
	 * @throws SQLException if a database access error occurs.
	 */
	public final ResultSet query(PreparedStatement query, StatementInterface statement) throws SQLException {
		queryValidation(statement);

		if(query.execute()) {
			return query.getResultSet();
		}

		throw new SQLException("The query failed to execute successfully: " + query);
	}

	/*
	 * Prepares a query as a prepared statement before executing it.
	 *
	 * @param query the query to prepare.
	 * @return the JDBC prepared statement object for the given query.
	 * @throws SQLException if a database access error occurs.
	 */
	public final Statement prepare(String query) throws SQLException {
		return getConnection().prepareStatement(query);
	}

	protected Collection runQuery(String query) throws SQLException {
		try(ResultSet resultSet = query(query)) {
			return new Collection(resultSet);
		} catch(SQLTransactionRollbackException e) {
			throw new SQLTransactionRollbackException(e.getMessage()); 
		}
	}

	protected int runQueryUpdate(String query) throws SQLException {
		try(Statement stmt = prepare(query)) {
			if(stmt instanceof PreparedStatement) {
				return ((PreparedStatement) stmt).executeUpdate();
			}

			return stmt.executeUpdate(query);
		} catch(SQLTransactionRollbackException e) {
			throw new SQLTransactionRollbackException(e.getMessage());
		}
	}

	protected Set<Integer> runQueryInsert(String query) throws SQLException {
		try(PreparedStatement stmt = getConnection().prepareStatement(query,
					Statement.RETURN_GENERATED_KEYS)) {
			stmt.executeUpdate();

			Set<Integer> ids = new HashSet<>();

			ResultSet keys = stmt.getGeneratedKeys();
			while(keys.next()) {
				ids.add(keys.getInt(1));
			}

			return ids;
		} catch(SQLTransactionRollbackException e) {
			throw new SQLTransactionRollbackException(e.getMessage());
		}
	}

	protected Set<Integer> runQueryInsert(QueryBuilder queryBuilder) throws SQLException {
		String query = queryBuilder.toSQL();

		try(PreparedStatement stmt = getConnection().prepareStatement(query,
					Statement.RETURN_GENERATED_KEYS)) {
			int preparedIndex = 1;

			for(Map<String, Object> row : queryBuilder.getItems()) {
				for(Map.Entry<String, Object> item : row.entrySet()) {
					if(item.getValue() == null) {
						continue;
					}

					String value = item.getValue().toString();

					if(value.startsWith("RAW:") ||
					   value.equalsIgnoreCase("true") ||
					   value.equalsIgnoreCase("false") ||
					   value.matches("[-+]?\\d*\\.?\\d+")) {
					   continue;
					}

					stmt.setString(preparedIndex++, value);
				}
			}

			stmt.executeUpdate();

			Set<Integer> ids = new HashSet<>();

			ResultSet keys = stmt.getGeneratedKeys();
			while(keys.next()) {
				ids.add(keys.getInt(1));
			}

			return ids;
		} catch(SQLTransactionRollbackException e) {
			throw new SQLTransactionRollbackException(e.getMessage());
		}
	}

	protected String setupAndRun(QueryGrammar grammar, QueryBuilder builder) {
		return grammar.format(builder);
	}

	protected String setupAndRun(AlterGrammar grammar, Table table, Map<String, Boolean> options) {
		return grammar.format(table, options);
	}
}
