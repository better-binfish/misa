package xyz.binfish.misa.database.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Collections;
import java.util.NoSuchElementException;

import java.math.BigDecimal;
import java.security.SecureRandom;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import xyz.binfish.misa.util.RandomUtil;

public class Collection implements Cloneable, Iterable<DataRow> {

	/*
	 * The empty collection (immutable).
	 */
	public static final Collection EMPTY_COLLECTION = new Collection(
			new HashMap<>(),
			new ArrayList<>()
	);

	private final HashMap<String, String> keys;
	private final List<DataRow> items;

	/*
	 * Create an empty collection.
	 */
	public Collection() {
		this.keys = new HashMap<>();
		this.items = new ArrayList<>();
	}

	/*
	 * Create new collection with the given keys and items instances.
	 *
	 * @param keys  the keys map that should be used for the collection.
	 * @param items the list of items that should be stored in the collection.
	 */
	private Collection(HashMap<String, String> keys, List<DataRow> items) {
		this.keys = keys;
		this.items = items;
	}

	/*
	 * Create new Collection object from the provided collection
	 * instance, this is the same as calling the copy method.
	 *
	 * @param instance the collection to copy.
	 */
	public Collection(@Nonnull Collection instance) {
		this.keys = new HashMap<>();
		this.items = new ArrayList<>();

		for(DataRow row : instance.all()) {
			items.add(new DataRow(row));
		}
	}

	/*
	 * Create new Collection object from a multidimensional map.
	 *
	 * @param items the map of items to create the collection from.
	 */
	public Collection(@Nonnull List<Map<String, Object>> items) {
		this.keys = new HashMap<>();
		this.items = new ArrayList<>();

		for(Map<String, Object> row : items) {
			row.keySet().stream().filter((key) -> (!keys.containsKey(key))).forEach((key) -> {
				keys.put(key, row.get(key).getClass().getTypeName());
			});

			this.items.add(new DataRow(row));
		}
	}

	/*
	 * Create new Collection instance, allowing you to the loop
	 * and fetch data from a ResultSet object a lot easier.
	 *
	 * @param result the ResultSet to generate the collection from.
	 * @throws SQLException if a database access error occurs.
	 */
	public Collection(@Nullable ResultSet result) throws SQLException {
		this.keys = new HashMap<>();
		this.items = new ArrayList<>();

		if(result == null) {
			return;
		}

		ResultSetMetaData meta = result.getMetaData();
		for(int i = 1; i <= meta.getColumnCount(); i++) {
			keys.put(meta.getColumnLabel(i), meta.getColumnClassName(i));
		}

		while(result.next()) {
			Map<String, Object> array = new HashMap<>();

			for(String key : keys.keySet()) {
				array.put(key, result.getString(key));
			}

			items.add(new DataRow(array));
		}

		if(!result.isClosed()) {
			result.close();
		}
	}

	/*
	 * Get all the DataRow items from the collection.
	 *
	 * @return all the DataRow items from the collection.
	 */
	public List<DataRow> all() {
		return items;
	}

	/*
	 * Calculates the average for the first key field.
	 *
	 * @return the average for the first key field.
	 */
	public double avg() {
		return avg(keys.keySet().iterator().next());
	}

	/*
	 * Calculates the average for a field.
	 *
	 * @param field the field to calculated the average of.
	 * @return the average for the provided field.
	 */
	public double avg(String field) {
		if(isEmpty() || !keys.containsKey(field)) {
			return 0;
		}

		BigDecimal decimal = new BigDecimal(0);

		for(DataRow row : items) {
			Object obj = row.get(field);

			switch(obj.getClass().getTypeName()) {
				case "java.lang.Double":
					decimal = decimal.add(new BigDecimal((Double) obj));
					break;
				case "java.lang.Long":
					decimal = decimal.add(new BigDecimal((Long) obj));
					break;
				case "java.lang.Integer":
					decimal = decimal.add(new BigDecimal((Integer) obj));
					break;
				case "java.lang.Float":
					decimal = decimal.add(new BigDecimal((Float) obj));
					break;
			}
		}

		return decimal.divide(new BigDecimal(items.size())).doubleValue();
	}

	/*
	 * Breaks the collection into multiple, smaller lists of the given size.
	 *
	 * @param size the size to chunk the collection down to.
	 * @return the chunked down collection.
	 */
	public List<Collection> chunk(int size) {
		List<Collection> chunk = new ArrayList<>();

		int index = 0, counter = 0;
		for(DataRow row : items) {
			if(counter++ >= size) {
				index++;
				counter = 0;
			}

			try {
				Collection get = chunk.get(index);

				get.add(row);
			} catch(IndexOutOfBoundsException e) {
				Collection collection = new Collection();

				collection.add(row);

				chunk.add(index, collection);
			}
		}

		return chunk;
	}

	/*
	 * Chunks every value stored in the collection and compares
	 * it to see if it matches the provided item.
	 *
	 * @param item the item to compare the collection with.
	 * @return true if this collection contains the provided elements.
	 */
	public boolean contains(Object item) {
		return items.stream().anyMatch((row)
				-> (row.keySet().stream().anyMatch((key)
				-> (row.get(key).equals(item)))));
	}

	/*
	 * Create copy of the current collections instance.
	 *
	 * @return the new collection.
	 */
	public Collection copy() {
		return new Collection(this);
	}

	/*
	 * Loops through every entity in the Collection and parses the key and
	 * DataRow object to the consumer.
	 *
	 * @param comparator the collection consumer to use.
	 * @return the collection instance.
	 */
	public Collection each(CollectionEach comparator) {
		ListIterator<DataRow> iterator = items.listIterator();

		while(iterator.hasNext()) {
			comparator.forEach(iterator.nextIndex(), iterator.next());
		}

		return this;
	}

	/*
	 * Get the first index of the collection.
	 *
	 * @return the first DataRow object, generated from the ResultSet object,
	 * or NULL if the collection doesn't have any items.
	 */
	public DataRow first() {
		if(items.isEmpty()) {
			return null;
		}

		return items.get(0);
	}

	/*
	 * Get the result of the provided index.
	 *
	 * @param index the index to get from the collection.
	 * @return the DataRow object in the provided index.
	 * @throws IndexOutOfBoundsException if the index is out of range.
	 */
	public DataRow get(int index) throws IndexOutOfBoundsException {
		return items.get(index);
	}

	/*
	 * Get all the keys from the ResultSet object in the form of a
	 * HashMap, where the key is the database table column
	 * name, and the value is the database column type.
	 *
	 * @return a map of all the database keys.
	 */
	public HashMap<String, String> getKeys() {
		return keys;
	}

	/*
	 * Get all the data row items.
	 *
	 * @return the data row items.
	 */
	public List<DataRow> getItems() {
		return items;
	}

	/*
	 * Checks to see the collection contains the provided field.
	 *
	 * @param field the field to check if exists.
	 * @return true if the field exists in the collection, false if it doesn't.
	 */
	public boolean has(String field) {
		return keys.containsKey(field);
	}

	/*
	 * Return ture if this collection contains no elements.
	 *
	 * @return true if this collection contains no elements.
	 */
	public boolean isEmpty() {
		return items.isEmpty();
	}

	/*
	 * Get the last DataRow of the collection.
	 *
	 * @return the last DataRow of the collection.
	 */
	public DataRow last() {
		if(isEmpty()) {
			return null;
		}

		return items.get(items.size() - 1);
	}

	/*
	 * Get the max/highest integer value from the provided field.
	 *
	 * @param field the field to use.
	 * @return the highest value from the provided field or Integer.MIN_VALUE.
	 */
	public int maxInt(String field) {
		if(!has(field)) {
			return Integer.MIN_VALUE;
		}

		int max = Integer.MIN_VALUE;
		for(DataRow row : items) {
			int x = row.getInt(field);

			if(max < x) {
				max = x;
			}
		}

		return max;
	}

	/*
	 * Get the max/highest long value from the provided field.
	 *
	 * @param field the field to use.
	 * @return the highest value from the provided field or Long.MIN_VALUE.
	 */
	public long maxLong(String field) {
		if(!has(field)) {
			return Long.MIN_VALUE;
		}

		long max = Long.MIN_VALUE;
		for(DataRow row : items) {
			long x = row.getLong(field);

			if(max < x) {
				max = x;
			}
		}

		return max;
	}

	/*
	 * Get the max/highest double value from the provided field.
	 *
	 * @param field the field to use.
	 * @return the highest value from the provided field or Double.MIN_VALUE.
	 */
	public double maxDouble(String field) {
		if(!has(field)) {
			return Double.MIN_VALUE;
		}

		double max = Double.MIN_VALUE;
		for(DataRow row : items) {
			double x = row.getDouble(field);

			if(max < x) {
				max = x;
			}
		}

		return max;
	}

	/*
	 * Get the max/highest float value from the provided field.
	 *
	 * @param field the field to use.
	 * @return the highest value from the provided field or Float.MIN_VALUE.
	 */
	public float maxFloat(String field) {
		if(!has(field)) {
			return Float.MIN_VALUE;
		}

		float max = Float.MIN_VALUE;
		for(DataRow row : items) {
			float x = row.getFloat(field);

			if(max < x) {
				max = x;
			}
		}

		return max;
	}

	/*
	 * Get the min/lowest integer value from the provided field.
	 *
	 * @param field the field to use.
	 * @return tho lowest value from the provided field or Integer.MAX_VALUE.
	 */
	public int minInt(String field) {
		if(!has(field)) {
			return Integer.MAX_VALUE;
		}

		int min = Integer.MAX_VALUE;
		for(DataRow row : items) {
			int x = row.getInt(field);

			if(min > x) {
				min = x;
			}
		}

		return min;
	}

	/*
	 * Get the min/lowest long value from the provided field.
	 *
	 * @param field the field to use.
	 * @return the lowest value from the provided field or Long.MAX_VALUE.
	 */
	public long minLong(String field) {
		if(!has(field)) {
			return Long.MAX_VALUE;
		}

		long min = Long.MAX_VALUE;
		for(DataRow row : items) {
			long x = row.getLong(field);

			if(min > x) {
				min = x;
			}
		}

		return min;
	}

	/*
	 * Get the min/lowest long value from the provided field.
	 *
	 * @param field the field to use.
	 * @return the lowest value from the provided field or Double.MAX_VALUE.
	 */
	public double minDouble(String field) {
		if(!has(field)) {
			return Double.MAX_VALUE;
		}

		double min = Double.MAX_VALUE;
		for(DataRow row : items) {
			double x = row.getDouble(field);

			if(min > x) {
				min = x;
			}
		}

		return min;
	}

	/*
	 * Get the min/lowest float value from the provided field.
	 *
	 * @param field the field to use.
	 * @return the lowest value from the provided field or Float.MAX_VALUE.
	 */
	public float minFloat(String field) {
		if(!has(field)) {
			return Float.MAX_VALUE;
		}

		float min = Float.MAX_VALUE;
		for(DataRow row : items) {
			float x = row.getFloat(field);

			if(min > x) {
				min = x;
			}
		}

		return min;
	}

	/*
	 * Removes and returns the last item of the collection.
	 *
	 * @return the last item of the collection.
	 */
	public DataRow pop() {
		if(isEmpty()) {
			return null;
		}

		return items.remove(items.size() - 1);
	}

	/*
	 * Gets a RANDOM item from the collection.
	 *
	 * @return a RANDOM item from the collection.
	 */
	public DataRow random() {
		if(isEmpty()) {
			return null;
		}

		return items.get(RandomUtil.getInteger(items.size()));
	}

	/*
	 * Reverses the order of items in the collection.
	 *
	 * @return the reversed collection.
	 */
	public Collection reverse() {
		Collections.reverse(items);

		return this;
	}

	/*
	 * Search the collection where the field is equal to the value.
	 *
	 * @param field the field to check.
	 * @param value the value to use.
	 * @return the index of the item that matches the search or -1.
	 */
	public int search(String field, Object value) {
		if(isEmpty() || !has(field)) {
			return -1;
		}

		String rValue = value.toString();

		for(int index = 0; index < items.size(); index++) {
			DataRow row = get(index);

			if(row.getString(field).equals(rValue)) {
				return index;
			}
		}

		return -1;
	}

	/*
	 * Removes and returns the first element in the collection.
	 *
	 * @return the first element in the collection.
	 */
	public DataRow shift() {
		try {
			return items.remove(0);
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}

	/*
	 * Randomly shuffles the items in the collection.
	 *
	 * @return the newly shuffled collection.
	 */
	public Collection shuffle() {
		Collections.shuffle(items, new SecureRandom());

		return this;
	}

	/*
	 * Sorts the collection according to the order induced by the specified
	 * comparator. All elements in the list must be mutually comparable
	 * using the specified comparator.
	 *
	 * @param comparator the comparator to use to sort the collection.
	 * @return the collection instance.
	 */
	public Collection sort(Comparator<DataRow> comparator) {
		items.sort(comparator);

		return this;
	}
	
	/*
	 * Sorts the collection in an ascending order using the provided
	 * field, if the field is not found the collection will be returned
	 * in its current state.
	 *
	 * @param field the field that should be used to sort the collection.
	 * @return the collection instance.
	 */
	public Collection sortBy(String field) {
		if(!has(field)) {
			return this;
		}

		return sort(Comparator.comparingInt(row -> row.get(field).hashCode()));
	}

	/*
	 * Sorts the collection in an descending order using the provided field, if
	 * the field is not found the collection will be returned in its current state.
	 *
	 * @param field the field that should be used to sort the collection.
	 * @return the collection instance.
	 */
	public Collection sortByDesc(String field) {
		if(!has(field)) {
			return this;
		}

		return sort((DataRow first, DataRow second) -> second.get(field).hashCode() - first.get(field).hashCode());
	}

	/*
	 * Get the total number of items in the collection.
	 *
	 * @return the total number of items in the collection.
	 */
	public int size() {
		return items.size();
	}

	/*
	 * Calculates the sum of a list of integers.
	 *
	 * @param field the field to calculated the sum of.
	 * @return the sum for the provided field.
	 */
	public long sumInt(String field) {
		long sum = 0;

		if(!has(field)) {
			return sum;
		}

		for(DataRow row : items) {
			sum += row.getInt(field);
		}

		return sum;
	}

	/*
	 * Takes the provided number of items from the collection and returns a new collection.
	 *
	 * @param amount the amount of items to take from the original collection.
	 * @return a new collection with the provided number of items from the original collection.
	 */
	public Collection take(int amount) {
		Collection collection = new Collection();
		Iterator<DataRow> iterator = items.iterator();

		int index = 0;
		while(iterator.hasNext()) {
			DataRow next = iterator.next();

			if(index++ >= amount) {
				break;
			}

			collection.add(new DataRow(next));
			iterator.remove();
		}

		return collection;
	}

	/*
	 * Get all the data rows where the field equals the value, this uses strict
	 * comparisons to match the values, use whereLoose method to filter using "loose" comparisons.
	 */
	public List<DataRow> where(String field, Object value) {
		if(isEmpty() || !has(field)) {
			return new ArrayList<>();
		}

		String rValue = value.toString();
		List<DataRow> rows = new ArrayList<>();

		items.stream()
			.filter((row) -> (row.getString(field).equals(rValue)))
			.forEach(rows::add);

		return rows;
	}

	/*
	 * Get all the data rows where the field equals the value, this uses a loose
	 * comparisons to match the values, use the where method to filter using "strict" comparisons.
	 */
	public List<DataRow> whereLoose(String field, Object value) {
		if(isEmpty() || !has(field)) {
			return new ArrayList<>();
		}

		String rValue = value.toString();
		List<DataRow> rows = new ArrayList<>();

		items.stream()
			.filter((row) -> (row.getString(field).equalsIgnoreCase(rValue)))
			.forEach(rows::add);

		return rows;
	}

	@Nonnull
	@Override
	public Iterator<DataRow> iterator() {
		return new CollectionIterator();
	}

	private void add(DataRow row) {
		this.items.add(new DataRow(row));
	}

	private class CollectionIterator implements Iterator<DataRow> {

		private int cursor = 0;

		@Override
		public boolean hasNext() {
			return cursor < Collection.this.items.size();
		}

		@Override
		public DataRow next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}

			return Collection.this.items.get(cursor++);
		}
	}
}
