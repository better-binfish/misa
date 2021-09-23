package xyz.binfish.misa.database.schema.migrations;

import java.sql.SQLException;

import xyz.binfish.misa.Constants;
import xyz.binfish.misa.Configuration;
import xyz.binfish.misa.locale.LanguageManager;
import xyz.binfish.misa.database.schema.Migration;
import xyz.binfish.misa.database.schema.Schema;

public class CreateGuildTableMigration implements Migration {

	@Override
	public boolean up(Schema schema) throws SQLException {
		return schema.createIfNotExists(Constants.GUILD_TABLE_NAME, table -> {
			table.Long("id").unsigned();
			table.Long("owner_id").unsigned();
			table.String("name", 32);
			table.String("icon", 32).nullable();
			table.String("locale", 4).defaultValue(
				LanguageManager.getDefaultLocale().getCode()
			);
			table.String("prefix", 2).defaultValue(
				Configuration.getInstance().get("defaultPrefix", null)
			);
			table.Long("log_channel_id").unsigned().nullable();
			table.Long("auto_role_id").unsigned().nullable();
			table.Boolean("is_premium").defaultValue(false);
			table.Boolean("is_banned").defaultValue(false);
			table.Timestamp();
		});
	}

	@Override
	public boolean down(Schema schema) throws SQLException {
		return schema.dropIfExists(Constants.GUILD_TABLE_NAME);
	}
}
