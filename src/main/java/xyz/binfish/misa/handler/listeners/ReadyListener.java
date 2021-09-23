package xyz.binfish.misa.handler.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.JDAInfo;

import javax.annotation.Nonnull;

import xyz.binfish.misa.handler.Listener;
import xyz.binfish.misa.Misa;

public class ReadyListener extends Listener {

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.log("\n" +
				"'||    ||'  ||\n" +
				" |||  |||  ...   ....   ....\n" +
				" |'|..'||   ||  ||. '  '' .||\n" +
				" | '|' ||   ||  . '|.. .|' ||\n" +
				".|. | .||. .||. |'..|' '|..'|'\n" +
				""
				+ "\n\tVersion: " + Misa.getVersion()
				+ "\n\tJVM:     " + System.getProperty("java.version")
				+ "\n\tJDA:     " + JDAInfo.VERSION
				+ "\n"
		);

		logger.info(
				String.format("%s is ready", event.getJDA().getSelfUser().getAsTag())
		);
	}
}
