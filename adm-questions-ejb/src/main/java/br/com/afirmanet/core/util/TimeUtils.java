package br.com.afirmanet.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * <p>
 * Classe utilitária para manipulação de tempo e uso variado do objeto <code>Calendar</code>.
 * </p>
 */
public final class TimeUtils {

	/**
	 * <p>
	 * Formato padrão <code>HH:mm</code> aplicado em tempo.
	 * </p>
	 */
	public static final String DEFAULT_TIME_PATTERN = "HH:mm";

	/**
	 * <p>
	 * Formato padrão <code>HH:mm-HH:mm</code> aplicado em período de tempo.
	 * </p>
	 */
	public static final String DEFAULT_PERIOD_PATTERN = "HH:mm-HH:mm";

	private static final Pattern DEFAULT_PERIOD_REGEX = Pattern.compile("(\\d{2})[:](\\d{2})[-](\\d{2})[:](\\d{2})");

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private TimeUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	/**
	 * <p>
	 * Converte a <code>String</code> informada para millisegundos.
	 * </p>
	 *
	 * <pre>
	 * Calendar currentCalendar = Calendar.getInstance(); // Equivale a 25/04/2011
	 *
	 * TimeUtils.toTimeInMillis("22:24", "HH:mm", currentCalendar)       = 1303781040000
	 * TimeUtils.toTimeInMillis("22:24:25", "HH:mm:ss", currentCalendar) = 1303781065000
	 * </pre>
	 *
	 * @param time
	 *        uma <code>String</code> que representa o tempo a ser convertido em millisegundos
	 * @param pattern
	 *        uma <code>String</code> que representa o formato a ser considerado para extração do tempo
	 * @param calendar
	 *        um <code>Date</code> que será incorporado nos millisegundos gerado
	 *
	 * @return millisegundos que representa a <code>String</code> e <code>Calendar</code> informados
	 *
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>time</code> não corresponder ao padrão especificado
	 */
	public static long toTimeInMillis(String time, String pattern, Calendar calendar) {
		try {
			DateFormat dateFormat = new SimpleDateFormat(pattern);
			dateFormat.setLenient(false);

			Calendar calendarTemp = Calendar.getInstance();
			calendarTemp.setTime(dateFormat.parse(time));

			calendar.set(Calendar.HOUR_OF_DAY, calendarTemp.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, calendarTemp.get(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, calendarTemp.get(Calendar.SECOND));
			calendar.set(Calendar.MILLISECOND, calendarTemp.get(Calendar.MILLISECOND));

			return calendar.getTimeInMillis();

		} catch (ParseException e) {
			throw new IllegalArgumentException("Parâmetro informado não corresponde a uma hora válida. A hora deve estar no formato [" + pattern + "] e no range [HH = 0-23 | mm = 0-59 | ss = 0-59].",
					e);
		}
	}

	public static long toTimeInMillis(String time, String pattern) {
		return toTimeInMillis(time, pattern, Calendar.getInstance());
	}

	/**
	 * <p>
	 * Converte a <code>String</code> informada para millisegundos. O formato do tempo permitido é
	 * {@value #DEFAULT_TIME_PATTERN}.
	 * </p>
	 *
	 * <p>
	 * <b>NOTA:</b> Caso a instância do <code>Calendar</code> não seja informada será considerada a data corrente para
	 * incorporar junto aos milisegundos retornado.
	 * </p>
	 *
	 * <pre>
	 * Calendar currentCalendar = Calendar.getInstance(); // Equivale a 25/04/2011
	 * Calendar oldCalendar = Calendar.getInstance();
	 * oldCalendar.set(2011, 0, 1); // Equivale a 01/01/2011
	 *
	 * TimeUtils.toTimeInMillis("22:24")                  = 1303781040000
	 * TimeUtils.toTimeInMillis("22:24", currentCalendar) = 1303781040000
	 * TimeUtils.toTimeInMillis("22:24", oldCalendar)     = 1293927840000
	 * TimeUtils.toTimeInMillis("22:24", null)            = 1303781040000
	 * </pre>
	 *
	 * @param time
	 *        uma <code>String</code> que representa o tempo a ser convertido em millisegundos
	 * @param date
	 *        um <code>Date</code> qeu será incorporado nos millisegundos gerado
	 *
	 * @return millisegundos que representa a <code>String</code> informada
	 *
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>time</code> não estiver no padrão esperado {@value #DEFAULT_TIME_PATTERN}.
	 */
	public static long toTimeInMillis(String time, Calendar date) {
		return toTimeInMillis(time, DEFAULT_TIME_PATTERN, date);
	}

	public static long toTimeInMillis(String time) {
		return toTimeInMillis(time, DEFAULT_TIME_PATTERN, Calendar.getInstance());
	}

	/**
	 * <p>
	 * Verifica se o <code>Calendar</code> está compreendido no período (Tempo inicial e final) informado.
	 * </p>
	 *
	 * <p>
	 * <b>NOTA:</b> Caso o <code>Calendar</code> não seja informado, será considerado a data corrente do sistema.
	 * </p>
	 *
	 * <pre>
	 * Calendar currentCalendar = Calendar.getInstance(); // Data/Hora corrente 25/04/2011 17:53:33
	 *
	 * TimeUtils.checkPeriod("17:00-07:00") = true
	 * TimeUtils.checkPeriod("07:00-18:00") = true
	 * TimeUtils.checkPeriod("07:00-17:00") = false
	 * TimeUtils.checkPeriod("00:00-23:59") = true
	 * </pre>
	 *
	 * @param period
	 *        uma <code>String</code> que representa o período {@value #DEFAULT_PERIOD_PATTERN} considerado para
	 *        avaliação da data informada
	 * @param date
	 *        um <code>Calendar</code> que será avaliado dentro do período informado
	 *
	 * @return <code>true</code> para data dentro do período, caso contrário <code>false</code>
	 *
	 * @throws IllegalArgumentException
	 *         se a <code>String</code> que representa o período não estiver no padrão {@value #DEFAULT_PERIOD_PATTERN}
	 */
	public static boolean checkPeriod(String period, Calendar date) {
		if (!DEFAULT_PERIOD_REGEX.matcher(period).matches()) {
			throw new IllegalArgumentException("Parâmetro informado não corresponde a um período válido. O período deve estar no formato [" + DEFAULT_PERIOD_PATTERN + "]!");
		}

		String[] times = period.split("-");

		return checkPeriod(times[0], times[1], DEFAULT_TIME_PATTERN, date);
	}

	public static boolean checkPeriod(String period) {
		return checkPeriod(period, Calendar.getInstance());
	}

	/**
	 * <p>
	 * Verifica se o <code>Calendar</code> está compreendido entre as <code>String</code> que representam o tempo
	 * inicial e final informados.
	 * </p>
	 *
	 * <pre>
	 * Calendar currentCalendar = Calendar.getInstance(); // 25/04/2011 17:59:40
	 *
	 * TimeUtils.checkPeriod("17:00", "07:00", "HH:mm") = true
	 * TimeUtils.checkPeriod("07:00", "18:00", "HH:mm") = true
	 * TimeUtils.checkPeriod("00:00", "23:59", "HH:mm") = true
	 * </pre>
	 *
	 * @param startTime
	 *        uma <code>String</code> que representa o tempo inicial a ser considerado como período de avaliação
	 * @param endTime
	 *        uma <code>String</code> que representa o tempo final a ser considerado como período de avaliação
	 * @param patternTime
	 *        uma <code>String</code> que representa o padrão utilizado para parsear os tempos inicial e final
	 *        utilizados para definir o período de avaliação
	 * @param calendar
	 *        um <code>Calendar</code> que representa a data considerada para avaliação
	 *
	 * @return <code>true</code> para data dentro do período, caso contrário <code>false</code>
	 *
	 * @throws IllegalArgumentException
	 *         se o parâmetro <code>startTime</code> e <code>endTime</code> não estiverem dentro do padrão informado
	 */
	public static boolean checkPeriod(String startTime, String endTime, String patternTime, Calendar calendar) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patternTime);

		LocalTime start = LocalTime.parse(startTime, formatter);
		LocalTime end = LocalTime.parse(endTime, formatter);

		LocalDateTime dateTime = DateUtils.toLocalDateTime(calendar.getTime());
		LocalTime time = LocalTime.of(dateTime.getHour(), dateTime.getMinute());

		if (end.isBefore(start)) {
			return time.isAfter(start) || time.isBefore(end);
		}

		return time.isAfter(start) && time.isBefore(end);
	}

}
