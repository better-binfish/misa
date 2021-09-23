package xyz.binfish.misa.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MentionableUtil {

	/*
	 * A simple regular expression used to match a string
	 * to see if it matches a user mention format.
	 */
	private static final Pattern MENTION_PATTERN = Pattern.compile("<@(!|)+[0-9]{16,}+>");

	/*
	 * Gets the first user object matching in the given message context and arguments.
	 *
	 * @param context the message object.
	 * @param args    the arguments parsed to the command.
	 * @return the user matching the first inder or null.
	 */
	public static User getUser(Message context, String[] args) {
		return getUser(context, args, 0);
	}

	/*
	 * Get the N index user object matching in the given message context and arguments.
	 *
	 * @param context the message object.
	 * @param args    the arguments parsed to the command.
	 * @param index   the index of the argument that should be checked.
	 * @return user matching the given index or null.
	 */
	public static User getUser(Message context, String[] args, int index) {
		if(args.length <= index) {
			return null;
		}

		String arg = args[index].trim();

		if(MENTION_PATTERN.matcher(arg).matches()) {
			String userId = arg.substring(2, arg.length() - 1);

			if(userId.charAt(0) == '!') {
				userId = userId.substring(1, userId.length());
			}

			try {
				Member member = context.getGuild().getMemberById(userId);
				return member == null ? null : member.getUser();
			} catch(NumberFormatException e) {
				return null;
			}
		}

		return null;
	}

	/*
	 * Get all user object matching in the given message context and arguments.
	 *
	 * @param context the message object.
	 * @param args    the arguments parsed to the command.
	 * @return the list of matching users or null.
	 */
	public static List<User> getUsers(Message context, String[] args) {
		if(args.length < 1) {
			return null;
		}

		List<User> users = new ArrayList<User>();

		for(String element : args) {

			element = element.trim();

			if(MENTION_PATTERN.matcher(element).matches()) {
				String userId = element.substring(2, element.length() - 1);

				if(userId.charAt(0) == '!') {
					userId = userId.substring(1, userId.length());
				}

				try {
					Member member = context.getGuild().getMemberById(userId);
					users.add(member.getUser());
				} catch(NumberFormatException e) {
					return null;
				}
			}
		}

		return users.isEmpty() ? null : users;
	}

	/*
	 * Checks if the array of arguments contains mentions of the user.
	 *
	 * @param args the array of string arguments to matching.
	 * @return true if there is a mention in the array otherwise false.
	 */
	public static boolean isMentionUser(String[] args) {
		if(args.length < 1) {
			return false;
		}

		for(String element : args) {
			if(MENTION_PATTERN.matcher(element.trim()).matches()) {
				return true;
			}
		}

		return false;
	}

	/*
	 * Get the first channel object matching in the given message context and arguments.
	 *
	 * @param context the message object.
	 * @param args    the arguments parsed to the command.
	 * @return the first channel matching the given argument or null.
	 */
	public static GuildChannel getChannel(Message context, String[] args) {
		return getChannel(context, args, 0);
	}

	/*
	 * Get the N index channel object matching in the given message context and arguments.
	 *
	 * @param context the message object.
	 * @param args    the arguments parsed to the command.
	 * @param index   the index of the argument that should be checked.
	 * @return the channel matching the given index or null.
	 */
	public static GuildChannel getChannel(Message context, String[] args, int index) {
		if(!context.getMentionedChannels().isEmpty()) {
			return context.getMentionedChannels().get(0);
		}

		if(args.length <= index) {
			return null;
		}

		String part = args[index].trim();

		if(NumberUtil.isNumeric(part)) {
			TextChannel textChannel = context.getGuild().getTextChannelById(part);
			if(textChannel != null) {
				return textChannel;
			}

			return context.getGuild().getVoiceChannelById(part);
		}

		return null;
	}
}
