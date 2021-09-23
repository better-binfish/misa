package xyz.binfish.misa.database.grammar.mysql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.binfish.misa.database.grammar.QueryGrammar;
import xyz.binfish.misa.database.query.QueryBuilder;

public class InsertGrammar extends QueryGrammar {
	
	/*
	 * List of key that should be added to the query.
	 */
	protected final List<String> keyset = new ArrayList<>();

	@Override
	public String format(QueryBuilder builder) {
		query = "INSERT INTO ";

		addPart(String.format(" %s", formatField(builder.getTable())));

		buildKeyset(builder);

		buildValues(builder);

		return finalize(builder);
	}

	@Override
	protected String finalize(QueryBuilder builder) {
		addPart(";");

		return query;
	}

	private void buildKeyset(QueryBuilder builder) {
		List<Map<String, Object>> items = builder.getItems();

		items.stream().forEach((map) -> {
			map.keySet().stream().filter((key) -> (!keyset.contains(key))).forEach((key) -> {
				keyset.add(key);
			});
		});

		addPart(" (");

		keyset.stream().forEach((key) -> {
			addPart(String.format("`%s`, ", key));
		});

		removeLast(2).addPart(")");
	}

	private void buildValues(QueryBuilder builder) {
		addPart(" VALUES ");

		List<Map<String, Object>> items = builder.getItems();

		for(Map<String, Object> row : items) {
			addPart(" (");

			for(String key : keyset) {
				if(!row.containsKey(key)) {
					addPart("NULL, ");
					continue;
				}

				if(row.get(key) == null) {
					addPart("NULL, ");
					continue;
				}

				String value = row.get(key).toString();

				if(value.startsWith("RAW:")) {
					addPart("%s, ", value.substring(4));
					continue;
				}

				if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
					addPart(String.format("%s, ", value.equalsIgnoreCase("true") ? 1 : 0));
					continue;
				}

				if(isNumeric(value)) {
					addPart(String.format("%s, ", value));
					continue;
				}

				addPart("?, ");
			}

			removeLast(2).addPart("),");
		}

		removeLast(1);
	}
}
