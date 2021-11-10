package xyz.binfish.misa.modules.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import xyz.binfish.misa.handler.Command;
import xyz.binfish.misa.util.MessageType;
import xyz.binfish.misa.util.StringUtil;
import xyz.binfish.misa.Misa;
import xyz.binfish.misa.Constants;

public class ShardCommand extends Command {

	public ShardCommand() {
		super();
		this.name = "shards";
		this.aliases = null;
		this.usage = "shards";
		this.guildOnly = false;
		this.privateAccess = true;
	}

	@Override
	public String getDescription() {
		return "Get information about bot shards.";
	}

	@Override
	public boolean execute(String[] args, MessageChannel channel, User author, Message message) {
		if(Misa.getSettings().getShardCount() < 2) {
			return this.sendError(message, "Sharding is not enabled right now.");
		}

		StringBuilder description = new StringBuilder();
		for(int i = Misa.getSettings().getShardCount() - 1; i >= 0; i--) {
			JDA shard = Misa.getShardManager().getShardById(i);

			if(shard == null) {
				description.append(String.format("Shard `#%s` %s (%s)\n",
					i, getShardConnectionIcon(JDA.Status.SHUTDOWN), "Connection to Discord ..."
				));
				continue;
			}

			if(!shard.getStatus().equals(JDA.Status.CONNECTED)) {
				description.append(String.format("Shard `#%s` %s (%s)\n",
					i, getShardConnectionIcon(shard.getStatus()),
					StringUtil.capitalizeOnlyFirstChar(shard.getStatus().name().replace("_", " "))
				));
			}

			description.append(String.format("Shard `#%s` %s (%s)\n",
				shard.getShardInfo().getShardId(),
				getShardConnectionIcon(shard.getStatus()),
				String.format("%s users, %s guilds, %s ms ping", 
					shard.getUsers().size(), shard.getGuilds().size(), shard.getGatewayPing())
			));
		}

		channel.sendMessage(
				new EmbedBuilder()
					.setColor(MessageType.INFO.getColor())
					.setAuthor("Shard Information", null, Misa.getSelfUser().getEffectiveAvatarUrl())
					.setDescription(description.toString())
					.build()
		).queue();

		return true;
	}

	private String getShardConnectionIcon(JDA.Status status) {
		switch(status) {
			case CONNECTED:
				return Constants.EMOJI_ONLINE;
			case FAILED_TO_LOGIN:
			case DISCONNECTED:
			case SHUTTING_DOWN:
			case SHUTDOWN:
				return Constants.EMOJI_OFFLINE;
			default:
				return Constants.EMOJI_IDLE;
		}
	}
}
