package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.util.DateFormatter;
import xyz.binfish.misa.util.StringUtil;
import xyz.binfish.misa.util.MessageType;

public class EmojiCommand extends Command {

	public EmojiCommand() {
		super();
		this.name = "emoji";
		this.aliases = null;
		this.usage = "emoji [emoji_id] | [emoji_name]";
		this.guildOnly = true;
	}

	@Override
	public String getDescription() {
		return "Get information about the specified emoji by name or id, or a list of all custom emoji.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		Guild currentGuild = message.getGuild();

		EmbedBuilder embed = new EmbedBuilder()
			.setColor(MessageType.INFO.getColor());

		if(args.length > 0) {
			Emote emoji;

			if(args[0].length() < 2) {
				return this.sendWarning(message,
						this.getString("nameIsShort"));	
			}

			if(StringUtil.isIdentifier(args[0])) {
				emoji = currentGuild.getEmoteById(args[0]);
			} else {
				List<Emote> listEmojis = currentGuild.getEmotesByName(args[0], true);
				emoji = (listEmojis.isEmpty() ? null : listEmojis.get(0));
			}

			if(emoji == null) {
				return this.sendWarning(message,
						this.getString("failedToFindEmoji", args[0]));
			}

			embed
				.setDescription(
						this.getString("embed.description")
							.replace(":name", emoji.getName())
							.replace(":id", emoji.getId())
							.replace(":isAnimated", (emoji.isAnimated() ? "Yes" : "No"))
							.replace(":url", emoji.getImageUrl())
							.replace(":creationDate", DateFormatter.getReadableDateTime(
									emoji.getTimeCreated().toLocalDateTime()))
				)
				.setThumbnail(emoji.getImageUrl());
		} else {
			List<Emote> emojis = currentGuild.getEmotes();

			if(emojis.isEmpty()) {
				return this.sendWarning(message, this.getString("noEmojis"));
			}

			StringBuilder description = new StringBuilder();
			for(Emote emoji : emojis) {
				description.append(String.format("%s ", emoji.getAsMention()));
			}

			embed
				.setTitle(this.getString("embed.title")
						.replace(":name", currentGuild.getName()))
				.setDescription(description);
		}

		channel.sendMessage(embed.build()).queue();

		return true;
	}
}
