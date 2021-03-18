package net.bflows.pagafatture.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateTimeUtil {
	

	public static LocalDateTime DateServerToUTC(LocalDateTime systemDate) {

		ZonedDateTime ldtZoned = systemDate.atZone(ZoneId.systemDefault());
		LocalDateTime localDatetime = ldtZoned.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
		return localDatetime;
	}

	public static LocalDateTime getPreviousMonthDate(int month) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -month);
		LocalDateTime conv = LocalDateTime.ofInstant(c.getTime().toInstant(), ZoneId.systemDefault());
		return conv;
	}
	
	public static Date getDateWithoutTime(LocalDateTime systemDate) {
		ZonedDateTime zdt = systemDate.atZone(ZoneId.systemDefault());
		Date date = Date.from(zdt.toInstant());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

}
