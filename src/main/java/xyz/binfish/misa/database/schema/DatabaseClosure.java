package xyz.binfish.misa.database.schema;

public interface DatabaseClosure {

	/*
	 * Runs the database closure using the given table.
	 *
	 * @param table the migration/schema table that should be built.
	 */
	void run(Table table);
}
