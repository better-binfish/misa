package xyz.binfish.misa;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;

public class Configuration {

	private static Configuration instance;
	private Properties properties = new Properties();

	private Configuration() {
		try {
			File config = new File("misa.properties");

			if(config.exists() && config.isFile()) {
				InputStream is = new FileInputStream(config);
				properties.load(is);
				is.close();
			} else {
				throw new RuntimeException("Could not find the configuration file.");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private <T> T getValue(String key, T defaultValue, Class<T> type) {
		T value = defaultValue;
		String prop = properties.getProperty(key);

		if(prop != null) {
			try {
				value = type.getConstructor(String.class).newInstance(prop);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		return value;
	}

	public Boolean is(String key, Boolean defaultValue) {
		return getValue(key, defaultValue, Boolean.class);
	}

	public Integer getInteger(String key, Integer defaultValue) {
		return getValue(key, defaultValue, Integer.class);
	}

	public String get(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public String[] getArray(String key, String[] defaultValue) {
		String prop = properties.getProperty(key);
		return (prop != null ? prop.split(",") : defaultValue);
	}

	public static synchronized Configuration getInstance() {
		if(instance == null) {
			instance = new Configuration();
		}

		return instance;
	}
}
