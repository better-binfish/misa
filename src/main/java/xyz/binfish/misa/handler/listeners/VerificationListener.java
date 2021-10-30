package xyz.binfish.misa.handler.listeners;

import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.EmbedBuilder;

import xyz.binfish.misa.handler.Listener;
import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.util.MessageType;
import xyz.binfish.logger.Logger;

public class VerificationListener extends Listener {

	@Override
	public void onGuildMemberUpdatePending(GuildMemberUpdatePendingEvent event) {
		Guild guild = event.getGuild();
		GuildModel guildModel = GuildController.fetchGuild(guild);
		if(guildModel == null) {
			Logger.getLogger().error("An error occurred while loading the server settings, in VerificationListener#onGuildMemberUpdatePending");
			return;
		}

		if(event.getOldPending() && !(guildModel.getVerifyRoleId() == 0)) {
			Role verifyRole = guild.getRoleById(guildModel.getVerifyRoleId());
			if(verifyRole == null) {

				TextChannel channel = event.getGuild().getTextChannelById(guildModel.getLogChannelId());
				if(channel != null) {
					channel.sendMessage(
							new EmbedBuilder()
								.setColor(MessageType.WARNING.getColor())
								.setDescription(String.format("Role for verification not found and not added to user %s (ID: `%s`)",
										event.getUser().getAsMention(), event.getUser().getId()))
								.build()).queue();
				}
				return;
			}

			if(event.getMember().getRoles().stream()
					.filter(role -> role.getIdLong() == verifyRole.getIdLong())
					.findFirst()
					.orElse(null) != null) {
				return;
			}

			guild.addRoleToMember(event.getUser().getIdLong(), verifyRole).queue();
		}
	}
}
