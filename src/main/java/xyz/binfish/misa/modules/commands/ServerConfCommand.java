package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.util.MessageType;
import xyz.binfish.misa.Misa;

public class ServerConfCommand extends Command {

	public ServerConfCommand() {
		super();
		this.name = "serverconf";
		this.aliases = new String[]{ "guildconf", "srvconf" };
		this.usage = "serverconf";
		this.guildOnly = true;
		this.permissions = new Permission[]{
			Permission.ADMINISTRATOR
		};
	}

	@Override
	public String getDescription() {
		return "View configuration for server.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		GuildModel guildModel = GuildController.fetchGuild(message);
		if(guildModel == null) {
			return this.sendErrorMessage(message,
					"data.errors.errorOccurredWhileLoading", "server settings");
		}

		Guild currentGuild = message.getGuild();
		GuildChannel logChannel = Misa.getJDA()
			.getGuildChannelById(guildModel.getLogChannelId());

		channel.sendMessage(
				new EmbedBuilder()
					.setColor(MessageType.INFO.getColor())
					.setDescription(
						this.getString("embed.description")
							.replace(":name", currentGuild.getName())
							.replace(":channel", (logChannel != null
									? String.format("%s (ID: `%s`)", logChannel.getName(), logChannel.getId())
									: "None"))
							.replace(":lang", langPackage.getNativeName())
							.replace(":prefix", guildModel.getPrefix())
					)
					.setThumbnail(currentGuild.getIconUrl())
					.build()
		).queue();

		return true;
	}
}
