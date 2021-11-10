package xyz.binfish.misa.handler.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import javax.annotation.Nonnull;

import xyz.binfish.misa.handler.Listener;

public class ReadyListener extends Listener {

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info(
			String.format("%s is ready. Launched shard with ID: %s",
				event.getJDA().getSelfUser().getAsTag(),
				event.getJDA().getShardInfo().getShardId())
		);
	}
}
