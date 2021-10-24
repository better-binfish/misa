package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.util.DateFormatter;
import xyz.binfish.misa.util.StringUtil;
import xyz.binfish.misa.util.MessageType;

public class RoleCommand extends Command {

	public RoleCommand() {
		super();
		this.name = "role";
		this.aliases = null;
		this.usage = "role <@role> | <role_id> | <role_name>";
		this.guildOnly = true;
	}

	@Override
	public String getDescription() {
		return "Get information about specified role by mention, identifier or name of role.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		Guild currentGuild = message.getGuild();
		Role role = null;

		if(args.length < 1) {
			return this.sendError(message,
					"data.errors.missingArgument", "<@role> | <role_id> | <role_name>");
		}

		if(!StringUtil.isIdentifier(args[0])) {
			if(message.getMentionedRoles().isEmpty()) {
				List<Role> roles = currentGuild.getRolesByName(args[0], true);

				if(!roles.isEmpty()) {
					role = roles.get(0);
				}
			} else {
				role = message.getMentionedRoles().get(0);
			}
		} else {
			role = currentGuild.getRoleById(args[0]);
		}

		if(role == null) {
			return this.sendError(message,
					"data.errors.noRolesWithNameOrId", args[0]);
		}

		channel.sendMessage(
			new EmbedBuilder()
				.setColor(MessageType.INFO.getColor())
				.setDescription(
					this.getString("embed.description")
						.replace(":name", role.getName())
						.replace(":id", role.getId())
						.replace(":color",
							(role.getColor() != null
							 	? String.format("#%s", Integer.toHexString(role.getColor().getRGB())
									.substring(2).toUpperCase())
								: "None"))
						.replace(":mentionable", (role.isMentionable() ? "Yes" : "No"))
						.replace(":permissions", Arrays.asList(role.getPermissions().toArray())
							.stream()
							.map(Object::toString)
							.collect(Collectors.joining(", ")))
						.replace(":position", String.valueOf(role.getPosition()))
						.replace(":creationDate", DateFormatter.getReadableDateTime(
								role.getTimeCreated().toLocalDateTime()))
				)
				.build()
		).queue();

		return true;
	}
}
