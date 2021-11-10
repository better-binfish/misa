package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
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
			return this.sendError(message,
					"data.errors.errorOccurredWhileLoading", "server settings");
		}

		Guild currentGuild = message.getGuild();
		TextChannel logChannel = (guildModel.getLogChannelId() != 0
				? currentGuild.getTextChannelById(guildModel.getLogChannelId())
				: null);

		channel.sendMessage(
				new EmbedBuilder()
					.setColor(MessageType.INFO.getColor())
					.setDescription(
						this.getString("embed.description")
							.replace(":name", currentGuild.getName())
							.replace(":lang", langPackage.getNativeName())
							.replace(":prefix", guildModel.getPrefix())
							.replace(":verification", (guildModel.getVerifyRoleId() != 0
									? "Enable\n" + this.getString("embed.verificationRoleNote")
										.replace(":role", String.format("<@&%1$s> (ID: `%1$s`)", guildModel.getVerifyRoleId()))
									: "Disable"))
							.replace(":channel", (logChannel != null
									? String.format("<#%1$s> (ID: `%1$s`)", logChannel.getId())
									: "None"))
					)
					.setThumbnail(currentGuild.getIconUrl())
					.build()
		).queue();

		return true;
	}
}
