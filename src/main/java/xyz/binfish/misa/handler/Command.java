package xyz.binfish.misa.handler;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import xyz.binfish.misa.locale.LanguagePackage;
import xyz.binfish.misa.util.StringUtil;
import xyz.binfish.misa.util.MessageType;

public abstract class Command {

	public String name;
	public String[] aliases;
	public String usage;

	public boolean guildOnly;
	public boolean privateAccess;

	public Permission[] permissions;
	public LanguagePackage langPackage;

	public Command() { }

	public String getName() {
		return this.name;
	}

	public String[] getAliases() {
		return this.aliases;
	}

	public String getDescription() {
		return null;
	}

	public String getUsage() {
		return this.usage;
	}

	public boolean isGuildOnly() {
		return this.guildOnly;	
	}

	public Permission[] getRequiredPermissions() {
		return this.permissions;
	}

	public String getClassName() {
		return this.getClass().getSimpleName();
	}

	public boolean sendError(Message context, String error) {
		if(!StringUtil.isLanguageString(error)) {
			this.sendResultEmbed(context, error, MessageType.ERROR);

			return false;
		}

		String localizedError = langPackage.getString(error);
		if(localizedError != null) {
			error = localizedError;
		}

		this.sendResultEmbed(context, error, MessageType.ERROR);

		return false;
	}

	public boolean sendError(Message context, String error, Object... args) {
		if(!StringUtil.isLanguageString(error)) {
			this.sendResultEmbed(context,
					langPackage.format(error, args), MessageType.ERROR);

			return false;
		}

		String localizedError = langPackage.getString(error, args);
		if(localizedError != null) {
			error = localizedError;
		}

		this.sendResultEmbed(context, error, MessageType.ERROR);

		return false;
	}

	public boolean sendWarning(Message context, String message) {
		this.sendResultEmbed(context, message, MessageType.WARNING);

		return false;
	}

	public void sendSuccess(Message context, String message) {
		this.sendResultEmbed(context, message, MessageType.SUCCESS);
	}

	private void sendResultEmbed(Message context, String message, MessageType type) {
		String title = "";

		switch(type) {
			case ERROR: title = "Error"; break;
			case WARNING: title = "Warning"; break;
			case SUCCESS: title = "Success"; break;
			case INFO: title = "Info"; break;
		}

		EmbedBuilder embed = new EmbedBuilder()
			.setTitle(title)
			.setColor(type.getColor())
			.setDescription(message)
			.setFooter(String.format("Time: %s", new Date().getTime()));

		context.getChannel().sendMessage(embed.build())
			.complete()
			.delete()
			.queueAfter(10, TimeUnit.SECONDS);
	}

	public String getString(String path) {
		return langPackage.getString("data.commands." + this.getClass().getSimpleName() + "." + path);
	}

	public String getString(String path, Object... args) {
		return langPackage.getString("data.commands." + this.getClass().getSimpleName() + "." + path, args);
	}

	public abstract boolean execute(String[] args, MessageChannel channel, User author, Message inputMessage);
}
