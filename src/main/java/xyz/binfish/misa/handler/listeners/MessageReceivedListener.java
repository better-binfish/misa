package xyz.binfish.misa.handler.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

import xyz.binfish.misa.handler.Listener;
import xyz.binfish.misa.handler.Handler;

public class MessageReceivedListener extends Listener {

	private static Handler handler;

	public MessageReceivedListener(Handler preparedHandler) {
		if(preparedHandler != null) {
			this.handler = preparedHandler;
		} else {
			throw new IllegalArgumentException("The message listener needs a handler.");
		}
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		User user = event.getAuthor();

		if(user.isBot() || event.isWebhookMessage()) {
			return;
		}

		handler.registerMessage(event);
	}
}
