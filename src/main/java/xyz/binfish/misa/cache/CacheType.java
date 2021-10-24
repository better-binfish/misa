package xyz.binfish.misa.cache;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;

import xyz.binfish.misa.cache.adapters.FileAdapter;
import xyz.binfish.misa.cache.adapters.MemoryAdapter;

public enum CacheType {

	/*
	 * Represents a file cache type, can be used to store
	 * things that are persisted throughout restarts and
	 * for very long periods of time in general.
	 */
	FILE("File", false, FileAdapter.class),

	/*
	 * Represents a memory cache type, can be used to store
	 * things directly in the memory, is great for storing
	 * something that may have to be accessed a lot,
	 * or things that has a short lifespan.
	 */
	MEMORY("Memory", true, MemoryAdapter.class);

	private static final EnumMap<CacheType, CacheAdapter> instances = new EnumMap<>(CacheType.class);

	static {
		for(CacheType type : values()) {
			try {
				instances.put(type, type.getClassInstance().getDeclaredConstructor().newInstance());
			} catch(InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				System.out.printf("Invalid cache type given: %s", e.getMessage());
				System.exit(1);
			}
		}
	}

	private final String name;
	private final boolean isDefault;
	private final Class<? extends CacheAdapter> instance;

	CacheType(String name, boolean isDefault, Class<? extends CacheAdapter> instance) {
		this.name = name;
		this.isDefault = isDefault;
		this.instance = instance;
	}

	/*
	 * Get the default cache type.
	 *
	 * @return the default cache type.
	 */
	public static CacheType getDefault() {
		return MEMORY;
	}

	/*
	 * Get the cache type by name.
	 *
	 * @param name the name of the cache type that should be returned.
	 * @return possibly null, the cache type with the given name.
	 */
	@Nullable
	public static CacheType fromName(String name) {
		for(CacheType type : values()) {
			if(type.getName().equalsIgnoreCase(name)) {
				return type;
			}
		}

		return null;
	}

	/*
	 * Get the name of the cache type.
	 *
	 * @return the name of the cache type.
	 */
	public String getName() {
		return name;
	}

	/*
	 * Check if the current cache type is the default cache type.
	 *
	 * @return true if the cache type is the default cache type, or false otherwise.
	 */
	public boolean isDefault() {
		return isDefault;
	}

	/*
	 * Get the cache adapter class instance.
	 *
	 * @return the cache adapter class instance.
	 */
	public Class<? extends CacheAdapter> getClassInstance() {
		return instance;
	}

	/*
	 * Get the adapter instance for the current cache type.
	 * 
	 * @return the adapter instance for the current cache type.
	 */
	public CacheAdapter getAdapter() {
		return instances.get(this);
	}
}
