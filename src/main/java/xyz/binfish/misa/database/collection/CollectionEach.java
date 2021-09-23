package xyz.binfish.misa.database.collection;

public interface CollectionEach {
	
	/*
	 * This is called by the Collection#each method, used
	 * to loops through every entity in the Collection and
	 * parses the key and DataRow object to the consumer.
	 *
	 * @param key   the key for the element.
	 * @param value the data row linked to the key.
	 */
	void forEach(int key, DataRow value);
}
