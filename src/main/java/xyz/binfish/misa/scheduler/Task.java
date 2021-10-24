package xyz.binfish.misa.scheduler;

public interface Task {

	/*
	 * Handles the task when the task is ready to be invoked.
	 */
	void handle();
}
