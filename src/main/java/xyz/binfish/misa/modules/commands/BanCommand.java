package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.Permission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.util.StringUtil;
import xyz.binfish.misa.util.MentionableUtil;

public class BanCommand extends Command {

	public BanCommand() {
		super();
		this.name = "ban";
		this.aliases = new String[]{ "forceban" };
		this.usage = "ban <user_id> [user_id ...]";
		this.guildOnly = true;
		this.permissions = new Permission[]{
			Permission.BAN_MEMBERS
		};
	}

	@Override
	public String getDescription() {
		return "Ban or unban a group of identifiers for the guild.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		Guild currentGuild = message.getGuild();

		boolean isUnban = false;
		boolean argsIncrement = true;

		if(args.length > 0) {

			// Argument tracking
			switch( args[0].toLowerCase() ) {
				case "--unban":
					isUnban = true;
					break;
				case "--get-list":
					currentGuild.retrieveBanList().queue(bans -> {
						if(bans.isEmpty()) {
							this.sendWarning(message,
								this.getString("listIsEmpty"));
							return;
						}

						channel.sendMessage(this.getString("listHeader")
								.replace(":guildName", currentGuild.getName())).queue();

						StringBuilder idsList = new StringBuilder().append("```\r\n");

						for(Ban ban : bans)
							idsList.append(ban.getUser().getId() + "\r\n");
						idsList.append("```");

						if(idsList.length() > 2000) {
							try {
								File tmpFile = File.createTempFile("guild_" + currentGuild.getId() + "_bans_", ".txt");
								FileOutputStream fos = new FileOutputStream(tmpFile);

								fos.write(idsList.toString().getBytes());
								channel.sendFile(tmpFile).queue();
							} catch(IOException e) {
								this.sendError(message, String.format(
									"data.commands.%s.failedToSaveOrUploadFile", this.getClassName())
								);
								return;
							}
						} else {
							channel.sendMessage(idsList).queue();
						}
					});
					break;
				default:
					argsIncrement = false;
					break;
			}
		}

		List<String> ids = new ArrayList<String>();

		// Collecting ids
		if(MentionableUtil.isMentionUser(args)) {
			for(User user : MentionableUtil.getUsers(message, args)) {
				ids.add(user.getId());
			}
		} else {
			for(int i = ((argsIncrement) ? 1 : 0); i < args.length; i++) {
				String currentId = args[i];

				if(StringUtil.isIdentifier(currentId)) {
					ids.add(currentId);
				}
			}
		}

		if(ids.size() == 0) {
			return this.sendError(message,
					"data.errors.missingArgument", "<user_id>");
		}

		StringBuilder success = new StringBuilder()
			.append(this.getString("successToBanOrUnban")
				.replace(":action", ((!isUnban) ? "blocked" : "unblocked")))
			.append("\n```\n");

		for(String id : ids) {
			try {
				if(isUnban) {
					currentGuild.unban(id).queue();
				} else {
					currentGuild.ban(id, 0).queue();
				}

				success.append(String.format("%s\r\n", id));
			} catch(Exception e) {
				return this.sendError(message,
						String.format("data.commands.%s.failedToBanOrUnban", this.getClassName()),
						((!isUnban) ? "ban" : "unban"), id);
			}
		}

		success.append("```");
		channel.sendMessage(success).queue();

		return true;
	}
}
