package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Role;
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

public class VerificationRoleCommand extends Command {

	public VerificationRoleCommand() {
		super();
		this.name = "setverify";
		this.aliases = null;
		this.usage = "setverify <role_id | @role> | disable";
		this.guildOnly = true;
		this.permissions = new Permission[] {
			Permission.ADMINISTRATOR
		};
	}

	@Override
	public String getDescription() {
		return "Set the verification role for the membership screening.";
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
					"data.errors.missingArgument", "<role_id | @role> | disable");
		}

		if(ComparatorUtil.isFuzzyFalse(args[0])) {
			return disableVerify(message);
		}

		Role verifyRole = MentionableUtil.getRole(message, args);
		if(verifyRole == null) {
			return this.sendError(message,
					"data.errors.noRolesWithNameOrId", args[0]);
		}

		return updateVerifyRole(message, verifyRole);
	}

	private boolean disableVerify(Message message) {
		try {
			Misa.getDatabaseManager().newQueryBuilder(Constants.GUILD_TABLE_NAME)
				.where("id", message.getGuild().getId())
				.update(statement -> statement.set("verify_role_id", null));

			this.sendSuccess(message, this.getString("changed")
					.replace(":id", "null"));

			return true;
		} catch(SQLException e) {
			Logger.getLogger().error(
					String.format("Failed to disable the verification role for a server(%s), error: ",
						message.getGuild().getId()) + e.getMessage());

			return this.sendError(message,
					"Failed to disable the servers verification role settings, please try "
					+ "again, if this problem persists, please contact one of the bot developers about it.");
		}
	}

	private boolean updateVerifyRole(Message message, Role role) {
		try {
			Misa.getDatabaseManager().newQueryBuilder(Constants.GUILD_TABLE_NAME)
				.where("id", message.getGuild().getId())
				.update(statement -> statement.set("verify_role_id", role.getIdLong()));

			this.sendSuccess(message, this.getString("changed")
					.replace(":id", role.getId()));
			return true;
		} catch(SQLException e) {
			Logger.getLogger().error(
					String.format("Failed to update the verification role for a server(%s), error: ",
						message.getGuild().getId()) + e.getMessage());

			return this.sendError(message,
					"Failed to update the servers verification role settings, please try "
					+ "again, if this problem persists, please contact one of the bot developers about it.");
		}
	}
}
