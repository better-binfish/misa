package xyz.binfish.misa.cache;

import java.util.function.Supplier;

public abstract class CacheAdapter {

	/*
	 * Store item in the cache for a given number of seconds.
	 *
	 * @param token   the cache item token.
	 * @param value   the item that should be stored in the cache.
	 * @param seconds the amount of seconds the item should be stored for.
	 * @return true if the cache was save correctly, false otherwise.
	 */
	public abstract boolean put(String token, Object value, int seconds);

	/*
	 * Get item from the cache, or store the default value.
	 *
	 * @param token   the cache item token.
	 * @param value   the item that should be stored in the cache.
	 * @param seconds the amount of seconds the item should be stored for.
	 * @return true if the cache was save correctly, false otherwise.
	 */
	public abstract Object remember(String token, int seconds, Supplier<Object> closure);

	/*
	 * Store item in the cache indefinitely.
	 *
	 * @param token the cache item token.
	 * @param value the item that should be stored in the cache.
	 * @return true if the cache was save correctly, false otherwise.
	 */
	public abstract boolean forever(String token, Object value);

	/*
	 * Retrieve item from the cache by key.
	 *
	 * @param token the cache item token.
	 * @return the result of the cache token if it exists, null otherwise.
	 */
	public abstract Object get(String token);

	/*
	 * Retrieve item from the cache in raw form by key.
	 *
	 * @param token the cache item token.
	 * @return the raw cache item object.
	 */
	public abstract CacheItem getRaw(String token);

	/*
	 * Determine if item exists in the cache.
	 *
	 * @param token the cache item token.
	 * @return true if the cache exits, false otherwise.
	 */
	public abstract boolean has(String token);

	/*
	 * Remove item from the cache.
	 *
	 * @param token the cache item token.
	 * @return true if the cache was forgotten corretly, false otherwise.
	 */
	public abstract CacheItem forget(String token);

	/*
	 * Remove all items from the cache.
	 *
	 * @return true if the cache was emptied, false otherwise.
	 */
	public abstract boolean flush();
}
