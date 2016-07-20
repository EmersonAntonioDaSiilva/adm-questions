package br.com.afirmanet.core.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.SneakyThrows;

/**
 * <p>
 * Classe utilitária para manipulação de datas e usos variados dos objetos <code>Calendar</code> e <code>Date</code>.
 * </p>
 *
 */
public final class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	/**
	 * <p>
	 * Formato padrão <code>dd/MM/yyyy</code> aplicado em datas.
	 * </p>
	 */
	public static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";

	/**
	 * <p>
	 * Formato padrão <code>dd/MM/yyyy HH:mm:ss</code> aplicado em timestamp ou data com hora.
	 * </p>
	 */
	public static final String DEFAULT_TIMESTAMP_PATTERN = "dd/MM/yyyy HH:mm:ss";

	/**
	 * <p>
	 * Formato <code>yyyyMMdd</code> aplicado em datas oriunda de arquivos utilizados em processamento.
	 * </p>
	 */
	public static final String DATE_PATTERN_YYYYMMDD = "yyyyMMdd";

	public static final String DATE_PATTERN_YYYY_MM_DD = "yyyy-MM-dd";

	public static final String DATE_PATTERN_YYYYMMDDHHmmss = "yyyyMMddHHmmss";

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private DateUtils() {
		super();
	}

	public static LocalDate toLocalDate(Date date) {
		if (date instanceof java.sql.Date) {
			return ((java.sql.Date) date).toLocalDate();
		}
		return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault()).toLocalDate();
	}

	public static LocalDateTime toLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault());
	}

	public static Date toDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant());
	}

	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneOffset.systemDefault()).toInstant());
	}

	public static boolean isHoliday(List<LocalDate> holidays, LocalDate localDate) {
		for (LocalDate holiday : holidays) {
			if (holiday.isEqual(localDate)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isBusinessDay(List<LocalDate> holidays, LocalDate localDate) {
		return isBusinessDay(localDate) && !isHoliday(holidays, localDate);
	}

	private static boolean isBusinessDay(LocalDate localDate) {
		boolean isBusinessDay = !(localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY);
		return isBusinessDay;
	}

	public static LocalDate getNextBusinessDay(List<LocalDate> holidays, LocalDate localDate) {
		while (!isBusinessDay(holidays, localDate)) {
			localDate = localDate.plusDays(1);
		}

		return localDate;
	}

	public static LocalDate getPreviousBusinessDay(List<LocalDate> holidays, LocalDate localDate) {
		while (!isBusinessDay(holidays, localDate)) {
			localDate = localDate.minusDays(1);
		}

		return localDate;
	}

	@SneakyThrows(DatatypeConfigurationException.class)
	public static XMLGregorianCalendar toXmlGregorianCalendar(final LocalDateTime localDateTime) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(toDate(localDateTime));

		return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
	}

	public static String format(LocalDate localDate) {
		return format(localDate, DateUtils.DEFAULT_DATE_PATTERN);
	}

	public static String format(LocalDate localDate, String pattern) {
		if (localDate == null) {
			return null;
		}

		return localDate.format(DateTimeFormatter.ofPattern(pattern));
	}

	public static String format(LocalDateTime localDateTime) {
		return format(localDateTime, DateUtils.DEFAULT_TIMESTAMP_PATTERN);
	}

	public static String format(LocalDateTime localDateTime, String pattern) {
		if (localDateTime == null) {
			return null;
		}

		return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
	}

	public static LocalDate parseLocalDate(final String source) {
		return parseLocalDate(source, DEFAULT_DATE_PATTERN);
	}

	public static LocalDate parseLocalDate(final String source, final String pattern) {
		if (source == null) {
			return null;
		}

		LocalDate date = LocalDate.parse(source, DateTimeFormatter.ofPattern(pattern));
		return date;
	}

	public static LocalDateTime parseLocalDateTime(final String source) {
		return parseLocalDateTime(source, DEFAULT_TIMESTAMP_PATTERN);
	}

	public static LocalDateTime parseLocalDateTime(final String source, final String pattern) {
		if (source == null) {
			return null;
		}

		LocalDateTime date = LocalDateTime.parse(source, DateTimeFormatter.ofPattern(pattern));
		return date;
	}

	public static LocalDate defaultDate(LocalDate date, LocalDate defaultDate) {
		return date == null ? defaultDate : date;
	}

	public static boolean between(LocalDate date, LocalDate startDate, LocalDate endDate) {
		return date.compareTo(startDate) >= 0 && (endDate == null || date.compareTo(endDate) <= 0);
	}
}
