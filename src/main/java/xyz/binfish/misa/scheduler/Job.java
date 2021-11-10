package xyz.binfish.misa.scheduler;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import xyz.binfish.misa.Misa;
import xyz.binfish.logger.Logger;

public abstract class Job extends TimerTask {

	/*
	 * The amount of time the job should be delayed before starting.
	 */
	private final long delay;

	/*
	 * The amount of time in between each time the job should be executed.
	 */
	private final long period;

	/*
	 * The unit of time that the job should be scaled after.
	 */
	private final TimeUnit unit;

	/*
	 * Instantiates the job instance with the given delay,
	 * setting the default period to 1 along with time unit of TimeUnit#MINUTES.
	 *
	 * @param delay the delay before the command should be executed for the first time.
	 */
	public Job(long delay) {
		this(delay, 1);
	}

	/*
	 * Instantiates the job instance with the given delay and period,
	 * setting the default time unit to TimeUnit#MINUTES.
	 *
	 * @param delay  the delay before the command should be executed for the first time.
	 * @param period the time in between executions after the job has already run once. 
	 */
	public Job(long delay, long period) {
		this(delay, period, TimeUnit.MINUTES);
	}

	/*
	 * Instantiates the job instance with the delay, period
	 * and time unit.
	 *
	 * @param delay  the delay before the command should be executed for the first time.
	 * @param period the time in between executions after the job has already run once.
	 * @param unit   the unit of time the job should measure the delay and periods in.
	 */
	public Job(long delay, long period, TimeUnit unit) {
		this.delay = delay;
		this.period = period;
		this.unit = unit;
	}

	/*
	 * Get the job delay.
	 *
	 * @return the job delay.
	 */
	public long getDelay() {
		return delay;
	}

	/*
	 * Get the job period.
	 *
	 * @return the job period.
	 */
	public long getPeriod() {
		return period;
	}

	/*
	 * Get the time unit.
	 *
	 * @return the time unit.
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	/*
	 * Handles the given tasks by invoking them one by one within a try-catch
	 * statement, if a exception is thrown nothing should fail.
	 *
	 * @param tasks the tasks that should be handled.
	 */
	protected void handleTask(Task... tasks) {
		for(Task task : tasks) {
			try {
				if(Misa.getSettings().useDebugging()) {
					Logger.getLogger().debug(String.format("Invoking %s#handle", task.getClass().getName()));
				}
				task.handle();
			} catch(Exception e) {
				Logger.getLogger().error(String.format("An error occurred while running the %s class, message: %s",
							task.getClass().getSimpleName(), e.getMessage()), e
				);
			}
		}
	}
}
