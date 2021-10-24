package xyz.binfish.misa.database.model;

import net.dv8tion.jda.api.entities.Guild;

import xyz.binfish.misa.database.collection.DataRow;
import xyz.binfish.misa.Configuration;

public class GuildModel extends AbstractModel {

	private long id;
	private long ownerId;
	private String name;
	private String icon;
	private String locale;
	private String prefix;

	private long logChannelId;
	private long autoRoleId;
	private long verifyRoleId;

	private boolean isPremium = false;
	private boolean isBanned = false;

	public GuildModel(Guild guild) {
		super(null);

		this.id = guild.getIdLong();
		this.ownerId = guild.getOwnerIdLong();
		this.name = guild.getName();
		this.prefix = Configuration.getInstance().get("defaultPrefix", null);
	}

	public GuildModel(DataRow data) {
		super(data);

		if(hasData()) {
			this.id = data.getLong("id");
			this.ownerId = data.getLong("owner_id");
			this.name = data.getString("name");
			this.icon = data.getString("icon");
			this.locale = data.getString("locale");
			this.prefix = data.getString("prefix");

			this.logChannelId = data.getLong("log_channel_id");
			this.autoRoleId = data.getLong("auto_role_id");
			this.verifyRoleId = data.getLong("verify_role_id");

			this.isPremium = data.getBoolean("is_premium");
			this.isBanned = data.getBoolean("is_banned");
		}

		reset();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long id) {
		this.ownerId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String hash) {
		this.icon = hash;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String code) {
		this.locale = code;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public long getLogChannelId() {
		return logChannelId;
	}

	public void setLogChannelId(long id) {
		this.logChannelId = id;
	}

	public long getAutoRoleId() {
		return autoRoleId;
	}

	public void setAutoRoleId(long id) {
		this.autoRoleId = id;
	}

	public long getVerifyRoleId() {
		return verifyRoleId;
	}

	public void setVerifyRoleId(long id) {
		this.verifyRoleId = id;
	}

	public boolean isPremium() {
		return isPremium();
	}

	public void setPremium(boolean premium) {
		this.isPremium = premium;
	}

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean banned) {
		this.isBanned = banned;
	}
}
