package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.EmbedBuilder;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.util.MessageType;

public class PingCommand extends Command {

	public PingCommand() {
		super();
		this.name = "ping";
		this.aliases = null;
		this.usage = "ping";
		this.guildOnly = false;
	}

	@Override
	public String getDescription() {
		return "Shows the current ping.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		long start = System.currentTimeMillis();
		channel.sendTyping().queue(v -> {
			long ping = System.currentTimeMillis() - start;

			channel.sendMessage(
					new EmbedBuilder()
						.setColor(MessageType.INFO.getColor())
						.setDescription(this.getString("embed.description")
							.replace(":ping", String.valueOf(ping))
							.replace(":rating", ratePing(ping))
							.replace(":heartbeat", String.valueOf(message.getJDA().getGatewayPing())))
						.build()
			).queue();
		});

		return true;
	}

	private String ratePing(long ping) {
		if(ping <= 10)    return this.getString("rating.10");
		if(ping <= 100)   return this.getString("rating.100");
		if(ping <= 200)   return this.getString("rating.200");
		if(ping <= 300)   return this.getString("rating.300");
		if(ping <= 400)   return this.getString("rating.400");
		if(ping <= 700)   return this.getString("rating.700");
		if(ping <= 800)   return this.getString("rating.800");
		if(ping <= 900)   return this.getString("rating.900");
		if(ping <= 1600)  return this.getString("rating.1600");
		if(ping <= 10000) return this.getString("rating.10000");
		return this.getString("rating.other");
	}
}
