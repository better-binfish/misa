package xyz.binfish.misa.cache;

public class CacheItem {

	private final String key;
	private final Object value;
	private final long time;

	/*
	 * Create a new cache item entity with the given key, value, and expire time.
	 *
	 * @param key   the key that the cache item should be stored under.
	 * @param value the raw value that the cache item holds.
	 * @param time  the unix timestamp in milliseconds representing when the cache item expires.
	 */
	public CacheItem(String key, Object value, long time) {
		this.key = key;
		this.value = value;
		this.time = time;
	}

	/*
	 * Get key the cache item is stored under.
	 *
	 * @return the key the cache item is stored under.
	 */
	public String getKey() {
		return key;
	}

	/*
	 * Get the raw value of the cache item.
	 *
	 * @return the raw value of the cache item.
	 */
	public Object getValue() {
		return value;
	}

	/*
	 * Get the unix thimestamp in milliseconds for when the cache item
	 * should expire, if the cache item is set to last forever,
	 * the time will always be set to -1.
	 *
	 * @return the unix timestamp in milliseconds for when the cache item
	 * expires, or -1 if it is set to last forever.
	 */
	public long getTime() {
		return time;
	}

	/*
	 * Checks if the cache item has expired, if the cache item is set
	 * to last forever this will always return false.
	 */
	public boolean isExpired() {
		return !lastForever() && getTime() > System.currentTimeMillis();
	}

	/*
	 * Check if the cache item lasts forever.
	 *
	 * @return true if the cache item should last forever, false otherwise.
	 */
	public boolean lastForever() {
		return time == -1;
	}
}
