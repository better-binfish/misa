package xyz.binfish.misa.database;

import xyz.binfish.misa.database.Database;
import xyz.binfish.misa.database.DatabaseManager;

public abstract class HostnameDatabase extends Database {

	/*
	 * The database hostname that should be
	 * used to connect to the database.
	 */
	private String hostname;

	/*
	 * The port that database is listing on.
	 */
	private int port;

	/*
	 * The name of the database that should be used.
	 */
	private String databaseName;

	/*
	 * The login username used to connect to the database.
	 */
	private String username;

	/*
	 * The login password used to connect to the database.
	 */
	private String password;

	/*
	 * Creates a new host name database instance.
	 *
	 * @param dbm      the database manager class instance.
	 * @param hostname the host name to connect to.
	 * @param port     the port the database is listing on.
	 * @param database the database name to use.
	 * @param username the login username.
	 * @param password the login password.
	 */
	public HostnameDatabase(DatabaseManager dbm, String hostname, int port,
			String databaseName, String username, String password) {
		super(dbm);

		this.hostname = hostname;
		this.port = port;
		this.databaseName = databaseName;
		this.username = username;
		this.password = password;

		if(hostname.contains(":")) {
			String[] parts = hostname.split(":");

			if(parts.length == 2) {
				setHostname(parts[0]);
				setPort(Integer.parseInt(parts[1]));
			}
		}
	}

	/*
	 * Returns the host name the database is listing on.
	 *
	 * @return the database host name.
	 */
	public String getHostname() {
		return hostname;
	}

	/*
	 * Sets the host name the database is listing on.
	 *
	 * @param hostname the database host name.
	 */
	public void setHostname(String hostname) {
	}

	/*
	 * Returns the port the database is listing on.
	 *
	 * @return the database port.
	 */
	public int getPort() {
		return port;
	}

	/*
	 * Sets the port the database should be listing on,
	 * the port has to be between 0 and 65535.
	 *
	 * @param port the database port.
	 */
	public void setPort(int port) {
		if((port < 0) || (65535 < port)) {
			throw new RuntimeException("Port number cannot be below 0 or greater than 65535.");
		}

		this.port = port;
	}

	/*
	 * Gets the name of the database that should be used.
	 *
	 * @return the database name.
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/*
	 * Sets the database name that should be used.
	 *
	 * @param databaseName the database name.
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/*
	 * Returns the database username.
	 *
	 * @return the database username.
	 */
	public String getUsername() {
		return username;
	}

	/*
	 * Sets the database username.
	 *
	 * @param username the database username.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 * Returns the database password.
	 *
	 * @return the database password.
	 */
	public String getPassword() {
		return password;
	}

	/*
	 * Sets the database password.
	 *
	 * @param password the database password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
