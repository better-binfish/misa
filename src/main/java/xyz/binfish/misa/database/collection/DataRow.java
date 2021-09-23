package xyz.binfish.misa.database.collection;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Base64;
import java.util.Date;

import xyz.binfish.misa.util.NumberUtil;
import xyz.binfish.misa.exceptions.InvalidFormatException;

public class DataRow {

	private final Map<String, Object> items;
	private final Map<String, String> decodedItems;

	/*
	 * Create new data row object from the provided data row.
	 *
	 * @param row the row to generate the data row from.
	 */
	public DataRow(DataRow row) {
		this(row.items);
	}

	/*
	 * Create new data row object from map of data.
	 *
	 * @pram items the map to generate the data row from.
	 */
	public DataRow(Map<String, Object> items) {
		this.items = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		this.decodedItems = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		for(Map.Entry<String, Object> item : items.entrySet()) {
			this.items.put(item.getKey(), item.getValue());
		}
	}

	/*
	 * Get object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @return the value of the index given, or NULL if the index doesn't exists.
	 */
	public Object get(String name) {
		return get(name, null);
	}

	/*
	 * Get object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @param def  the default vault to return if the index doesn't exists.
	 * @return the value of the index given, or the default value given.
	 */
	public Object get(String name, Object def) {
		if(has(name)) {
			return items.get(name);
		}

		return def;
	}

	/*
	 * Get boolean object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @return the value of the index given, or FALSE if the index doesn't exists.
	 */
	public boolean getBoolean(String name) {
		return getBoolean(name, false);
	}

	/*
	 * Get boolean object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @param def  the default vault to retrun if the index doesn't exists.
	 * @return the value of the index given, or the default value given.
	 */
	public boolean getBoolean(String name, boolean def) {
		Object value = get(name, def);

		if(isNull(value)) {
			return def;
		}

		if(isString(value)) {
			String str = String.valueOf(value);

			return isEqual(str, "1", "true");
		}

		return (boolean) value;
	}

	/*
	 * Get double object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @return the value of the index given, or 0.0D if the index doesn't exists.
	 */
	public double getDouble(String name) {
		return getDouble(name, 0.0D);
	}

	/*
	 * Get double object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @param def  the default vault to return if the index doesn't exists.
	 * @return the value of the index given, or the default value given.
	 */
	public double getDouble(String name, double def) {
		Object value = get(name, def);

		if(isNull(value)) {
			return def;
		}

		if(isString(value)) {
			String str = String.valueOf(value);

			try {
				return Double.parseDouble(str);
			} catch(NumberFormatException e) {
				return def;
			}
		}

		switch(getType(value)) {
			case "Integer":
				value = ((Integer) value).doubleValue();
				break;
			case "Long":
				value = ((Long) value).doubleValue();
				break;
			case "Float":
				value = ((Float) value).doubleValue();
				break;
		}

		try {
			return (double) value;
		} catch(ClassCastException e) {
			return def;
		}
	}

	/*
	 * Get integer object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @return the value of the index given, or 0 if the index doesn't exists.
	 */
	public int getInt(String name) {
		return getInt(name, 0);
	}

	/*
	 * Get integer object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @param def  the default vault to return if the index doesn't exists.
	 * @return the value of the index given, or the default value given.
	 */
	public int getInt(String name, int def) {
		Object value = get(name, def);

		if(isNull(value)) {
			return def;
		}

		if(isString(value)) {
			String str = String.valueOf(value);

			return NumberUtil.parseInt(str, def);
		}

		switch(getType(value)) {
			case "Double":
				value = ((Double) value).intValue();
				break;
			case "Long":
				value = ((Long) value).intValue();
				break;
			case "Float":
				value = ((Float) value).intValue();
				break;
		}

		try {
			return (int) value;
		} catch(ClassCastException e) {
			return def;
		}
	}

	/*
	 * Get long object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @return the value of the index given, or 0L if the index doesn't exists.
	 */
	public long getLong(String name) {
		return getLong(name, 0L);
	}

	/*
	 * Get long object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @param def  the default vault to return if the index doesn't exists.
	 * @return the value of the index given, or the default value given.
	 */
	public long getLong(String name, long def) {
		Object value = get(name, def);

		if(isNull(value)) {
			return def;
		}

		if(isString(value)) {
			String str = String.valueOf(value);

			try {
				return Long.parseLong(str);
			} catch(NumberFormatException e) {
				return def;
			}
		}

		switch(getType(value)) {
			case "Double":
				value = ((Double) value).longValue();
				break;
			case "Integer":
				value = ((Integer) value).longValue();
				break;
			case "Float":
				value = ((Float) value).longValue();
				break;
		}

		try {
			return (long) value;
		} catch(ClassCastException e) {
			return def;
		}
	}

	/*
	 * Get float object from data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @return the value of the index given, or 0F if the index doesn't exists.
	 */
	public float getFloat(String name) {
		return getFloat(name, 0F);
	}

	/*
	 * Get float object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @param def the default vault to return if the index doesn't exists.
	 * @return the value of the index given, or the default value given.
	 */
	public float getFloat(String name, float def) {
		Object value = get(name, def);

		if(isNull(value)) {
			return def;
		}

		if(isString(value)) {
			String str = String.valueOf(value);

			try {
				return Float.parseFloat(str);
			} catch(NumberFormatException e) {
				return def;
			}
		}

		switch(getType(value)) {
			case "Double":
				value = ((Double) value).floatValue();
				break;
			case "Integer":
				value = ((Integer) value).floatValue();
				break;
			case "Long":
				value = ((Long) value).floatValue();
				break;
		}

		try {
			return (float) value;
		} catch(ClassCastException e) {
			return def;
		}
	}

	/*
	 * Get string object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @return the value of the index given, or NULL if the index doesn't exists.
	 */
	public String getString(String name) {
		return getString(name, null);
	}

	/*
	 * Get string object from the data rows item list, if the string is
	 * encoded with base64 it will automatically be decoded on request.
	 *
	 * @param name the index (name) to get.
	 * @param def  the default vault to return if the index doesn't exists.
	 * @return the value of the index given, or the default value given.
	 */
	public String getString(String name, String def) {
		Object value = get(name, def);

		if(isNull(value)) {
			return def;
		}

		String string = String.valueOf(value);
		if(!string.startsWith("base64:")) {
			return string;
		}

		if(decodedItems.containsKey(name)) {
			return decodedItems.get(name);
		}

		try {
			String decodedString = new String(Base64.getDecoder().decode(
				string.substring(7)
			));

			decodedItems.put(name, decodedString);

			return decodedString;
		} catch(IllegalArgumentException e) {
			return string;	
		}
	}

	/*
	 * Get date timestamp object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @return the value of the index given, or NULL if the index doesn't exists.
	 */
	public Date getTimestamp(String name) {
		return getTimestamp(name, null);
	}

	/*
	 * Get date timestamp object from the data rows item list.
	 *
	 * @param name the index (name) to get.
	 * @param def  the default vault to return if the index doesn't exists.
	 * @return the value of the index given, or default value given.
	 */
	public Date getTimestamp(String name, Date def) {
		try {
			String time = getString(name);

			if(time == null) {
				return null;
			}

			return new Date(time);
		} catch(InvalidFormatException e) {
			return def;
		}
	}

	/*
	 * Check to see if the given index exists in the data rows list of items.
	 *
	 * @param name the index (name) to check if exists.
	 * @return true if the index exists, otherwise it will return false.
	 */
	public boolean has(String name) {
		return items.containsKey(name);
	}

	/*
	 * Get all the keys from the data row.
	 *
	 * @return all the keys from the data row.
	 */
	public Set<String> keySet() {
		return items.keySet();
	}

	/*
	 * Get the raw map object for the data row.
	 *
	 * @return the raw data of the data row.
	 */
	public Map<String, Object> getRaw() {
		return items;
	}

	private boolean isString(Object name) {
		return getType(name).equalsIgnoreCase("string");
	}

	private boolean isNull(Object object) {
		return object == null || object == "null";
	}

	@Nonnull
	private String getType(Object name) {
		return name == null ? "unknown-type" : name.getClass().getSimpleName();
	}

	private boolean isEqual(String name, String... items) {
		for(String item : items) {
			if(name.equalsIgnoreCase(item)) {
				return true;
			}
		}

		return false;
	}
}
