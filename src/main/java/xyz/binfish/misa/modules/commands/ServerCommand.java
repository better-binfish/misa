package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.EmbedBuilder;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.util.DateFormatter;
import xyz.binfish.misa.util.StringUtil;
import xyz.binfish.misa.util.MessageType;

public class ServerCommand extends Command {

	public ServerCommand() {
		super();
		this.name = "server";
		this.aliases = new String[]{ "guild" };
		this.usage = "server";
		this.guildOnly = true;
	}

	@Override
	public String getDescription() {
		return "Get information about server.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		Guild currentGuild = message.getGuild();

		channel.sendMessage(
				new EmbedBuilder()
					.setColor(MessageType.INFO.getColor())
					.setDescription(
						this.getString("embed.description")
							.replace(":name", currentGuild.getName())
							.replace(":id", currentGuild.getId())
							.replace(":owner", currentGuild.getOwner().getUser().getAsTag())
							.replace(":members", String.valueOf(currentGuild.getMemberCount()))
							.replace(":roles", String.valueOf(currentGuild.getRoles().size()))
							.replace(":channels", String.valueOf(currentGuild.getChannels().size()))
							.replace(":verification",
								StringUtil.capitalizeOnlyFirstChar(currentGuild.getVerificationLevel().toString()))
							.replace(":filter",
								StringUtil.capitalizeOnlyFirstChar(currentGuild.getExplicitContentLevel().toString()))
							.replace(":boostCount", String.valueOf(currentGuild.getBoostCount()))
							.replace(":boostTier",
								StringUtil.capitalizeOnlyFirstChar(currentGuild.getBoostTier().toString()))
							.replace(":region", currentGuild.getRegion().toString())
							.replace(":creationDate", DateFormatter.getReadableDateTime(
									currentGuild.getTimeCreated().toLocalDateTime()))
					)
					.setThumbnail(currentGuild.getIconUrl())
					.build()
		).queue();

		return true;
	}
}
