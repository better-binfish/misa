package xyz.binfish.misa.database.query;

@FunctionalInterface
public interface ChangeableClosure {

	/*
	 * Run the changeable closure function, setting up all the columns.
	 *
	 * @param statement the changeable statement used to update the records in the query builder.
	 */
	void run(ChangeableStatement statement);
}
