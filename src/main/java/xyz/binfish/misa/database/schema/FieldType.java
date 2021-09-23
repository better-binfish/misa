package xyz.binfish.misa.database.schema;

public enum FieldType {

	INTEGER("INT", 1, false),
	DECIMAL("DECIMAL", 2, true),
	DOUBLE("DOUBLE", 1, true),
	FLOAT("FLOAT", 1, true),
	LONG("BIGINT", 1, false),
	BOOLEAN("BOOLEAN", 0, false),
	DATE("DATE", 0, false),
	DATETIME("DATETIME", 0, false),
	STRING("VARCHAR", 1, true),
	LONGTEXT("LONGTEXT", 0, false),
	MEDIUMTEXT("MEDIUMTEXT", 0, false),
	SMALLTEXT("TINYTEXT", 0, false),
	TEXT("TEXT", 0, false);

	private final String name;
	private final int argumentAmount;
	private boolean requiredArguments;

	private FieldType(String name, int argumentAmount, boolean requiredArguments) {
		this.name = name;
		this.argumentAmount = argumentAmount;
		this.requiredArguments = requiredArguments;
	}

	public String getName() {
		return name;
	}

	public boolean hasArguments() {
		return argumentAmount > 0;
	}

	public int getArgumentsAmount() {
		return argumentAmount;
	}

	public boolean requiredArguments() {
		return requiredArguments;
	}
}
