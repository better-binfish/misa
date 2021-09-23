package xyz.binfish.misa.database.controllers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.SQLException;

import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.database.DatabaseManager;
import xyz.binfish.misa.Misa;
import xyz.binfish.misa.Constants;
import xyz.binfish.logger.Logger;

public class GuildController {

	private static DatabaseManager dbm = Misa.getDatabaseManager();
	private static Logger logger = Logger.getLogger();

	public static GuildModel fetchGuild(Guild guild) {
		try {
			GuildModel guildModel = new GuildModel(dbm.newQueryBuilder(Constants.GUILD_TABLE_NAME)
					.selectAll()
					.where("id", guild.getId())
					.get().first());

			if(!guildModel.hasData()) {
				try {
					dbm.newQueryBuilder(Constants.GUILD_TABLE_NAME)
						.insert(statement -> {
							statement
								.set("id", guild.getIdLong())
								.set("owner_id", guild.getOwnerIdLong())
								.set("name", guild.getName(), true);

							if(guild.getIconId() != null) {
								statement.set("icon", guild.getIconId());
							}
						});
				} catch(Exception ex) {
					dbm.logger.error(ex.getMessage());
				}

				return new GuildModel(guild);
			}

			return guildModel;
		} catch(SQLException e) {
			dbm.logger.error("Failed to fetch guild model from the database.", e);

			return null;
		}
	}

	public static GuildModel fetchGuild(Message message) {
		if(message.isFromGuild()) {
			return fetchGuild(message.getGuild());
		}

		return null;
	}
}
