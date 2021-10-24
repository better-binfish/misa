package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.EmbedBuilder;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.util.DateFormatter;
import xyz.binfish.misa.util.StringUtil;
import xyz.binfish.misa.util.MessageType;
import xyz.binfish.misa.util.MentionableUtil;

public class ChannelCommand extends Command {

	public ChannelCommand() {
		super();
		this.name = "channel";
		this.aliases = null;
		this.usage = "channel [#channel] | [channel_id]";
		this.guildOnly = true;
	}

	@Override
	public String getDescription() {
		return "Get information about the channel by mention, identifier or name of channel.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel messageChannel, User author, Message message) {
		Guild currentGuild = message.getGuild();
		GuildChannel channel = null;

		if(args.length > 0) {
			channel = MentionableUtil.getChannel(message, args);

		} else {
			channel = currentGuild.getTextChannelById(messageChannel.getIdLong());
		}

		if(channel == null) {
			return this.sendError(message,
					"data.errors.noChannelsWithNameOrId", args[0]);
		}
		
		String additionalDescription = null;

		if(channel instanceof TextChannel) {
			TextChannel textChannel = (TextChannel) channel;
			
			String topic = "None";
			String isNsfw = (textChannel.isNSFW() ? "Yes" : "No");

			if(textChannel.getTopic() != null && textChannel.getTopic().trim().length() > 0) {
				topic = textChannel.getTopic();
			}

			additionalDescription = this.getString(
					"embed.additionalDescriptionTextChannel")
				.replace(":topic", topic)
				.replace(":isNsfw", isNsfw);
		}

		if(channel instanceof VoiceChannel) {
			VoiceChannel voiceChannel = (VoiceChannel) channel;

			int bitRate = voiceChannel.getBitrate() / 1000;
			int userLimit = voiceChannel.getUserLimit();

			additionalDescription = this.getString(
					"embed.additionalDescriptionVoiceChannel")
				.replace(":bitRate", String.valueOf(bitRate) + " kbps")
				.replace(":userLimit", (userLimit == 0) ? "Unlimited" : String.valueOf(userLimit));
		}

		StringBuilder description = new StringBuilder()
			.append(this.getString("embed.description")
					.replace(":name", channel.getName())
					.replace(":id", channel.getId())
					.replace(":category", (channel.getParent() != null
						? channel.getParent().getName() : "None"))
					.replace(":type", StringUtil.capitalizeOnlyFirstChar(
							channel.getType().toString()))
					.replace(":creationDate", DateFormatter.getReadableDateTime(
							channel.getTimeCreated().toLocalDateTime()))
			);

		if(additionalDescription != null) {
			description.append(String.join("\n", additionalDescription));
		}

		messageChannel.sendMessage(
			new EmbedBuilder()
				.setColor(MessageType.INFO.getColor())
				.setDescription(description.toString())
				.build()
		).queue();

		return true;
	}
}
