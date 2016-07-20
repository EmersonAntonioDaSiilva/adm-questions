package br.com.afirmanet.core.io.jatb;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.com.afirmanet.core.io.jatb.adapter.TxtAdapter;
import br.com.afirmanet.core.io.jatb.annotation.TxtBusinessRuleValidator;
import br.com.afirmanet.core.io.jatb.annotation.TxtField;
import br.com.afirmanet.core.io.jatb.annotation.TxtJavaTypeAdapter;
import br.com.afirmanet.core.io.jatb.annotation.TxtLine;
import br.com.afirmanet.core.io.jatb.annotation.TxtRoot;
import br.com.afirmanet.core.io.jatb.exception.JatbLayoutException;
import br.com.afirmanet.core.io.jatb.exception.JatbValidatorException;
import br.com.afirmanet.core.io.jatb.validator.TxtValidator;

public class Marshaller {

	private static final Class<? extends Annotation> ANNOTATION_CLASS = TxtRoot.class;
	private boolean skipValidator;

	public String marshal(Object instance) {
		return marshal(instance, false);
	}

	public String marshal(Object instance, final boolean skipValidator) {
		Class<? extends Object> clazz = instance.getClass();
		if (!clazz.isAnnotationPresent(ANNOTATION_CLASS)) {
			throw new JatbLayoutException("A instância deve pertencer a uma classe que esteja anotada com {0}.", ANNOTATION_CLASS.getName());
		}

		this.skipValidator = skipValidator;

		AccessibleObject lineContentMember = null;
		StringBuilder lineContent = new StringBuilder();

		try {
			List<String> fieldNames = new ArrayList<>();

			if ("java.lang.Object".equals(clazz.getSuperclass().getName())) {
				Field[] fields = clazz.getDeclaredFields();
				sortMembers(fields);

				for (Field field : fields) {
					TxtField txtField = field.getAnnotation(TxtField.class);
					if (txtField != null) {
						lineContent.append(toString(field, txtField, instance));
					}

					if (field.getAnnotation(TxtLine.class) != null) {
						lineContentMember = field;
					}
				}

			} else {
				Method[] methods = clazz.getDeclaredMethods();
				sortMembers(methods);

				for (Method method : methods) {
					TxtField txtField = method.getAnnotation(TxtField.class);
					if (txtField != null) {
						lineContent.append(toString(method, txtField, instance));
						fieldNames.add(method.getName().substring(3).toLowerCase()); // Joga o nome do método sem o 'set' ou 'get'
					}

					if (method.getAnnotation(TxtLine.class) != null) {
						lineContentMember = method;
					}
				}

				Field[] fieldsSuperclass = clazz.getSuperclass().getDeclaredFields();
				sortMembers(fieldsSuperclass);

				for (Field field : fieldsSuperclass) {
					TxtField txtField = field.getAnnotation(TxtField.class);
					if (txtField != null && !fieldNames.contains(field.getName().toLowerCase())) {
						lineContent.append(toString(field, txtField, instance));
					}

					if (field.getAnnotation(TxtLine.class) != null) {
						lineContentMember = field;
					}
				}
			}

			// Fill line content
			if (lineContentMember != null) {
				setMember(lineContentMember, instance, lineContent.toString());
			}

		} catch (Throwable e) {
			throw new JatbLayoutException(e.getMessage(), e);
		}

		return lineContent.toString();
	}

	private String toString(AccessibleObject member, TxtField txtField, Object instance) {
		String strValue = null;
		Object value = getMember(member, instance);
		boolean enabledValidator = !skipValidator || txtField.forceValidator();

		TxtBusinessRuleValidator validatorField = member.getAnnotation(TxtBusinessRuleValidator.class);
		if (enabledValidator && validatorField != null) {
			if (!applyValidator(validatorField, txtField, value)) {
				throw new JatbValidatorException("O conteúdo do campo [{0}] não passou pela sua respectiva [{0}] validação.", member.toString(), validatorField.value().getName());
			}
		}

		TxtJavaTypeAdapter adapterField = member.getAnnotation(TxtJavaTypeAdapter.class);
		if (adapterField != null) {
			strValue = applyAdapter(adapterField, value);
		} else {
			strValue = String.valueOf(value);
		}

		strValue = strValue == null || strValue.equals("null") || (StringUtils.isBlank(strValue) && !StringUtils.EMPTY.equals(txtField.defaultValue())) ? txtField.defaultValue() : strValue;

		int beginIndex = txtField.startChar() - 1;
		int endIndex = txtField.endChar();
		int size = endIndex - beginIndex;
		char padChar = txtField.padChar();

		if (strValue.length() > size) {
			strValue = StringUtils.substring(strValue, 0, size);
		} else {
			strValue = StringUtils.leftPad(strValue, size, padChar);
		}

		return strValue;
	}

	@SuppressWarnings("unchecked")
	private String applyAdapter(TxtJavaTypeAdapter adapterField, Object value) {
		try {
			Constructor<? extends TxtAdapter<Object, Object>> constructor = (Constructor<? extends TxtAdapter<Object, Object>>) adapterField.value().getConstructor();
			TxtAdapter<Object, Object> instance = constructor.newInstance();
			String valueAdapted;
			if (adapterField.remove()) {
				valueAdapted = (String) instance.marshalRemove(value, adapterField.caracterRemove());
			} else {
				valueAdapted = (String) instance.marshal(value);
			}
			return valueAdapted;
		} catch (Throwable e) {
			throw new JatbLayoutException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean applyValidator(TxtBusinessRuleValidator validatorField, TxtField txtField, Object value) {
		try {
			Constructor<? extends TxtValidator<Object, Object>> constructor = (Constructor<? extends TxtValidator<Object, Object>>) validatorField.value().getConstructor();
			TxtValidator<Object, Object> instance = constructor.newInstance();
			boolean isValid = instance.marshal(txtField, value);

			return isValid;
		} catch (Throwable e) {
			throw new JatbLayoutException(e);
		}
	}

	private Object getMember(AccessibleObject member, Object instance) {
		try {
			member.setAccessible(true);

			if (member instanceof Method) {
				return ((Method) member).invoke(instance);
			} else if (member instanceof Field) {
				return ((Field) member).get(instance);
			}

			return null;

		} catch (Exception e) {
			throw new JatbLayoutException(e);
		}
	}

	private <T> void setMember(AccessibleObject member, T instance, Object value) {
		try {
			member.setAccessible(true);

			if (member instanceof Method) {
				((Method) member).invoke(instance, value);

			} else if (member instanceof Field) {
				((Field) member).set(instance, value);
			}
		} catch (Throwable e) {
			throw new JatbLayoutException(e);
		}
	}

	private void sortMembers(AccessibleObject[] members) {
		Arrays.sort(members, (o1, o2) -> {
			TxtField o1TxtField = o1.getAnnotation(TxtField.class);
			TxtField o2TxtField = o2.getAnnotation(TxtField.class);
			if (o1TxtField != null && o2TxtField != null) {
				return o1TxtField.startChar() < o2TxtField.startChar() ? -1 : (o1TxtField.startChar() > o2TxtField.startChar() ? 1 : 0);
			}

			return 0;
		});
	}

}
