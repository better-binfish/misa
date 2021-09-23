package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.sql.SQLException;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.util.MessageType;
import xyz.binfish.misa.Misa;
import xyz.binfish.misa.Constants;
import xyz.binfish.logger.Logger;

public class PrefixCommand extends Command {

	public PrefixCommand() {
		super();
		this.name = "prefix";
		this.aliases = null;
		this.usage = "prefix [prefix]";
		this.guildOnly = true;
		this.permissions = new Permission[]{
			Permission.ADMINISTRATOR
		};
	}

	@Override
	public String getDescription() {
		return "Change the command prefix for the server.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		GuildModel guildModel = GuildController.fetchGuild(message);
		if(guildModel == null) {
			return this.sendErrorMessage(message,
					"data.errors.errorOccurredWhileLoading", "server settings");
		}

		if(args.length < 1) {
			EmbedBuilder embed = new EmbedBuilder()
				.setColor(MessageType.INFO.getColor())
				.setDescription(this.getString("embed.description")
						.replace(":prefix", guildModel.getPrefix())
			);

			channel.sendMessage(embed.build()).queue();

			return true;
		}

		String prefix = args[0];
		if(prefix.contains(" ") || prefix.length() < 1 || prefix.length() > 4) {
			return this.sendErrorMessage(message,
					String.format("data.commands.%s.invalidPrefix", this.getClassName(), prefix));
		}

		try {
			Misa.getDatabaseManager().newQueryBuilder(Constants.GUILD_TABLE_NAME)
				.where("id", message.getGuild().getId())
				.update(statement -> {
					statement.set("prefix", prefix);
				});

			this.sendSuccess(message, this.getString("changed")
					.replace(":prefix", prefix));
			return true;
		} catch(SQLException e) {
			Logger.getLogger().error(
					String.format("Failed to update the prefix for a server(%s), error: ",
						message.getGuild().getId()) + e.getMessage());

			return this.sendErrorMessage(message,
					"Failed to update the servers prefix settings, please try "
					+ "again, if this problem persists, please contact one of the bot developers about it.");
		}
	}
}
