package xyz.binfish.misa.database.connections;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.Map;

import xyz.binfish.misa.database.FileDatabase;
import xyz.binfish.misa.database.DatabaseManager;
import xyz.binfish.misa.database.StatementInterface;
import xyz.binfish.misa.database.grammar.sqlite.*;
import xyz.binfish.misa.database.schema.Table;
import xyz.binfish.misa.database.query.QueryBuilder;
import xyz.binfish.misa.exceptions.DatabaseException;

import xyz.binfish.misa.Configuration;

public class SQLiteConnection extends FileDatabase {

	/*
	 * Create a SQLite database connection instance.
	 *
	 * @param dbm the database manager class instance.
	 */
	public SQLiteConnection(DatabaseManager dbm) {
		this(dbm, Configuration.getInstance().get("databaseFilename", null));
	}

	/*
	 * Create a SQLite database connection instance.
	 *
	 * @param dbm      the database manager class instance.
	 * @param filename the filename of the database.
	 */
	public SQLiteConnection(DatabaseManager dbm, String filename) {
		super(dbm);

		if(filename.equals(":memory:")) {
			this.setFilename(null);
			return;
		}

		String[] parts = filename.split("\\.");
		String extension = parts[parts.length - 1];

		filename = String.join(".", Arrays.copyOf(parts, parts.length - 1));

		this.setFile(".", filename, extension);
	}

	@Override
	protected boolean initialize() {
		try {
			Class.forName("org.sqlite.JDBC");

			return true;
		} catch(ClassNotFoundException e) {
			throw new DatabaseException("SQLite JDBC class not found while initializing.", e);
		}
	}

	@Override
	public boolean open() throws SQLException {
		if(initialize()) {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:"
						+ (getFile() == null ? ":memory:" : getFile().getAbsolutePath()));

				return true;
			} catch(SQLException e) {
				String reason = "Could not establish an sqlite connection, SQLException: "
					+ e.getMessage();

				dbm.logger.error(reason);
				throw new SQLException(reason);
			}
		}

		return false;
	}

	@Override
	public StatementInterface getStatement(String query) throws SQLException {
		String[] statement = query.trim().split(" ", 2);

		try {
			return SQLiteStatement.valueOf(statement[0].toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new SQLException(String.format("Unknown statement: \"%s\".", statement[0]), e);
		}
	}

	@Override
	protected void queryValidation(StatementInterface paramInterface) throws SQLException {
		// This does nothing for SQLite.	
	}

	@Override
	public boolean hasTable(String tableName) {
		try {
			DatabaseMetaData dmd = getConnection().getMetaData();

			try(ResultSet tables = dmd.getTables(null, null, tableName,
						new String[]{ "TABLE" })) {
				if(tables.next()) {
					tables.close();

					return true;
				}
			}

			return false;
		} catch(SQLException e) {
			dbm.logger.error(
					String.format("Failed to check if table exists \"%s\" : %s", tableName, e.getMessage()));
			return false;
		}
	}

	@Override
	public boolean truncate(String tableName) {
		try {
			if(!hasTable(tableName)) {
				return false;
			}

			try(Statement stmt = getConnection().createStatement()) {
				stmt.executeUpdate(String.format("DELETE FROM `%s`;", tableName));
			}

			return true;
		} catch(SQLException e) {
			dbm.logger.error(String.format("Failed to truncate \"%s\" : %s", tableName, e.getMessage()));
		}

		return false;
	}

	@Override
	public String insert(QueryBuilder query) {
		return setupAndRun(new InsertGrammar(), query);
	}

	@Override
	public String select(QueryBuilder query) {
		return setupAndRun(new SelectGrammar(), query);	
	}

	@Override
	public String update(QueryBuilder query) {
		return setupAndRun(new UpdateGrammar(), query);
	}

	@Override
	public String delete(QueryBuilder query) {
		return setupAndRun(new DeleteGrammar(), query);
	}

	@Override
	public String create(Table table, Map<String, Boolean> options) {
		return setupAndRun(new CreateGrammar(), table, options);
	}
}
