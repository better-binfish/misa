package xyz.binfish.misa.database.schema;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;

import xyz.binfish.misa.database.DatabaseManager;

public class Schema {

	/*
	 * The database manager instance.
	 */
	private final DatabaseManager dbm;

	/*
	 * Creates a new Schematic instance for the provided database manager instance.
	 *
	 * @param dbm the database manager instance the schema instance should be created for.
	 */
	public Schema(DatabaseManager dbm) {
		this.dbm = dbm;
	}

	public DatabaseManager getDatabaseManager() {
		return dbm;
	}

	/*
	 * Checks if the default connection has the provided table name.
	 *
	 * @param tableName the table to check if exists.
	 * @return true if the table exists, false otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public boolean hasTable(String tableName) throws SQLException {
		return dbm.getDatabase().hasTable(tableName);
	}

	/*
	 * Checks if the default connection has the provided column for the given table.
	 *
	 * @param tableName  the table to use.
	 * @param columnName the column to check if exists.
	 * @return true if the column exists, false otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public boolean hasColumn(String tableName, String columnName) throws SQLException {
		return getMetaData().getColumns(null, null, tableName, columnName).next();
	}

	/*
	 * Create a new table using the DatabaseClosure and Table classes.
	 *
	 * @param tableName the name of table that should be created.
	 * @param closure   the database closure that creates the table.
	 * @return true if the table was created successfully, false otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public boolean create(String tableName, DatabaseClosure closure) throws SQLException {
		Table table = createAndRunTable(tableName, closure);

		Map<String, Boolean> options = new HashMap<>();
		options.put("ignoreExistingTable", true);

		String query = dbm.getDatabase().create(table, options);
		Statement stmt = dbm.getDatabase().prepare(query);

		if(dbm.debugMode)
			dbm.logger.debug("Schema create was called with: " + query);

		if(stmt instanceof PreparedStatement) {
			return !((PreparedStatement) stmt).execute();
		}

		return !stmt.execute(query);
	}

	/*
	 * Create a new table if it doesn't exists using the DatabaseClosure and Table classes.
	 *
	 * @param tableName the name of table that should be created.
	 * @param closure   the database closure that creates the table.
	 * @return true if the table was created successfully, false otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public boolean createIfNotExists(String tableName, DatabaseClosure closure) throws SQLException {
		if(hasTable(tableName)) {
			return false;
		}

		Table table = createAndRunTable(tableName, closure);

		Map<String, Boolean> options = new HashMap<>();
		options.put("ignoreExistingTable", false);

		String query = dbm.getDatabase().create(table, options);
		Statement stmt = dbm.getDatabase().prepare(query);

		if(dbm.debugMode)
			dbm.logger.debug("Schema create was called with: " + query);

		if(stmt instanceof PreparedStatement) {
			return !((PreparedStatement) stmt).execute();
		}

		return !stmt.execute(query);
	}

	/*
	 * Create and runs table for the provided closure for the given table.
	 *
	 * @param tableName the name of table should be created for.
	 * @param closure   the closure that should run the table.
	 * @return the table that was created.
	 */
	private Table createAndRunTable(String tableName, DatabaseClosure closure) {
		Table table = new Table(tableName);

		closure.run(table);

		return table;
	}

	/*
	 * Drops the provided table, if the table doesn't exists an 
	 * exception will be thrown.
	 *
	 * @param tableName the table that should be dropped.
	 * @return true if the table was dropped successfully, false otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public boolean drop(String tableName) throws SQLException {
		if(dbm.debugMode)
			dbm.logger.debug("Schema drop was called for table: " + tableName);

		return alterQuery(String.format("DROP TABLE `%s`;", tableName));
	}

	/*
	 * Drops the provided table if it exists.
	 *
	 * @param tableName the table that should be dropped.
	 * @return true if the table was dropped successfully, false otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public boolean dropIfExists(String tableName) throws SQLException {
		if(dbm.debugMode)
			dbm.logger.debug("Schema dropIfExists was called for table: " + tableName);

		return alterQuery(String.format("DROP TABLE IF EXISTS `%s`;", tableName));
	}

	/*
	 * Renames the provided table to hte new name using the database
	 * prefix, if the table doesn't exists an exception will be thrown.
	 *
	 * @param from the tables current name.
	 * @param to   the name the table should be ranamed to.
	 * @return true if the table was ranamed successfully, false otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public boolean rename(String from, String to) throws SQLException {
		if(dbm.debugMode)
			dbm.logger.debug("Schema rename was called with: [ " + from + ", " + to + " ]");

		return alterQuery(String.format("ALERT TABLE `%s` RENAME `%s`;", from, to));
	}

	/*
	 * Renames the provided table to the new name if it exists, if the
	 * table doesn't exist nothing will happen.
	 *
	 * @param from the tables current name.
	 * @param to   the name the table should be renamed to.
	 * @return true if the table was renamed successfully, false otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public boolean renameIfExists(String from, String to) throws SQLException {
		if(!hasTable(from)) {
			return false;
		}

		return rename(from, to);
	}

	/*
	 * Create alter query statement for the default database connection, and then executes
	 * the provided query using the Statement#execute(String) method.
	 *
	 * @param query the query that should be executed.
	 * @return TRUE if the query affected any rows/tables/columns successfully, FALSE otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public boolean alterQuery(String query) throws SQLException {
		if(dbm.debugMode)
			dbm.logger.debug("alterQuery(String query) was called with the following SQL query.\nSQL: " + query);

		Statement stmt = dbm.getDatabase().getConnection().createStatement();

		return !stmt.execute(query);
	}

	/*
	 * Gets the database meta data object from the default database connection.
	 *
	 * @return the database meta data object.
	 * @throws SQLException.
	 */
	private DatabaseMetaData getMetaData() throws SQLException {
		return dbm.getDatabase().getConnection().getMetaData();
	}
}
