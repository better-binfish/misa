package xyz.binfish.misa.database.connections;

import xyz.binfish.misa.database.StatementInterface;

public enum SQLiteStatement implements StatementInterface {

	SELECT("SELECT"),
	INSERT("INSERT"),
	UPDATE("UPDATE"),
	DELETE("DELETE"),
	REPLACE("REPLACE"),
	CREATE("CREATE"),
	ALTER("ALTER"),
	DROP("DROP"),
	ANALYZE("ANALYZE"),
	ATTACH("ATTACH"),
	BEGIN("BEGIN"),
	DETACH("DETACH"),
	END("END"),
	EXPLAIN("EXPLAIN"),
	INDEXED("INDEXED"),
	PRAGMA("PRAGMA"),
	REINDEX("REINDEX"),
	RELEASE("RELEASE"),
	SAVEPOINT("SAVEPOINT"),
	VACUUM("VACUUM"),
	LINE_COMMENT("--"),
	BLOCK_COMMENT("/*");

	private final String name;

	SQLiteStatement(String str) {
		this.name = str;
	}

	@Override
	public String toString() {
		return name;
	}
}
