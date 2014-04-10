package info.gehrels.voting.web;

import org.joda.time.DateTime;
import org.springframework.format.Formatter;

import java.util.Locale;

class DateTimeFormatter implements Formatter<DateTime> {

	@Override
	public DateTime parse(String s, Locale locale) {
		return new DateTime(s);
	}

	@Override
	public String print(DateTime dateTime, Locale locale) {
		return dateTime.toString();
	}
}
