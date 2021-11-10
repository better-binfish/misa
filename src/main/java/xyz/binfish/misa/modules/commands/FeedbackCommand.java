package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.LocalDateTime;
import java.sql.SQLException;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.Misa;
import xyz.binfish.misa.Constants;
import xyz.binfish.misa.Configuration;
import xyz.binfish.misa.database.collection.Collection;
import xyz.binfish.logger.Logger;

public class FeedbackCommand extends Command {

	public FeedbackCommand() {
		super();
		this.name = "feedback";
		this.aliases = null;
		this.usage = "feedback <message>";
		this.guildOnly = false;
	}

	@Override
	public String getDescription() {
		return "Send feedback about Misa back to the developers and the staff team.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		TextChannel feedbackChannel = Misa.getShardManager().getTextChannelById(
			Configuration.getInstance().get("feedbackChannelId", null)
		);

		if(feedbackChannel == null) {
			return this.sendError(message,
					String.format("data.commands.%s.invalidFeedbackChannel", this.getClassName()));
		}

		if(args.length == 0) {
			return this.sendError(message,
					"data.errors.missingArgument", "<message>");
		}

		String feedbackMessage = String.join(" ", args);

		if(feedbackMessage.length() < 32) {
			return this.sendWarning(message, 
					this.getString("mustBe32CharactersOrMore"));
		}

		boolean isSend = sendFeedback(message, feedbackChannel, feedbackMessage);

		if(isSend) {
			this.sendSuccess(message, this.getString("success"));
			message.delete().queue();

			return true;
		}

		return false;
	}

	private boolean sendFeedback(Message context, TextChannel feedbackChannel, String message) {
		EmbedBuilder embed = new EmbedBuilder()
			.setAuthor(context.getAuthor().getName() + "#" + context.getAuthor().getDiscriminator(),
					null, context.getAuthor().getEffectiveAvatarUrl())
			.setDescription(message)
			.addField("Channel", buildChannel(context.getChannel()), false)
			.setFooter("Author ID: " + context.getAuthor().getId())
			.setTimestamp(LocalDateTime.now());

		if(context.isFromGuild() && context.getGuild() != null) {
			embed.addField("Server", buildServer(context.getGuild()), false);
		}
		
		try {
			Collection collection = Misa.getDatabaseManager().newQueryBuilder(Constants.FEEDBACK_TABLE_NAME)
				.insert(statement -> {
					statement.set("user_id", context.getAuthor().getIdLong());
					statement.set("channel_id", context.isFromGuild() ? context.getChannel().getIdLong() : null);
					statement.set("message", message, true);
				});

			if(!collection.isEmpty()) {
				String id = collection.first().getString("id");
				embed.setFooter("Author ID: " + context.getAuthor().getId() + " | ID: #" + id);
			}

			feedbackChannel.sendMessage(embed.build()).queue();
		} catch(SQLException e) {
			Logger.getLogger().error("Failed to store feedback in the database: " + e.getMessage(), e);
			return false;
		}

		return true;
	}

	private String buildChannel(MessageChannel message) {
		return message.getName() + " (ID: `" + message.getId() + "`)";
	}

	private String buildServer(Guild server) {
		return server.getName() + " (ID: `" + server.getId() + "`)";
	}
}
