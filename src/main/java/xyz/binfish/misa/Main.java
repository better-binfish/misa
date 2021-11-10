package xyz.binfish.misa;

import java.io.File;
import java.io.IOException;
import javax.security.auth.login.LoginException;

import xyz.binfish.misa.cli.CommandLineParser;
import xyz.binfish.misa.cli.CommandLine;
import xyz.binfish.misa.cli.HelpFormatter;
import xyz.binfish.misa.cli.Options;
import xyz.binfish.misa.cli.Option;
import xyz.binfish.misa.exceptions.ParseException;
import xyz.binfish.logger.Logger;
import xyz.binfish.logger.LoggerConfig;

public class Main {

	public static void main(String[] args) throws IOException, LoginException {
		Options options = new Options();

		options.addOption(new Option("h", "help", false, "Displays this help menu."));
		options.addOption(new Option("v", "version", false, "Displays the current verison of the application."));
		options.addOption(new Option("sc", "shard-count", true, "Set the amount of shards the bot should start up."));
		options.addOption(new Option("s", "shards", true, "Set the shard IDs that should be started up, the shard IDs should be formatted by the lowest shard ID to start up, and the highest shard ID to start up, separated by a dash.\nExample: \"--shards=4-9\" would start up shard 4, 5, 6, 7, 8, and 9."));
		options.addOption(new Option("nocolor", "no-colors", false, "Disable colors for logger in the terminal."));
		options.addOption(new Option("d", "debug", false, "Enable debugging mode, this will log extra information to the terminal."));

		CommandLineParser parser = new CommandLineParser();
		HelpFormatter formatter = new HelpFormatter();

		try {
			CommandLine cmd = parser.parse(options, args);
			Settings settings = new Settings(cmd, args);

			if(cmd.hasOption("help")) {
				formatter.printHelp(options);
				System.exit(0);
			} else if(cmd.hasOption("version")) {
				System.out.println("Misa version " + AppInfo.getAppInfo().version);
				System.exit(0);
			}

			// First configuration initialization.
			Configuration config = Configuration.getInstance();

			// Logger creation.
			String pathToLogDirectory = config.get("defaultLogDirectory", "logs/");

			File logDirectory = new File(pathToLogDirectory);
			if(!logDirectory.exists() && !logDirectory.mkdirs()) {
				throw new IOException(
						String.format("Failed to create directory for logs files by path: %s", pathToLogDirectory)
				);
			}

			Logger logger = Logger.createLogger(
					new LoggerConfig(
						(new File(pathToLogDirectory).exists()
							? pathToLogDirectory
							: null
						)
					).setUseColors((settings.useColors() ? true : false))
			);

			new Misa(settings, config);
		} catch(ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp(options);

			System.exit(0);
		}
	}
}
