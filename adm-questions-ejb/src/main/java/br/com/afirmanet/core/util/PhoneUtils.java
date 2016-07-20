package br.com.afirmanet.core.util;

import javax.swing.text.MaskFormatter;

public class PhoneUtils {

	public static String format(String phoneNumber) {
		return format(phoneNumber, true);
	}

	public static String format(String phoneNumber, boolean lenient) {
		if (StringUtils.isBlank(phoneNumber)) {
			return null;
		}

		String phone = phoneNumber;

		try {
			phone = StringUtils.removeNonDigits(phoneNumber);

			String mask = null;
			if (phone.length() == 8) {
				mask = "####-####";
			} else if (phone.length() == 9) {
				mask = "#####-####";
			} else if (phone.length() == 10) {
				mask = "(##) ####-####";
			} else if (phone.length() == 11) {
				mask = "(##) #####-####";
			} else {
				return phoneNumber;
			}

			MaskFormatter maskFormatter = new MaskFormatter(mask);
			maskFormatter.setValueContainsLiteralCharacters(false);
			phone = maskFormatter.valueToString(phone);

		} catch (Exception e) {
			if (!lenient) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}

		return phone;
	}

	public static Long parse(String phoneNumber) {
		if (StringUtils.isBlank(phoneNumber)) {
			return null;
		}

		String phone = StringUtils.removeNonDigits(phoneNumber);
		if (phone.length() == 8 || phone.length() == 9 || phone.length() == 10 || phone.length() == 11) {
			return Long.valueOf(phone);
		}

		return null;
	}

	public static void main(String[] args) {
		System.out.println(format("12345678901a"));
	}

}
