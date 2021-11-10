package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.Misa;

public class ShutdownCommand extends Command {

	public ShutdownCommand() {
		super();
		this.name = "shutdown";
		this.aliases = null;
		this.usage = "shutdown";
		this.guildOnly = false;
		this.privateAccess = true;
	}

	@Override
	public String getDescription() {
		return "Safely shuts off the bot.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		Misa.shutdown();
		return true;
	}
}
