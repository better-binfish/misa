package xyz.binfish.misa.database.connections;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import java.util.Map;

import xyz.binfish.misa.database.HostnameDatabase;
import xyz.binfish.misa.database.DatabaseManager;
import xyz.binfish.misa.database.StatementInterface;
import xyz.binfish.misa.database.grammar.mysql.*;
import xyz.binfish.misa.database.schema.Table;
import xyz.binfish.misa.database.query.QueryBuilder;
import xyz.binfish.misa.Configuration;
import xyz.binfish.misa.exceptions.DatabaseException;

public class MySQLConnection extends HostnameDatabase {

	/*
	 * Create a MySQL database connection instance with the parsed
	 * information, the port used will default to 3366.
	 *
	 * @param dbm the database manager class instance.
	 */
	public MySQLConnection(DatabaseManager dbm) {
		this(dbm,
			 Configuration.getInstance().get("databaseHostname", null),
			 3366,
			 Configuration.getInstance().get("databaseName", null),
			 Configuration.getInstance().get("databaseUsername", null),
			 Configuration.getInstance().get("databasePassword", null)
		);
	}

	/*
	 * Create a MySQL database connection instance with the parsed information.
	 *
	 * @param dbm      the database manager class instance.
	 * @param hostname the hostname of the MySQL database.
	 * @param port     the port the connection should be opened on.
	 * @param database the name of the database.
	 * @param username the username for the user that should be used for the connection.
	 * @param password the password for the given username.
	 */
	public MySQLConnection(DatabaseManager dbm, String hostname,
			int port, String database, String username, String password) {
		super(dbm, hostname, port, database, username, password);
	}

	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			return true;
		} catch(ClassNotFoundException e) {
			throw new DatabaseException("MySQL Driver class missing.", e);
		}
	}

	@Override
	public boolean open() throws SQLException {
		if(initialize()) {
			try {
				connection = DriverManager.getConnection("jdbc:mysql://"
						+ getHostname() + ":" + getPort() + "/" + getDatabaseName()
						, getUsername(), getPassword());

				return true;
			} catch(SQLException e) {
				String reason = "Could not establish a MySQL connection, SQLException: "
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
			return MySQLStatement.valueOf(statement[0].toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new SQLException(String.format("Unknown statement: \"%s\".", statement[0]), e);
		}
	}

	@Override
	protected void queryValidation(StatementInterface statement) throws SQLException {
		SQLException exception;

		switch((MySQLStatement) statement) {
			case USE:
				exception = new SQLException("Please create a new connection to use a different database.");

				dbm.logger.error("Please create a new connection to use a different database.", exception);
				throw exception;
			case PREPARE:
			case EXECUTE:
			case DEALLOCATE:
				exception = new SQLException("Please use the prepare() method to prepare a query.");

				dbm.logger.error("Please use the prepare() method to prepare a query.", exception);
				throw exception;
		}
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
