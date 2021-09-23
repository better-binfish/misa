package xyz.binfish.misa.locale;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Guild;

import java.net.URL;
import java.io.IOException;

import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;
import javax.json.JsonException;

import xyz.binfish.misa.database.controllers.GuildController;
import xyz.binfish.misa.database.model.GuildModel;
import xyz.binfish.misa.util.FileResourcesUtil;
import xyz.binfish.logger.Logger;

public class LanguageManager {

	private static HashMap<String, LanguagePackage> languages = new HashMap<>();
	private static LanguagePackage defaultLanguage = null;
	private static Logger logger = Logger.getLogger();

	private LanguageManager() { }

	/*
	 * Produces reading resources in jar file, and the
	 * identification and loading all language packages, this
	 * method should be used only once.
	 */
	public static void up() {
		languages.clear();

		URL urlToJar = LanguageManager.class
			.getProtectionDomain()
			.getCodeSource()
			.getLocation();

		try {
			JarInputStream jar = new JarInputStream(urlToJar.openStream());
			JarEntry jarEntry = null;

			while((jarEntry = jar.getNextJarEntry()) != null) {
				String name = jarEntry.getName();

				if(name.startsWith("lang/")) {
					if(!name.substring(name.lastIndexOf(".") + 1, name.length()).equals("json")) {
						continue;
					}

					try {
						JsonReader jsonReader = Json.createReader(FileResourcesUtil
								.getFileFromResourceAsStream(name));

						JsonObject header = jsonReader.readObject().getJsonObject("header");

						//header.forEach((key, value) -> System.out.println(key + ":" + value));

						LanguagePackage langPackage = new LanguagePackage(
								header.getString("language"),
								header.getString("country"),
								header.getString("nativeName"),
								header.getString("englishName"));

						String currentLangCode = langPackage.getCode();

						if(languages.containsKey(currentLangCode)) {
							throw new RuntimeException("Can't load language package, the code '"
									+ currentLangCode + "' is already loaded");
						}

						if(langPackage.getCode().equals("en_US")) {
							defaultLanguage = langPackage;
						}

						languages.put(langPackage.getCode(), langPackage);

						logger.info("Loaded language package with code "
								+ langPackage.getCode());
					} catch(NullPointerException | JsonException e) {
						e.printStackTrace();
					}

				}
			}

			jar.close();
		} catch(IOException e) {
			e.printStackTrace();	
		}

		try {
			if(defaultLanguage == null) {
				throw new RuntimeException("Language package not loaded!");
			}
		} catch(RuntimeException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Get the default locale as LanguagePackage object.
	 *
	 * @return the LanguagePackage object.
	 */
	public static LanguagePackage getDefaultLocale() {
		return defaultLanguage;
	}

	/*
	 * Get the LanguagePackage object from given Message object.
	 *
	 * @param message the Message object from which should get the LanguagePackage object.
	 * @return the LanguagePackage object.
	 */
	public static LanguagePackage getLocale(Message message) {
		if(message == null) {
			return defaultLanguage;
		}

		return getLocale(GuildController.fetchGuild(message));
	}

	/*
	 * Get the LanguagePackage object from Guild object.
	 *
	 * @param guild the Guild object from which should get the LanguagePackage object.
	 * @return the LanguagePackage object.
	 */
	public static LanguagePackage getLocale(Guild guild) {
		if(guild == null) {
			return defaultLanguage;
		}

		return getLocale(GuildController.fetchGuild(guild));
	}

	/*
	 * Get the LanguagePackage object from the GuildModel received from the database.
	 *
	 * @param guildModel the GuildModel received from the database.
	 * @return the LanguagePackage object inherent given GuildModel.
	 */
	public static LanguagePackage getLocale(GuildModel guildModel) {
		try {
			if(guildModel != null) {
				String localeCode = guildModel.getLocale();

				if(languages.containsKey(localeCode)) {
					return languages.get(localeCode);
				}
			}
		} catch(Exception e) {
			logger.error("Failed to get locale from message. Error: " + e.getMessage());
		}

		return defaultLanguage;
	}

	/*
	 * Get a HashMap with all loaded language packages.
	 *
	 * @return the HashMap with all loaded LanguagePackage objects.
	 */
	public static HashMap<String, LanguagePackage> getLocales() {
		return languages;
	}

	/*
	 * Parse the given string, trying to match it with one of the languages.
	 *
	 * @param string the string representation of the that should be returned.
	 * @return the language matching the given string, or NULL if no languages matched
	 * the given string.
	 */
	public static LanguagePackage parse(String string) {
		if(string != null && string.length() > 0) {
			for(LanguagePackage language : languages.values()) {
				if(language.getEnglishName().equalsIgnoreCase(string)
						|| language.getNativeName().equalsIgnoreCase(string)
						|| language.getCode().equalsIgnoreCase(string)) {
					return language;
				}
			}
		}

		return null;
	}
}
