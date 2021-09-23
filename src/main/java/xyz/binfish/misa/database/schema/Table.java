package xyz.binfish.misa.database.schema;

import java.util.LinkedHashMap;
import java.util.Map;

public class Table {

	private final String name;
	private final LinkedHashMap<String, Field> fields = new LinkedHashMap<>();

	/*
	 * Creates a new table instance for the provided table.
	 *
	 * @param name the table name.
	 */
	public Table(String name) {
		this.name = name;
	}


	/*
	 * Gets the name of the table.
	 *
	 * @return the name of the table.
	 */
	public String getName() {
		return name;
	}

	/*
	 * Gets the fields created by the table.
	 *
	 * @return the fields created by the table.
	 */
	public Map<String, Field> getFields() {
		return fields;
	}

	/*
	 * Creates a column of the given name.
	 *
	 * @param field the name of the column that should be created.
	 */
	public void Increments(String field) {
		makeField(field, FieldType.INTEGER).autoIncrement();
	}

	/*
	 * Creates a column of the given name.
	 *
	 * @param field the name of the column that should be created.
	 */
	public void BigIncrements(String field) {
		makeField(field, FieldType.LONG).autoIncrement();
	}

	/*
	 * Creates a Integer whit the provided name and a length 17.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field Integer(String field) {
		return Integer(field, 17);
	}

	/*
	 * Create a FieldType#INTEGER column with the provided name and length.
	 *
	 * @param field  the name of the column that should be created.
	 * @param length the length of the column.
	 * @return the field instance.
	 */
	public Field Integer(String field, int length) {
		return makeField(field, FieldType.INTEGER, length);
	}

	/*
	 * Create a FieldType#LONG column with the provided name.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field Long(String field) {
		return makeField(field, FieldType.LONG);
	}

	/*
	 * Create a FieldType#LONG column with the provided name and length.
	 *
	 * @param field  the name of the column that should be created.
	 * @param length the length of the column.
	 * @return the field instance.
	 */
	public Field Long(String field, int length) {
		return makeField(field, FieldType.LONG, length);
	}

	/*
	 * Creates a Decimal column with the provided name, and a length of 17.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field Decimal(String field) {
		return Decimal(field, 17);
	}

	/*
	 * Creates a FieldType#DECIMAL column with the provided name and a length.
	 *
	 * @param field  the name of the column that should be created.
	 * @param length the length of the column.
	 * @return the field instance.
	 */
	public Field Decimal(String field, int length) {
		return makeField(field, FieldType.DECIMAL, length);
	}

	/*
	 * Creates a Double column with the provided name, and a length of 15.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field Double(String field) {
		return Double(field, 15);
	}

	/*
	 * Creates a FieldType#DOUBLE with the provided name and a length.
	 *
	 * @param field  the name of the column that should be created.
	 * @param length the length if the column.
	 * @return the field instance.
	 */
	public Field Double(String field, int length) {
		return makeField(field, FieldType.DOUBLE, length);
	}

	/*
	 * Creates a Float column with the provided name, and a length of 17.
	 *
	 * @param field tha name of the column that should be created.
	 * @return the field instance.
	 */
	public Field Float(String field) {
		return Float(field, 17);
	}

	/*
	 * Creates a FieldType#FLOAT column with the provided name and a length.
	 *
	 * @param field  the name of the column that should be created.
	 * @param length the length if the column.
	 * @return the field instance.
	 */
	public Field Float(String field, int length) {
		return makeField(field, FieldType.FLOAT, length);
	}

	/*
	 * Creates a FieldType#BOOLEAN column with the provided name.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field Boolean(String field) {
		return makeField(field, FieldType.BOOLEAN);
	}

	/*
	 * Creates a FieldType#DATE column with the provided name.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field Date(String field) {
		return makeField(field, FieldType.DATE);
	}

	/*
	 * Creates a FieldType#DATETIME column with the provided name.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field DateTime(String field) {
		return makeField(field, FieldType.DATETIME);
	}

	/*
	 * Creates a String column with the provided name, and a length of 256.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field String(String field) {
		return String(field, 256);
	}

	/*
	 * Creates a FieldType#STRING column with the provided name and length.
	 *
	 * @param field  the name of the column that should be created.
	 * @param length the length if the column.
	 * @return the field instance.
	 */
	public Field String(String field, int length) {
		return makeField(field, FieldType.STRING, length);
	}

	/*
	 * Creates a FieldType#LONGTEXT column with the provided name.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field LongText(String field) {
		return makeField(field, FieldType.LONGTEXT);
	}

	/*
	 * Creates a FieldType#MEDIUMTEXT column with the provided name.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field MediumText(String field) {
		return makeField(field, FieldType.MEDIUMTEXT);
	}

	/*
	 * Creates a FieldType#SMALLTEXT column with the provided name.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field SmallText(String field) {
		return makeField(field, FieldType.SMALLTEXT);
	}

	/*
	 * Creates a FieldType#TEXT column with the provided name.
	 *
	 * @param field the name of the column that should be created.
	 * @return the field instance.
	 */
	public Field Text(String field) {
		return makeField(field, FieldType.TEXT);
	}

	/*
	 * Creates timestamp called created_at that gets used when a row is first created.
	 */
	public void Timestamp() {
		Timestamp("created_at", new DefaultSQLAction("CURRENT_TIMESTAMP"));
	}

	/*
	 * Creates timestamp called updated_at which will update every time any of
	 * the rows columns are updated/modified.
	 */
	public void TimestampOnUpdate() {
		Timestamp("updated_at", new DefaultSQLAction("CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"));
	}

	/*
	 * Creates a FieldType#DATETIME column with the provided name, and sql action.
	 *
	 * @param field  the name of the column that should be created.
	 * @param action the sql action.
	 * @return the field instance.
	 */
	public void Timestamp(String field, DefaultSQLAction action) {
		makeField(field, FieldType.DATETIME)
			.defaultValue(action);
	}

	/*
	 * Creates a new field object of the given type.
	 *
	 * @param field the name of the colum that should be created.
	 * @param type  the type of field that should be created.
	 * @return the field instance.
	 */
	private Field makeField(String field, FieldType type) {
		Field obj = new Field(type);

		fields.put(field, obj);

		return obj;
	}

	/*
	 * Creates a new field object of the given type and length.
	 *
	 * @param field  the name of the column that should be created.
	 * @param type   the type of field that should be created.
	 * @param length the length of the field that should be created.
	 * @return the field instance.
	 *
	 */
	private Field makeField(String field, FieldType type, int length) {
		Field obj = new Field(type, length);

		fields.put(field, obj);

		return obj;
	}
}
