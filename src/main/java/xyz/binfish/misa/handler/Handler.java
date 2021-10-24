package xyz.binfish.misa.handler;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import xyz.binfish.misa.Misa;
import xyz.binfish.misa.Configuration;
import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.locale.LanguageManager;
import xyz.binfish.misa.util.ClassLoaderUtil;
import xyz.binfish.misa.exceptions.FaildToLoadCommandException;
import xyz.binfish.logger.Logger;

public class Handler {

	private static final HashMap<String, Command> commands = new HashMap<>();
	private static final HashMap<String, Command> commandsAliases = new HashMap<>();

	private static Logger logger = Logger.getLogger(); 

	public Handler() { }

	public void load(final String packageName) {
		ClassLoaderUtil loader = ClassLoaderUtil.getInstance();

		ArrayList<Class> classes = loader
			.getListClassesFromJar(packageName, new ArrayList<Class>());
		//classes = loader.getListClassesFromDirectoryRecursive(packageName, new ArrayList<Class>());

		for(Class cls : classes) {
			try {
				Constructor<Command> constructCommand = cls.getConstructor();
				this.loadCommand(constructCommand.newInstance());
			} catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadCommand(Command command) {
		String commandName = command.getName();

		if(command.guildOnly == false && command.permissions != null) {
			throw new FaildToLoadCommandException("The command with name '"
					+ commandName + "' cannot have permissions it is used outside the guild");
		}

		// Сommand name validation
		if(this.commands.containsKey(commandName)
				|| this.commandsAliases.containsKey(commandName)) {
			throw new FaildToLoadCommandException("Can't load command, the name '" + commandName
					+ "' is already used as a command name or alias");
		}

		this.commands.put(command.getName(), command);

		if(command.aliases != null) {
			for(String alias : command.aliases) {
				if(this.commands.containsKey(alias)
						|| this.commandsAliases.containsKey(alias)) {
					throw new FaildToLoadCommandException("Can't load command, the alias '" + alias
							+ "' is already used as a command name or alias");
				}

				this.commandsAliases.put(alias, command);
			}
		}

		logger.info(String.format("%s command loaded!", command.getName()));
	}

	public Command getCommand(String commandName) {
		return this.commands.get(commandName);
	}

	public void eachCommand(Consumer<Command> callback) {
		for(Map.Entry<String, Command> entry : this.commands.entrySet()) {
			callback.accept(entry.getValue());
		}
	}

	public void registerMessage(MessageReceivedEvent event) {
		Message message = event.getMessage();

		String prefix = (message.isFromGuild()
				? GuildController.fetchGuild(message).getPrefix()
				: Configuration.getInstance().get("defaultPrefix", "&"));
		String contentRaw = message.getContentRaw();

		if(!contentRaw.startsWith(prefix) && !contentRaw.startsWith(
					String.format("<@%s>", Misa.getJDA().getSelfUser().getId()))) {
			return;
		}

		String[] split = contentRaw.split("\\s+");
		String commandName = new String();

		int commandStartsWith = 1;

		if(split[0].startsWith(prefix)) {
			commandName = split[0].replace(prefix, "").toLowerCase();
		} else {
			if(split.length > 1) {
				commandName = split[1].toLowerCase();
				commandStartsWith++;
			}
		}

		String[] args = Arrays.copyOfRange(split, commandStartsWith, split.length);

		Command command = this.commands.get(commandName);

		if(command == null) {
			command = this.commandsAliases.get(commandName);
		}

		if(command == null) {
			return;
		}

		if(command.guildOnly == true && !event.isFromGuild()) {
			return;
		}

		if(command.privateAccess == true && !event.getAuthor().getId()
				.equals(Configuration.getInstance().get("ownerId", null))) {
			return;
		}
		// Getting language package
		command.langPackage = LanguageManager.getLocale(message);

		// Checking the permission
		if(command.permissions != null) {
			for(Permission prms : command.getRequiredPermissions()) {
				if(!event.getMember().hasPermission(prms)) {
					command.sendError(event.getMessage(),
							"data.errors.missingPermission", prms.getName());
					return;
				}
			}
		}

		try {
			command.execute(args, event.getChannel(), event.getAuthor(), event.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
