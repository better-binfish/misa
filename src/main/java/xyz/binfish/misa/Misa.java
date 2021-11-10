package xyz.binfish.misa;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.SelfUser;

import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import javax.security.auth.login.LoginException;

import java.util.ArrayList;
import java.util.EnumSet;

import xyz.binfish.misa.Constants;
import xyz.binfish.misa.handler.Handler;
import xyz.binfish.misa.handler.listeners.ReadyListener;
import xyz.binfish.misa.handler.listeners.MessageReceivedListener;
import xyz.binfish.misa.handler.listeners.GuildListener;
import xyz.binfish.misa.handler.listeners.MemberListener;
import xyz.binfish.misa.handler.listeners.VerificationListener;

import xyz.binfish.misa.database.DatabaseManager;
import xyz.binfish.misa.database.Database;
import xyz.binfish.misa.database.schema.Migration;

import xyz.binfish.misa.locale.LanguageManager;
import xyz.binfish.misa.cache.CacheManager;
import xyz.binfish.misa.scheduler.ScheduleHandler;
import xyz.binfish.misa.scheduler.Job;
import xyz.binfish.misa.util.ClassLoaderUtil;

import xyz.binfish.logger.Logger;

public class Misa {

	private static Handler handler;
	private static DatabaseManager databaseManager;
	private static CacheManager cacheManager;
	private static Settings settings;
	private static ShardManager shardManager;

	public Misa(Settings settings, Configuration config) throws LoginException {
		this.settings = settings;

		Logger logger = Logger.getLogger();
		logger.log(getVersionInfo());

		// Locale
		logger.info("Upping language manager ...");
		LanguageManager.up();

		// Cache
		logger.info("Creating cache manager ...");
		cacheManager = new CacheManager();

		// Jobs
		logger.info("Registering jobs ...");
		for(Class cls : ClassLoaderUtil.getInstance()
				.getListClassesFromJar(Constants.PACKAGE_JOBS, new ArrayList<Class>())) {
			try {
				ScheduleHandler.registerJob((Job) cls.newInstance());
			} catch(InstantiationException | IllegalAccessException e) {
				logger.error("Failed registering job, error: " + e.getMessage());
			}
		}

		logger.info(String.format("Registered %s jobs successfully.", ScheduleHandler.entrySet().size()));

		// Database part
		logger.info("Database manager initialization ...");
		databaseManager = new DatabaseManager();

		// Migrations
		logger.info("Upping database migrations ...");
		for(Class cls : ClassLoaderUtil.getInstance()
				.getListClassesFromJar(Constants.PACKAGE_MIGRATION, new ArrayList<Class>())) {
			try {
				Migration mg = (Migration) cls.newInstance();
				mg.up(databaseManager.getSchema());
			} catch(InstantiationException | IllegalAccessException | java.sql.SQLException e) {
				logger.error("Failed upping migration, error: " + e.getMessage());
			}
		}

		// Handler
		handler = new Handler();
		handler.load(Constants.PACKAGE_MODULES + ".commands");

		try {
			shardManager = buildShardManager(config);
		} catch(LoginException e) {
			logger.error("Something went wrong while building ShardManager!", e);
		}
	}

	public static Handler getHandler() {
		return handler;
	}

	public static ShardManager getShardManager() {
		return shardManager;
	}

	public static SelfUser getSelfUser() {
		for(JDA shard : getShardManager().getShards()) {
			if(shard.getStatus().equals(JDA.Status.CONNECTED)) {
				return shard.getSelfUser();
			}
		}
		return null;
	}

	public static DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public static CacheManager getCache() {
		return cacheManager;
	}

	public static Settings getSettings() {
		return settings;
	}

	public static void shutdown() {
		Logger logger = Logger.getLogger();
		logger.info("Shutting down.");

		if(getShardManager() != null) {
			for(JDA shard : getShardManager().getShards()) {
				shard.shutdown();
			}
		}

		try {
			getDatabaseManager().getDatabase().close();
		} catch(java.sql.SQLException e) {
			logger.error("Failed to close database connection during shutdown: ", e);
		}

		logger.closeLogger();
		System.exit(0);
	}

	private String getVersionInfo() {
		return new String("\n" +
				"'||    ||'  ||\n" +
				" |||  |||  ...   ....   ....\n" +
				" |'|..'||   ||  ||. '  '' .||\n" +
				" | '|' ||   ||  . '|.. .|' ||\n" +
				".|. | .||. .||. |'..|' '|..'|'\n" +
				""
				+ "\n\tVersion: " + AppInfo.getAppInfo().version
				+ "\n\tJVM:     " + System.getProperty("java.version")
				+ "\n\tJDA:     " + JDAInfo.VERSION
				+ "\n"
		);
	}

	private ShardManager buildShardManager(Configuration config) throws LoginException {
		DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(EnumSet.of(
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_BANS,
				GatewayIntent.GUILD_EMOJIS,
				GatewayIntent.GUILD_INVITES,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_PRESENCES,
				GatewayIntent.DIRECT_MESSAGES)
			)
			.setToken(config.get("token", null))
			.setStatus(OnlineStatus.ONLINE)
			.setActivity(Activity.watching(config.get("activityText", null)))
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.setChunkingFilter(ChunkingFilter.NONE)
			.enableCache(EnumSet.of(
					CacheFlag.ACTIVITY,
					CacheFlag.CLIENT_STATUS)
			)
			.setShardsTotal(settings.getShardCount());

		if(settings.getShards() != null) {
			builder.setShards(settings.getShards());
		}

		builder.addEventListeners(
			new ReadyListener(),
			new MessageReceivedListener(getHandler()),
			new GuildListener(),
			new MemberListener(),
			new VerificationListener()
		);

		return builder.build();
	}
}
