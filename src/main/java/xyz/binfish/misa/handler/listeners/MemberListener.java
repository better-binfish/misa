package xyz.binfish.misa.handler.listeners;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.LocalDateTime;

import xyz.binfish.misa.handler.Listener;
import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.util.MessageType;
import xyz.binfish.misa.locale.LanguageManager;

public class MemberListener extends Listener {

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		GuildModel guildModel = GuildController.fetchGuild(event.getGuild());
		if(guildModel == null) {
			return;
		}

		TextChannel channel = event.getGuild().getTextChannelById(guildModel.getLogChannelId());
		if(channel == null) {
			return;
		}

		channel.sendMessage(
			new EmbedBuilder()
				.setColor(MessageType.INFO.getColor())
				.setDescription(LanguageManager.getLocale(guildModel)
					.getString("data.events.GuildMemberJoinEvent.embed.description")
						.replace(":mention", String.format("<@%s>", event.getUser().getId()))
						.replace(":id", event.getUser().getId())
				)
				.setFooter("Member Join")
				.setTimestamp(LocalDateTime.now())
				.build()
		).queue();
	}

	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		GuildModel guildModel = GuildController.fetchGuild(event.getGuild());
		if(guildModel == null) {
			return;
		}

		TextChannel channel = event.getGuild().getTextChannelById(guildModel.getLogChannelId());
		if(channel == null) {
			return;
		}

		event.getGuild().retrieveBanById(event.getUser().getIdLong()).queue(ban -> {
			return;
		}, error -> {
			channel.sendMessage(
				new EmbedBuilder()
					.setColor(MessageType.INFO.getColor())
					.setDescription(LanguageManager.getLocale(guildModel)
						.getString("data.events.GuildMemberLeaveEvent.embed.description")
							.replace(":tag", event.getUser().getAsTag())
							.replace(":id", event.getUser().getId())
					)
					.setFooter("Member Leave")
					.setTimestamp(LocalDateTime.now())
					.build()
			).queue();
		});
	}

	@Override
	public void onGuildVoiceMute(GuildVoiceMuteEvent event) {
		// mute command FIXME
	}

	@Override
	public void onGuildBan(GuildBanEvent event) {
		GuildModel guildModel = GuildController.fetchGuild(event.getGuild());
		if(guildModel == null) {
			return;
		}

		TextChannel channel = event.getGuild().getTextChannelById(guildModel.getLogChannelId());
		if(channel == null) {
			return;
		}

		event.getGuild().retrieveBanById(event.getUser().getIdLong()).queue(ban -> {
			channel.sendMessage(
				new EmbedBuilder()
					.setColor(MessageType.INFO.getColor())
					.setDescription(LanguageManager.getLocale(guildModel)
						.getString("data.events.GuildBanEvent.embed.description")
							.replace(":tag", ban.getUser().getAsTag())
							.replace(":id", ban.getUser().getId())
							.replace(":reason", (ban.getReason() != null ? ban.getReason() : "Not specified"))
					)
					.setFooter("Member Ban")
					.setTimestamp(LocalDateTime.now())
					.build()
			).queue();
		}, error -> channel.sendMessage("Something went wrong when retrieving unban, "
			+ "please contact one of the bot developers about it.").queue());
	}

	@Override
	public void onGuildUnban(GuildUnbanEvent event) {
		GuildModel guildModel = GuildController.fetchGuild(event.getGuild());
		if(guildModel == null) {
			return;
		}

		TextChannel channel = event.getGuild().getTextChannelById(guildModel.getLogChannelId());
		if(channel == null) {
			return;
		}

		channel.sendMessage(
			new EmbedBuilder()
				.setColor(MessageType.INFO.getColor())
				.setDescription(LanguageManager.getLocale(guildModel)
					.getString("data.events.GuildUnbanEvent.embed.description")
						.replace(":tag", event.getUser().getAsTag())
						.replace(":id", event.getUser().getId())
				)
				.setFooter("Member Unban")
				.setTimestamp(LocalDateTime.now())
				.build()
		).queue();
	}
}
