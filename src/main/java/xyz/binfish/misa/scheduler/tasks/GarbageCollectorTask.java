package xyz.binfish.misa.scheduler.tasks;

import xyz.binfish.misa.cache.adapters.MemoryAdapter;
import xyz.binfish.misa.cache.CacheType;
import xyz.binfish.misa.scheduler.Task;
import xyz.binfish.misa.Misa;

public class GarbageCollectorTask implements Task {

	@Override
	public void handle() {
		// Remove cache entries from the memory cache adapter
		// if the keys are still stored by has expired.
		MemoryAdapter adapter = (MemoryAdapter) Misa.getCache().getAdapter(CacheType.MEMORY);
		adapter.getCacheKeys().removeIf(key -> !adapter.has(key));
	}
}
