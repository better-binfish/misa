package xyz.binfish.misa.locale;

import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;

import java.text.MessageFormat;

import xyz.binfish.misa.util.FileResourcesUtil;
import xyz.binfish.misa.util.ComparatorUtil;
import xyz.binfish.misa.util.NumberUtil;
import xyz.binfish.logger.Logger;

public class JsonReaderAdapter {

	private String path;

	private static Logger logger = Logger.getLogger();

	/*
	 * Create a json reader adapter instance.
	 *
	 * @param path the to file path to read.
	 */
	public JsonReaderAdapter(String path) {
		this.path = path;
	}

	/*
	 * Get a string value along the path to the key from a json file, with
	 * the formatting of the passed arguments to a string. If the path is NULL
	 * then the method returns NULL.
	 *
	 * @param path the path to the required key, the value of which you want to get.
	 * @param args the arguments that should be formatted for the given language string.
	 * @return the formatted string value of key.
	 */
	public String getString(String path, Object... args) {
		String message = getString(path);

		if(message == null) {
			return null;
		}

		return format(message, args);
	}

	/*
	 * Get a string value along the path to the key from a json file. If the
	 * path is NULL then the method returns NULL.
	 *
	 * @param path the path to the required key, the value of which you want to get.
	 * @return the string value of key.
	 */
	public String getString(String path) {
		if(path == null) {
			return null;
		}

		return get(path);
	}

	/*
	 * Get a boolean value along the path to the key from a json file. If the
	 * path or value is null then the method returns null.
	 *
	 * @param path the path to the required key, the value of which you want to get.
	 * @return the desired boolean value, or null if value not found.
	 */
	public Boolean getBoolean(String path) {
		if(path == null) {
			return null;
		}

		String value = get(path);
		if(value == null) {
			return null;
		}

		return ComparatorUtil.getFuzzyType(value);
	}

	/*
	 * Get a int value along the path to the key from a json file. If the
	 * path or value is null then the method returns null.
	 *
	 * @param path the path to the required key, the value of which you want to get.
	 * @return the desired integer value, or null if value not found.
	 */
	public Integer getInt(String path) {
		if(path == null) {
			return null;
		}

		String value = get(path);
		if(value == null) {
			return null;
		}

		return NumberUtil.parseInt(value);
	}

	/*
	 * Get string value by path from json file. Returns NULL on failure.
	 *
	 * @param path path the path to the required key, the value of which you want to get.
	 * @return the string value of key.
	 */
	public String get(String path) {
		JsonReader jsonReader = Json.createReader(FileResourcesUtil
				.getFileFromResourceAsStream(this.path));
		JsonObject jo = jsonReader.readObject();
		                jsonReader.close();

		String[] pathParts = path.split("\\.");

		for(int i = 0; i < pathParts.length - 1; i++) {
			jo = jo.getJsonObject(pathParts[i]);
		}

		try {
			return jo.get(pathParts[pathParts.length - 1]).toString();
		} catch(ClassCastException | NullPointerException e) {
			logger.error("Failed to get value from json file.\nBy path: " + path);
			return null;
		}
	}

	/*
	 * Formats the given string with the given arguments using the language formatting,
	 * each argument given will be replaced with {index number}, so the first argument
	 * will be replaced with any instances of {0}, the second will be replaced with any
	 * instances of {1}, etc.
	 *
	 * Every argument given can be replaced multiple times per string, the placement of the
	 * placeholders({0}, {1}, etc) doesn't matter either, giving developers free rein to
	 * format and structure their messages however they want to.
	 *
	 * @param message the message that should be formatted with the given arguments.
	 * @param args    the arguments that should be replaced in the given message.
	 * @return the formatted string, or the original string if the formatting process
	 * failed due to an invalid argument exception.
	 */
	public String format(@Nonnull String message, Object... args) {
		Object[] arguments = new Object[args.length];
		int num = 0;

		for(Object arg : args) {
			if(arg == null) {
				continue;
			}

			arguments[num++] = arg.toString();
		}

		try {
			return MessageFormat.format(
				message.replace("'", "''"), arguments
			);
		} catch(IllegalArgumentException e) {
			logger.error("An exception was thrown while formatting \"" + message
				+ "\", error: " + e.getMessage(), e
			);

			return message;
		}
	}
}
