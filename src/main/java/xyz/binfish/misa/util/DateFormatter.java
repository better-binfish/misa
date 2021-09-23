package xyz.binfish.misa.util;

import java.time.temporal.TemporalAccessor;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

	private static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	private DateFormatter() { }

	public static String getReadableDateTime(TemporalAccessor ta) {
		return fmt.format(ta);
	}

	public static String getReadableDate(TemporalAccessor ta) {
		return getReadableDateTime(ta).substring(0, 10);
	}

	public static String getReadableTime(TemporalAccessor ta) {
		return getReadableDateTime(ta).substring(10);
	}
}
