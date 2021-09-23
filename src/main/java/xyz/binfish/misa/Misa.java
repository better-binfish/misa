package xyz.binfish.misa;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;

import xyz.binfish.misa.Constants;
import xyz.binfish.misa.handler.Handler;
import xyz.binfish.misa.handler.listeners.ReadyListener;
import xyz.binfish.misa.handler.listeners.MessageReceivedListener;
import xyz.binfish.misa.handler.listeners.GuildListener;
import xyz.binfish.misa.handler.listeners.MemberListener;

import xyz.binfish.misa.database.DatabaseManager;
import xyz.binfish.misa.database.Database;
import xyz.binfish.misa.database.schema.Migration;

import xyz.binfish.misa.locale.LanguageManager;
import xyz.binfish.misa.util.ClassLoaderUtil;

import xyz.binfish.logger.Logger;
import xyz.binfish.logger.LoggerConfig;

public class Misa {

	private static Handler handler;
	private static JDA jda;
	private static DatabaseManager databaseManager;

	private static final String VERSION = "0.1";
	private static boolean DEBUG_MODE;

	private Misa() throws LoginException {

		// Init config
		Configuration config = Configuration.getInstance();

		// Logger creation
		String pathToLogDirectory = config.get("defaultLogDirectory", "logs/");

		Logger logger = Logger.createLogger(
			new LoggerConfig(
				(pathToLogDirectory != null && new File(pathToLogDirectory).exists()
				 ? pathToLogDirectory
				 : null)).setUseColors(true)
		);

		// Locale
		logger.info("Upping language manager ...");
		LanguageManager.up();

		// Database part
		logger.info("Database manager initialization ...");
		databaseManager = new DatabaseManager();

		ArrayList<Class> classes = ClassLoaderUtil.getInstance()
			.getListClassesFromJar(Constants.PACKAGE_MIGRATION, new ArrayList<Class>());

		logger.info("Upping database migrations ...");
		for(Class cls : classes) {
			try {
				Migration mg = (Migration) cls.newInstance();
				mg.up(databaseManager.getSchema());
			} catch(InstantiationException | IllegalAccessException | java.sql.SQLException e) {
				logger.error("Migration error: " + e.getMessage());
			}
		}

		// Handler
		handler = new Handler();
		handler.load(Constants.PACKAGE_MODULES + ".commands");

		try {
			jda = JDABuilder.createDefault(config.get("token", null))
				.setStatus(OnlineStatus.ONLINE)
				.setActivity(Activity.watching(config.get("activityText", null)))
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setChunkingFilter(ChunkingFilter.NONE)
				.enableIntents(EnumSet.of(
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
				.enableCache(EnumSet.of(
							CacheFlag.ACTIVITY,
							CacheFlag.CLIENT_STATUS)
				)
				.addEventListeners(
						new ReadyListener(),
						new MessageReceivedListener(handler),
						new GuildListener(),
						new MemberListener()
				)
				.build();

			jda.awaitReady();
		} catch(InterruptedException e) {
			logger.error("Something went wrong while building JDA!", e);
		}
	}

	public static void main(String[] args) throws LoginException {
		if(args.length > 0) {
			DEBUG_MODE = (args[0].equals("debugMode=true") ? true : false);
		}

		new Misa();
	}

	public static Handler getHandler() {
		return handler;
	}

	public static JDA getJDA() {
		return jda;
	}

	public static DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public static boolean getDebugMode() {
		return DEBUG_MODE;
	}

	public static String getVersion() {
		return VERSION;
	}
}
