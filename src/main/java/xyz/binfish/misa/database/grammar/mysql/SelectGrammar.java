package xyz.binfish.misa.database.grammar.mysql;

import java.util.List;

import xyz.binfish.misa.database.grammar.QueryGrammar;
import xyz.binfish.misa.database.query.QueryBuilder;
import xyz.binfish.misa.database.query.QueryOrder;
import xyz.binfish.misa.database.query.Clause;
import xyz.binfish.misa.database.query.JoinClause;
import xyz.binfish.misa.database.query.OperatorType;

public class SelectGrammar extends QueryGrammar {

	@Override
	public String format(QueryBuilder builder) {
		query = "SELECT ";

		buildColumns(builder);

		buildJoins(builder);

		buildWhereClause(builder);

		return finalize(builder);
	}

	private void buildColumns(QueryBuilder builder) {
		if(builder.getColumns().isEmpty() || builder.getColumns().size() == 1 && builder.getColumns().get(0).equals("*")) {
			query += String.format("* FROM %s ", formatField(builder.getTable()));
			return;
		}

		builder.getColumns().stream().forEach((column) -> {
			if(column.startsWith("RAW:")) {
				query += String.format("%s, ", column.substring(4));
				return;
			}

			query += formatField(column) + ", ";
		});

		removeLast(2);

		query += String.format(" FROM %s ", formatField(builder.getTable()));
	}

	private void buildJoins(QueryBuilder builder) {
		List<JoinClause> joins = builder.getJoins();

		for(JoinClause join : joins) {
			if(join.clauses.isEmpty()) {
				continue;
			}

			addPart(String.format(" %s JOIN %s ON ", join.type.toUpperCase(), formatField(join.table)));

			int orderLength = 0;

			for(Clause clause : join.clauses) {
				String string = String.format(" %s %s %s",
						formatField(clause.getOne()), clause.getIdentifier(), formatField((String) clause.getTwo())
				);

				if(clause.getOrder() == null) {
					clause.setOrder(OperatorType.AND);
				}

				String operator = clause.getOrder().getOperator();

				orderLength = operator.length() + 2;
				addPart(String.format(string + " %s ", operator));
			}

			if(orderLength > 0) {
				removeLast(orderLength);
			}
		}
	}

	@Override
	protected String finalize(QueryBuilder builder) {
		if(!builder.getOrder().isEmpty()) {
			addPart(" ORDER BY ");

			for(QueryOrder order : builder.getOrder()) {
				if(order.getField() == null) {
					continue;
				}

				if(order.isRawSQL()) {
					addPart(" %s, ", order.getField());
					continue;
				}

				if(order.getType() != null) {
					addPart(" %s %s, ", formatField(order.getField()), order.getType().toUpperCase());
				}
			}

			removeLast(2);
		}

		if(builder.getTake() <= 0) {
			return query.trim() + ";";
		}

		if(builder.getTake() > 0) {
			addPart(String.format(" LIMIT %d", builder.getTake()));

			if(builder.getSkip() > 0) {
				addPart(String.format(" OFFSET %d", builder.getSkip()));
			}
		}

		addPart(";");

		return query;
	}
}
