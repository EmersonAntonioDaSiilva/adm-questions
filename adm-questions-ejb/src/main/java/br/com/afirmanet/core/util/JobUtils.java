package br.com.afirmanet.core.util;

import java.text.ParseException;
import java.util.Calendar;

import org.quartz.CronExpression;

public final class JobUtils {

	public static Boolean isSatisfiedBy(String cronExpression) {
		try {
			CronExpression cron = new CronExpression(cronExpression);
			Boolean isSatisfied = cron.isSatisfiedBy(Calendar.getInstance().getTime());
			return isSatisfied;

		} catch (ParseException e) {
			return false;
		}
	}

	public static Boolean isSatisfiedByMultipleExpression(String cronExpression) {
		String arrayCron[] = cronExpression.split(";");
		boolean condicao = false;
		for (String element : arrayCron) {
			condicao = isSatisfiedBy(element);
			if (condicao) {
				break;
			}
		}
		return condicao;
	}

}
