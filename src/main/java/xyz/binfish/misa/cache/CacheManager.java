package xyz.binfish.misa.cache;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import xyz.binfish.misa.cache.CacheType;

public class CacheManager extends CacheAdapter {
	
	/*
	 * Create the new cache manager.
	 */
	public CacheManager() { }

	@Override
	public boolean put(String token, Object value, int seconds) {
		return getAdapter(null).put(token, value, seconds);
	}

	/*
	 * Store item in the cache for a given number of seconds.
	 *
	 * @param type    the cache type to store the value in.
	 * @param token   the cache item token.
	 * @param value   the item that should be stored in the cache.
	 * @param seconds the amount of seconds the item should be stored for.
	 * @return true if the cache was save correctly, false otherwise.
	 */
	public boolean put(CacheType type, String token, Object value, int seconds) {
		return getAdapter(type).put(token, value, seconds);
	}

	@Override
	public Object remember(String token, int seconds, Supplier<Object> closure) {
		return getAdapter(null).remember(token, seconds, closure);
	}

	/*
	 * Get item from the cache, or store the default value.
	 *
	 * @param type    the cache type to store the value in.
	 * @param token   the cache item token.
	 * @param seconds the amount of seconds the item should be stored for.
	 * @param closure the closure that should be invoked if the cache doesn't exists.
	 * @return the object that exists in the cache, if the cache token is empty
	 *         the result of the closure will be returned instead.
	 */
	public Object remember(CacheType type, String token, int seconds, Supplier<Object> closure) {
		return getAdapter(type).remember(token, seconds, closure);
	}

	@Override
	public boolean forever(String token, Object value) {
		return getAdapter(null).forever(token, value);
	}

	/*
	 * Store item in the cache indefinitely.
	 *
	 * @param type  the cache type to store the value in.
	 * @param token the cache item token.
	 * @param value the item that should be stored in the cache.
	 * @return true if the cache was save correctly, false otherwise.
	 */
	public boolean forever(CacheType type, String token, Object value) {
		return getAdapter(type).forever(token, value);
	}

	@Override
	public Object get(String token) {
		return getAdapter(null).get(token);
	}

	/*
	 * Retrieve item from the cache by key.
	 *
	 * @param type  the cache type to get the value from.
	 * @param token the cache item token.
	 * @return the result of the cache token if it exists, null otherwise.
	 */
	public Object get(CacheType type, String token) {
		return getAdapter(type).get(token);
	}

	@Override
	public CacheItem getRaw(String token) {
		return getAdapter(null).getRaw(token);
	}

	/*
	 * Retrieve item from the cache in raw from by key.
	 *
	 * @param type  the cache type to get the value from.
	 * @param token the cache item token.
	 * @return the raw cache item object.
	 */
	public CacheItem getRaw(CacheType type, String token) {
		return getAdapter(type).getRaw(token);
	}

	@Override
	public boolean has(String token) {
		return getAdapter(null).has(token);
	}

	/*
	 * Retrieve item from the cache in raw form by key.
	 *
	 * @param type  the cache type to get the value from.
	 * @param token the cache item token.
	 * @return the raw cache item object.
	 */
	public boolean has(CacheType type, String token) {
		return getAdapter(type).has(token);
	}

	@Override
	public CacheItem forget(String token) {
		return getAdapter(null).forget(token);
	}

	/*
	 * Remove item from the cache
	 *
	 * @param type  the cache type to get the value from.
	 * @param token the cache item token.
	 * @return ture if the cache was forgetten correctly, false otherwise.
	 */
	public CacheItem forget(CacheType type, String token) {
		return getAdapter(type).forget(token);
	}

	@Override
	public boolean flush() {
		return getAdapter(null).flush();
	}

	/*
	 * Remove all items from the given cache type cache.
	 *
	 * @param type the cache type should have all of its keys flushed.
	 * @return ture if the cache was emptied, false otherwise.
	 */
	public boolean flush(CacheType type) {
		return getAdapter(type).flush();
	}

	/*
	 * Gets the cache adapter for the given cache type.
	 *
	 * @param type the cache type that should be returned.
	 * @return the cache adapter matching the given cache type, or
	 * the default cache adapter of null is given.
	 */
	public CacheAdapter getAdapter(@Nullable CacheType type) {
		if(type != null) {
			return type.getAdapter();
		}

		return CacheType.getDefault().getAdapter();
	}
}
