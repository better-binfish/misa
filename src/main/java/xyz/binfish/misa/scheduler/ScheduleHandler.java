package xyz.binfish.misa.scheduler;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ScheduleHandler {

	private static final Set<ScheduledFuture<?>> tasks = new HashSet<>();
	private static final ScheduledExecutorService schedulerService = Executors.newScheduledThreadPool(5);

	/*
	 * Registers a job with the scheduler service, the job will define how often
	 * it should run, and the schedule service will then periodically run the
	 * job on a separate thread when it's time for it to run.
	 *
	 * @param job the job that should be registered with the scheduler service.
	 */
	public static void registerJob(@Nonnull Job job) {
		tasks.add(schedulerService.scheduleAtFixedRate(job, job.getDelay(), job.getPeriod(), job.getUnit()));
	}

	/*
	 * Get a set of scheduled future instances for jobs
	 * that are registred to the scheduler service.
	 * 
	 * @return a set of scheduled future instances for registered jobs.
	 */
	public static Set<ScheduledFuture<?>> entrySet() {
		return tasks;
	}

	/*
	 * Get the scheduler execution service used to
	 * register the jobs with.
	 *
	 * @return the scheduler service used for all the registered jobs.
	 */
	public static ScheduledExecutorService getScheduler() {
		 return schedulerService;
	}
}
