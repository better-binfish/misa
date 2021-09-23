package xyz.binfish.misa.database;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.ResultSet;

import java.util.Map;

import xyz.binfish.misa.database.schema.Table;

public interface DatabaseConnection {

	/*
	 * Attempts to open the database connection to the database type,
	 * this will return true if it manages to connect to the database,
	 * and false otherwise.
	 *
	 * @return true if the database connection is open, false otherwise.
	 * @throws SQLException        if a database access error occurs.
	 * @throws SQLTimeoutException when the driver has timeout.
	 */
	boolean open() throws SQLException;

	/*
	 * Attempts to get the database statement from the query.
	 *
	 * @param query the query to check.
	 * @return the implementation of the statement contract.
	 * @throws SQLException if a database access error occurs.
	 */
	StatementInterface getStatement(String query) throws SQLException;

	/*
	 * Attempts to find out if the parsed string is a table.
	 *
	 * @param tableName the table name to check.
	 * @return true if the table exists, false otherwise.
	 */
	boolean hasTable(String tableName);

	/*
	 * Attempts to truncate the given table, this will delete
	 * every recotd in the table and reset it completely.
	 *
	 * @param tableName the table name to truncate.
	 * @return true if the table was successfully reset, false otherwise.
	 */
	boolean truncate(String tableName);
}
