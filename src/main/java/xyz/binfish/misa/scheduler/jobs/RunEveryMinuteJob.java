package xyz.binfish.misa.scheduler.jobs;

import xyz.binfish.misa.scheduler.Job;
import xyz.binfish.misa.scheduler.tasks.GarbageCollectorTask;

public class RunEveryMinuteJob extends Job {

	private final GarbageCollectorTask garbageCollectorTask = new GarbageCollectorTask();

	public RunEveryMinuteJob() {
		super(0, 1);
	}

	@Override
	public void run() {
		handleTask(
				garbageCollectorTask
		);
	}
}
