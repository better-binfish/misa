package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.Permission;

import java.sql.SQLException;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.util.ComparatorUtil;
import xyz.binfish.misa.util.MentionableUtil;
import xyz.binfish.misa.Misa;
import xyz.binfish.misa.Constants;
import xyz.binfish.logger.Logger;

public class LogChannelCommand extends Command {

	public LogChannelCommand() {
		super();
		this.name = "setlog";
		this.aliases = null;
		this.usage = "setlog (channel_id | #channel) | disable";
		this.guildOnly = true;
		this.permissions = new Permission[]{
			Permission.ADMINISTRATOR
		};
	}

	@Override
	public String getDescription() {
		return "Set mention a text channel to logging in the mentioned channel.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		GuildModel guildModel = GuildController.fetchGuild(message);
		if(guildModel == null) {
			return this.sendError(message,
					"data.errors.errorOccurredWhileLoading", "server settings");
		}

		if(args.length < 1) {
			return this.sendError(message,
					"data.errors.missingArgument", "<channel_id | #channel> | disable");
		}

		if(ComparatorUtil.isFuzzyFalse(args[0])) {
			return disableLogChannel(message);
		}

		GuildChannel gChannel = MentionableUtil.getChannel(message, args);
		if(gChannel == null || !(gChannel instanceof TextChannel)) {
			return this.sendError(message,
					"data.errors.noChannelsWithNameOrId", args[0]);
		}

		return updateLogChannel(message, gChannel);
	}

	private boolean disableLogChannel(Message message) {
		try {
			Misa.getDatabaseManager().newQueryBuilder(Constants.GUILD_TABLE_NAME)
				.where("id", message.getGuild().getId())
				.update(statement -> statement.set("log_channel_id", null));

			this.sendSuccess(message, this.getString("changed")
					.replace(":name", "null"));
			return true;
		} catch(SQLException e) {
			Logger.getLogger().error(
					String.format("Failed to disable the log channel for a server(%s), error: ",
						message.getGuild().getId()) + e.getMessage());

			return this.sendError(message,
					"Failed to disable the servers log channel settings, please try "
					+ "again, if this problem persists, please contact one of the bot developers about it.");
		}
	}

	private boolean updateLogChannel(Message message, GuildChannel gChannel) {
		try {
			Misa.getDatabaseManager().newQueryBuilder(Constants.GUILD_TABLE_NAME)
				.where("id", message.getGuild().getId())
				.update(statement -> statement.set("log_channel_id", gChannel.getIdLong()));

			this.sendSuccess(message, this.getString("changed")
					.replace(":name", gChannel.getId()));
			return true;
		} catch(SQLException e) {
			Logger.getLogger().error(
					String.format("Failed to update the log channel for a server(%s), error: ",
						message.getGuild().getId()) + e.getMessage());

			return this.sendError(message,
					"Failed to update the servers log channel settings, please try "
					+ "again, if this problem persists, please contact one of the bot developers about it.");
		}
	}
}
