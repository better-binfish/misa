package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.sql.SQLException;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.util.ComparatorUtil;
import xyz.binfish.misa.util.StringUtil;
import xyz.binfish.misa.util.RandomUtil;
import xyz.binfish.misa.util.MessageType;
import xyz.binfish.misa.Misa;
import xyz.binfish.misa.Constants;
import xyz.binfish.logger.Logger;

public class VerificationCommand extends Command {

	public VerificationCommand() {
		super();
		this.name = "verify";
		this.aliases = null;
		this.usage = "verify [disable]";
		this.guildOnly = true;
		// Permission.ADMINISTRATOR for all flags.
	}

	@Override
	public String getDescription() {
		return "This will send you some info about verification.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		GuildModel guildModel = GuildController.fetchGuild(message);
		if(guildModel == null) {
			return this.sendError(message,
					"data.errors.errorOccurredWhileLoading", "server settings");
		}

		message.delete().queue();

		if(args.length > 0) {

			if(!message.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				return this.sendError(message,
						"data.errors.missingPermission", Permission.ADMINISTRATOR.getName());
			}

			if(ComparatorUtil.isFuzzyFalse(args[0])) {
				return disableVerify(message);
			}

			switch( args[0].toLowerCase() ) {
				case "--set-role":
					if(args.length == 1 || !StringUtil.isIdentifier(args[1])) {
						return this.sendError(message,
								"data.errors.missingArgument", "<role_id>");
					}

					Role verifyRole = message.getGuild().getRoleById(args[1]);
					if(verifyRole == null) {
						return this.sendError(message,
								"data.errors.noRolesWithNameOrId", args[1]);
					}

					return updateVerifyRole(message, verifyRole);
				default:
					return false;
			}
		}

		if(guildModel.getVerifyRoleId() == 0) {
			return this.sendError(message,
					String.format("data.commands.%s.isDisabled", this.getClassName()));
		}

		Role verifyRole = message.getGuild().getRoleById(guildModel.getVerifyRoleId());
		if(verifyRole == null) {
			return false;
		}

		if(message.getGuild().getMemberById(author.getIdLong()).getRoles().stream()
					.filter(role -> role.getIdLong() == verifyRole.getIdLong())
					.findFirst()
					.orElse(null) != null) {
			return this.sendWarning(message, this.getString("alreadyVerified"));
		}

		String code = RandomUtil.generateString(6) ;
		Misa.getCache().put("verify_" + author.getId(),
				String.format("%s:%s", code, message.getGuild().getId()), 60 * 2);

		author.openPrivateChannel().queue(pc -> pc.sendMessage(
					new EmbedBuilder()
						.setColor(MessageType.INFO.getColor())
						.setDescription(
							this.getString("embed.description")
								.replace(":code", code)
						).build()).queue()
		);

		return true;
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
