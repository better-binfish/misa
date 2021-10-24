package xyz.binfish.misa.handler.listeners;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import xyz.binfish.misa.handler.Listener;
import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.locale.LanguageManager;
import xyz.binfish.misa.locale.LanguagePackage;
import xyz.binfish.misa.util.MessageType;
import xyz.binfish.misa.Misa;

public class VerificationListener extends Listener {

	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) {
			return;
		}

		if(Misa.getCache().has("verify_" + event.getAuthor().getId())) {
			String[] values = ((String) Misa.getCache().get("verify_" + event.getAuthor().getId())).split(":");
			Misa.getCache().forget("verify_" + event.getAuthor().getId());

			if(values[0].equals(event.getMessage().getContentRaw())) {
				Guild verifyGuild = Misa.getJDA().getGuildById(values[1]);
				LanguagePackage langPackage = LanguageManager.getLocale(verifyGuild);

				GuildModel guildModel = GuildController.fetchGuild(verifyGuild);
				if(guildModel == null) {
					event.getMessage().getChannel().sendMessage(
							langPackage.getString("data.errors.errorOccurredWhileLoading", "server settings")).queue();
					return;
				}

				Role verifyRole = verifyGuild.getRoleById(guildModel.getVerifyRoleId());
				if(verifyRole == null) {
					event.getMessage().getChannel().sendMessage(
							langPackage.getString("data.errors.noRolesWithNameOrId",
								String.valueOf(guildModel.getVerifyRoleId()))
					).queue();
					return;
				}

				verifyGuild.addRoleToMember(event.getAuthor().getIdLong(), verifyRole).queue();
			} else {
				event.getMessage().getChannel().sendMessage("Invalid code, please try again.").queue();
				return;
			}
		}
	}
}
