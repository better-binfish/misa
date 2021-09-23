package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.EmbedBuilder;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.util.MentionableUtil;
import xyz.binfish.misa.util.MessageType;

public class AvatarCommand extends Command {

	public AvatarCommand() {
		super();
		this.name = "avatar";
		this.aliases = new String[]{ "ava", "pfa" };
		this.usage = "avatar [@member] | [user_id]";
		this.guildOnly = true;
	}

	@Override
	public String getDescription() {
		return "Get the profile picture of someone on the server by id, mention or your default.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		User user;

		if(MentionableUtil.isMentionUser(args)) {
			user = MentionableUtil.getUser(message, args);
		} else {
			user = author;

			if(args.length > 0) {
				Member member = message.getGuild().getMemberById(args[0]);

				if(member != null) {
					user = member.getUser();
				}
			}
		}

		if(!user.isBot()) {
			EmbedBuilder embed = new EmbedBuilder()
				.setColor(MessageType.INFO.getColor())
				.setTitle(user.getAsTag() + " nya~", null)
				.setImage(user.getEffectiveAvatarUrl() + "?size=1024");

			channel.sendMessage(embed.build()).queue();
		}

		return true;
	}
}
