package xyz.binfish.misa.database.grammar.mysql;

import java.util.Map;

import xyz.binfish.misa.database.grammar.AlterGrammar;

import xyz.binfish.misa.database.schema.Table;
import xyz.binfish.misa.database.schema.Field;
import xyz.binfish.misa.database.schema.FieldType;

public class CreateGrammar extends AlterGrammar {

	@Override
	public String format(Table table, Map<String, Boolean> options) {
		query = "CREATE TABLE ";

		buildTable(table, options);

		buildFields(table);

		return finalize(table);
	}

	@Override
	protected String finalize(Table table) {
		addPart(");");
		return query;
	}

	private void buildTable(Table table, Map<String, Boolean> options) {
		if(!options.getOrDefault("ignoreExistingTable", Boolean.FALSE)) {
			addPart(" IF NOT EXISTS ");
		}

		addPart(" %s (", formatField(table.getName()));
	}

	private void buildFields(Table table) {
		String fields = "";
		String primary = "";

		for(String name : table.getFields().keySet()) {
			Field field = table.getFields().get(name);
			FieldType type = field.getType();

			String line = String.format("%s %s", formatField(name), type.getName());

			if(type.requiredArguments()) {
				if(type.getArgumentsAmount() == 2) {
					line += String.format("(%s, %s)", field.getLength(), field.getTail());
				} else {
					line += String.format("(%s)", field.getLength());
				}
			}

			if(field.isUnsigned()) {
				line += " UNSIGNED";
			}

			String nullable = field.isNullable() ? " NULL" : " NOT NULL";
			String defaultString = " ";

			if(field.getDefaultValue() != null) {
				defaultString = " DEFAULT ";

				if(field.getDefaultValue().toUpperCase().equals("NULL")) {
					defaultString += "NULL";
				} else if(field.isDefaultSQLAction()) {
					defaultString += field.getDefaultValue();
				} else {
					defaultString += String.format("'%s'", field.getDefaultValue().replace("'", "\'"));
				}

				line += defaultString;
			}

			if(field.isAutoIncrement()) {
				primary += formatField(name) + ", ";
				line += " AUTO_INCREMENT";
			}

			fields += line + ", ";
		}

		if(primary.length() > 0) {
			fields += String.format("PRIMARY KEY (%s), ", primary.substring(0, primary.length() - 2));
		}

		addPart(fields.substring(0, fields.length() - 2));
	}
}
