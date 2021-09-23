package xyz.binfish.misa.database.schema.migrations;

import java.sql.SQLException;

import xyz.binfish.misa.Constants;
import xyz.binfish.misa.database.schema.Migration;
import xyz.binfish.misa.database.schema.Schema;

public class CreateFeedbackTableMigration implements Migration {
	
	@Override
	public boolean up(Schema schema) throws SQLException {
		return schema.createIfNotExists(Constants.FEEDBACK_TABLE_NAME, table -> {
			table.Increments("id");
			table.Long("user_id").unsigned();
			table.Long("channel_id").unsigned().nullable();
			table.Text("message");
			table.Timestamp();
		});
	}

	@Override
	public boolean down(Schema schema) throws SQLException {
		return schema.dropIfExists(Constants.FEEDBACK_TABLE_NAME);
	}
}
