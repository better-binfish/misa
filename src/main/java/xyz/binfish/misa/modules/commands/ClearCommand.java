package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.Permission;

import java.util.List;

import xyz.binfish.misa.handler.Command;

public class ClearCommand extends Command {

	private static final int MAX_MESSAGES = 100;

	public ClearCommand() {
		super();
		this.name = "clear";
		this.aliases = new String[]{ "cls" };
		this.usage = "clear <count_messages>";
		this.guildOnly = true;
		this.permissions = new Permission[] {
			Permission.MESSAGE_MANAGE
		};
	}

	@Override
	public String getDescription() {
		return "Deletes the specified number of messages.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		if(args.length < 1) {
			return this.sendError(message,
					"data.errors.missingArgument", "<count_messages>");
		}

		int countOfMessages = 0;

		try {
			countOfMessages = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			return this.sendError(message,
					"data.errors.invalidProperty", args[0], "positive number");
		}

		if(countOfMessages <= 0 || countOfMessages > MAX_MESSAGES) {
			return this.sendWarning(message,
					this.getString("tryingToExceedLimit"));
		}

		List<Message> messages = channel.getHistory()
			.retrievePast(countOfMessages + 1).complete();
		channel.purgeMessages(messages);

		this.sendSuccess(message, this.getString("success")
				.replace(":countOfMessages", String.valueOf(countOfMessages)));

		return true;
	}
}
