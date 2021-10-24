package xyz.binfish.misa;

import java.io.File;

public class Constants {

	public static final File CACHE_STORAGE_PATH = new File("storage");

	// Database tables
	public static final String GUILD_TABLE_NAME = "guilds";
	public static final String FEEDBACK_TABLE_NAME = "feedback";

	// Package Specific Information
	public static final String PACKAGE_MODULES = "xyz.binfish.misa.modules";
	public static final String PACKAGE_MIGRATION = "xyz.binfish.misa.database.schema.migrations";
	public static final String PACKAGE_JOBS = "xyz.binfish.misa.scheduler.jobs";

	// Emojis

	// Links
	public static final String SUPPORT_SERVER = "https://discord.gg/eVCGeCP";
}
