package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.util.MessageType;
import xyz.binfish.misa.Misa;
import xyz.binfish.misa.Constants;
import xyz.binfish.misa.Configuration;

public class HelpCommand extends Command {

	public HelpCommand() {
		super();
		this.name = "help";
		this.aliases = new String[]{ "?" };
		this.usage = "help [command_name | command_alias]";
		this.guildOnly = false;
	}

	@Override
	public String getDescription() {
		return "Provides a list of commands and what they do, and how you can use them.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		GuildModel guildModel = GuildController.fetchGuild(message);
		String prefix = (guildModel == null
				? Configuration.getInstance().get("defaultPrefix", null)
				: guildModel.getPrefix());

		EmbedBuilder embed = new EmbedBuilder()
			.setColor(MessageType.INFO.getColor());

		if(args.length < 1) {
			StringBuilder commandsList = new StringBuilder();

			Misa.getHandler().eachCommand((command) -> {
				commandsList.append(String.format("`%s` ", command.name));
			});

			embed
				.setDescription(
						this.getString("embed.menuDescription")
							.replace(":commandsCount", String.valueOf(commandsList.toString().split(" ").length))
							.replace(":prefix", prefix)
							.replace(":commands", commandsList)
							.replace(":links", "[Website](#) | [Support](" + Constants.SUPPORT_SERVER + ")")
				)
				.setFooter("Misa version " + Misa.getVersion());
		} else {
			Command command = Misa.getHandler().getCommand(args[0]);

			if(command == null) {
				return this.sendWarning(message,
						this.getString("failedToFindCommand", args[0]));
			}

			String aliases = new String();

			if(command.aliases != null) {
				for(String alias : command.aliases) {
					aliases += String.format("`%s` ", alias);
				}
			}

			String permissions = new String();

			if(command.permissions != null) {
				for(Permission prm : Arrays.copyOf(command.permissions, command.permissions.length)) {
					permissions += String.format("`%s` ", prm.toString());
				}
			}

			String langDescription = langPackage.getString(
					String.format("data.commands.%s.description", command.getClassName())
			);

			embed.setDescription(
					this.getString("embed.commandDescription")
						.replace(":name", command.name)
						.replace(":aliases", (command.aliases != null ? aliases : "None"))
						.replace(":description", (langDescription != null ? langDescription : command.getDescription()))
						.replace(":usage", command.usage)
						.replace(":permissions", (command.permissions != null ? permissions : "None"))
			);
		}

		channel.sendMessage(embed.build()).queue();

		return true;
	}
}
