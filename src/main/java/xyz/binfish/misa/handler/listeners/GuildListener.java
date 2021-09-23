package xyz.binfish.misa.handler.listeners;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.LocalDateTime;
import java.sql.SQLException;

import xyz.binfish.misa.handler.Listener;
import xyz.binfish.misa.Constants;
import xyz.binfish.misa.Misa;
import xyz.binfish.misa.Configuration;
import xyz.binfish.misa.util.MessageType;

public class GuildListener extends Listener {

	@Override
	public void onGuildUpdateName(GuildUpdateNameEvent event) {
		try {
			Misa.getDatabaseManager().newQueryBuilder(Constants.GUILD_TABLE_NAME)
				.where("id", event.getGuild().getId())
				.update(statement -> statement.set("name", event.getGuild().getName(), true));
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		logger.info("%greenJoined guild with an ID of " + event.getGuild().getId()
				+ " called: '" + event.getGuild().getName() + "'%reset");
	
		// metric

		TextChannel channel = Misa.getJDA().getTextChannelById(
				Configuration.getInstance().get("activityLogChannelId", null)
		);

		if(channel == null) {
			return;
		}

		event.getGuild().retrieveOwner().queue(
				owner -> handleSendGuildJoinMessage(event, channel, owner),
				error -> handleSendGuildJoinMessage(event, channel, null)
		);
	}

	private void handleSendGuildJoinMessage(GuildJoinEvent event, TextChannel channel, Member owner) {
		StringBuilder description = new StringBuilder()
			.append(String.format("Joined to guild **%s** (ID: `%s`) ",
						event.getGuild().getName(), event.getGuild().getId()))
			.append("\nOwner is " + (owner == null ? "Unknown (Was not found!)" : String.format("**%s** (ID: `%s`)",
							owner.getUser().getAsTag(), owner.getId())));

		channel.sendMessage(
			new EmbedBuilder()
				.setColor(MessageType.INFO.getColor())
				.setDescription(description)
				.setFooter("Guild Join")
				.setTimestamp(LocalDateTime.now())
				.build()
		).queue();
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		logger.info("%redLeft guild with an ID of " + event.getGuild().getId()
				+ " called: '" + event.getGuild().getName() + "'%reset");

		// metric

		TextChannel channel = Misa.getJDA().getTextChannelById(
				Configuration.getInstance().get("activityLogChannelId", null)
		);

		if(channel == null) {
			return;
		}

		handleSendGuildLeaveMessage(event, channel);
	}

	private void handleSendGuildLeaveMessage(GuildLeaveEvent event, TextChannel channel) {
		channel.sendMessage(
			new EmbedBuilder()
				.setColor(MessageType.INFO.getColor())
				.setDescription(String.format("Left the guild **%s** (ID: `%s`)",
						event.getGuild().getName(), event.getGuild().getId())
				)
				.setFooter("Guild Leave")
				.setTimestamp(LocalDateTime.now())
				.build()
		).queue();
	}
}
