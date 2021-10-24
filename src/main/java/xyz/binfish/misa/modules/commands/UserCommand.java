package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.awt.Color;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.util.DateFormatter;
import xyz.binfish.misa.util.StringUtil;
import xyz.binfish.misa.util.MentionableUtil;

public class UserCommand extends Command {

	public UserCommand() {
		super();
		this.name = "user";
		this.aliases = null;
		this.usage = "user [@member] | [user_id]";
		this.guildOnly = false;
	}

	@Override
	public String getDescription() {
		return "Get information about specified user by mention or identifier of user.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		Member member = message.getMember();

		if(args.length > 0) {
			User user = null;

			if(MentionableUtil.isMentionUser(args)) {
				user = MentionableUtil.getUser(message, args);
			}

			if(user == null) {
				return this.sendError(message,
						"data.errors.noUsersWithNameOrId", args[0]);
			}

			member = message.getGuild().getMember(user);
		}

		if(!member.getUser().isBot()) {

			EmbedBuilder embed = new EmbedBuilder()
				.setColor(getRoleColor(member.getRoles()))
				.setThumbnail(member.getUser().getEffectiveAvatarUrl())
				.setDescription(
						this.getString("embed.description")
							.replace(":name", member.getUser().getAsTag())
							.replace(":id", member.getId())
							.replace(":nickname", (member.getNickname() != null ? member.getNickname() : "None"))
							.replace(":status", StringUtil.capitalizeOnlyFirstChar(member.getOnlineStatus().toString()))
							.replace(":isOwner", (member.isOwner() ? "Yes" : "No"))
							.replace(":roles", (member.getRoles().size() == 0) ? "None" : member.getRoles().stream()
								.map(Role::getName)
								.collect(Collectors.joining(", ")))
							.replace(":joinDate", DateFormatter.getReadableDateTime(
									member.getTimeJoined().toLocalDateTime()))
							.replace(":registrationDate", DateFormatter.getReadableDateTime(
									member.getUser().getTimeCreated().toLocalDateTime()))
				);

			if(!member.getActivities().isEmpty()) {
				Iterator<Activity> iterable = member.getActivities().iterator();

				while(iterable.hasNext()) {
					Activity activity = iterable.next();
					String activityText = activity.getName();

					if(activity.isRich()) {
						return false;
					} else {
						if(activity.getType() == ActivityType.CUSTOM_STATUS) {
							String emojiString = null;

							if(activity.getEmoji() != null) {
								emojiString = activity.getEmoji().getAsMention();
							}

							embed.addField("Custom Status", (emojiString != null
										? emojiString + " " + activityText
										: activityText), true);
						} else if(activity.getType() == ActivityType.DEFAULT) {
							embed.addField("Activity", activityText, true);
						}
					}

					//if(activity.getUrl() != null) {
					//	activityText = String.format("[%s](%s)",
					//			activityText, activity.getUrl());
					//}
				}
			}

			channel.sendMessage(embed.build()).queue();
		}

		return true;
	}

	private Color getRoleColor(List<Role> roles) {
		for(Role role : roles) {
			if(role.getColor() != null) return role.getColor();
		}

		return Color.decode("#00AE86");
	}
}
