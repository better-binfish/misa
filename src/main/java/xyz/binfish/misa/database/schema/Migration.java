package xyz.binfish.misa.database.schema;

import java.sql.SQLException;

public interface Migration {

	/*
	 * Attempts to migrate the database.
	 *
	 * @param schema the database schematic instance.
	 * @return the result of the schematic instance call.
	 * @throws SQLException if a database access error occurs.
	 */
	boolean up(Schema schema) throws SQLException;

	/*
	 * Attempts to rollback the mgirations from the database.
	 *
	 * @param schema the database schematic instance.
	 * @return the result of the schematic instance call.
	 * @throws SQLException if a database access error call.
	 */
	boolean down(Schema schema) throws SQLException;
}
