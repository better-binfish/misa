package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.locale.LanguageManager;
import xyz.binfish.misa.locale.LanguagePackage;
import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.util.MessageType;
import xyz.binfish.misa.Misa;
import xyz.binfish.misa.Constants;
import xyz.binfish.logger.Logger;

public class LanguageCommand extends Command {

	public LanguageCommand() {
		super();
		this.name = "language";
		this.aliases = new String[]{ "lang", "locale" };
		this.usage = "language [code]";
		this.guildOnly = true;
		this.permissions = new Permission[]{
			Permission.ADMINISTRATOR
		};
	}

	@Override
	public String getDescription() {
		return "Show a list of available languages or set a language that should be used for the server.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		if(args.length < 1) {
			return sendLanguageList(message);
		}

		LanguagePackage desiredPackage = LanguageManager.parse(args[0]);

		if(desiredPackage == null) {
			return this.sendError(message,
					String.format("data.commands.%s.invalidLanguageCode", this.getClassName()),
					args[0]);
		}

		GuildModel guildModel = GuildController.fetchGuild(message);
		if(guildModel == null) {
			return this.sendError(message,
					"data.errors.errorOccurredWhileLoading", "server settings");
		}

		try {
			Misa.getDatabaseManager().newQueryBuilder(Constants.GUILD_TABLE_NAME)
				.where("id", message.getGuild().getId())
				.update(statement -> statement.set("locale", desiredPackage.getCode()));

			this.sendSuccess(message, this.getString("changed")
					.replace(":name", desiredPackage.getNativeName()));

			return true;
		} catch(SQLException e) {
			Logger.getLogger().error(
					String.format("Failed to update the language for a server(%s), error: ",
						message.getGuild().getId()) + e.getMessage());

			return this.sendError(message,
					"Failed to update the servers language settings, please try "
					+ "again, if this problem persists, please contact one of the bot developers about it.");
		}
	}

	private boolean sendLanguageList(Message context) {
		List<String> items = new ArrayList<>();

		for(LanguagePackage langPackage : LanguageManager.getLocales().values()) {
			items.add(String.format("`%s` %s", langPackage.getCode(), langPackage.getNativeName()));
		}

		EmbedBuilder languageList = new EmbedBuilder()
			.setColor(MessageType.INFO.getColor())
			.setTitle(this.getString("embed.title"))
			.setDescription(String.join("\n", items))
			.addField(
				this.getString("embed.fields.usageField.title"),
				String.format("`%s`", this.usage),
				true
			)
			.addField(
				this.getString("embed.fields.exampleField.title"),
				String.format("`%s %s`", this.name, LanguageManager.getDefaultLocale().getCode()),
				true
			);

		context.getChannel()
			.sendMessage(languageList.build()).queue();

		return true;
	}
}
