package br.com.afirmanet.core.io.jatb;

import static org.hamcrest.Matchers.isIn;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import br.com.afirmanet.core.io.jatb.adapter.TxtAdapter;
import br.com.afirmanet.core.io.jatb.annotation.TxtBusinessRuleValidator;
import br.com.afirmanet.core.io.jatb.annotation.TxtBusinessRuleValidators;
import br.com.afirmanet.core.io.jatb.annotation.TxtCustomMessage;
import br.com.afirmanet.core.io.jatb.annotation.TxtField;
import br.com.afirmanet.core.io.jatb.annotation.TxtJavaTypeAdapter;
import br.com.afirmanet.core.io.jatb.annotation.TxtLine;
import br.com.afirmanet.core.io.jatb.annotation.TxtRoot;
import br.com.afirmanet.core.io.jatb.enumeration.TxtValidatorEnum;
import br.com.afirmanet.core.io.jatb.exception.JatbLayoutException;
import br.com.afirmanet.core.io.jatb.exception.JatbValidatorException;
import br.com.afirmanet.core.io.jatb.validator.TxtValidator;

public class Unmarshaller {

	private static final Class<? extends Annotation> ANNOTATION_CLASS = TxtRoot.class;
	private Map<String, String> errors;
	private boolean skipValidator;

	public <T> T unmarshal(final String lineContent, Class<T> clazz) {
		return unmarshal(lineContent, clazz, false);
	}

	public <T> T unmarshal(final String lineContent, Class<T> clazz, final boolean skipValidator) {
		if (!clazz.isAnnotationPresent(ANNOTATION_CLASS)) {
			throw new JatbLayoutException("Classe {0} deve estar anotada com {1}.", clazz.getName(), ANNOTATION_CLASS.getName());
		}

		this.skipValidator = skipValidator;

		validateLayout(clazz, lineContent);

		T instance = (T) null;
		errors = new HashMap<>();

		try {
			List<String> fieldNames = new ArrayList<>();

			Constructor<T> constructor = clazz.getConstructor();
			instance = constructor.newInstance();

			if ("java.lang.Object".equals(clazz.getSuperclass().getName())) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					try {
						TxtField txtField = field.getAnnotation(TxtField.class);
						if (txtField != null) {
							Object value = getValue(field, txtField, lineContent);
							setMember(field, instance, value);
						} else if (field.getAnnotation(TxtLine.class) != null) {
							setMember(field, instance, lineContent);
						}

					} catch (JatbValidatorException e) {
						errors.put(e.getCode(), e.getMessage());
					}
				}

			} else {
				Method[] methods = clazz.getDeclaredMethods();
				for (Method method : methods) {
					try {
						TxtField txtField = method.getAnnotation(TxtField.class);
						if (txtField != null) {
							Object value = getValue(method, txtField, lineContent);
							setMember(method, instance, value);
							fieldNames.add(method.getName().substring(3).toLowerCase()); // Joga o nome do método sem o 'set' ou 'get'
						}

					} catch (JatbValidatorException e) {
						errors.put(e.getCode(), e.getMessage());
					}
				}

				Field[] fieldsSuperclass = clazz.getSuperclass().getDeclaredFields();
				for (Field field : fieldsSuperclass) {
					try {
						TxtField txtField = field.getAnnotation(TxtField.class);
						if (txtField != null && !fieldNames.contains(field.getName().toLowerCase())) {
							Object value = getValue(field, txtField, lineContent);
							setMember(field, instance, value);
						} else if (field.getAnnotation(TxtLine.class) != null) {
							setMember(field, instance, lineContent);
						}

					} catch (JatbValidatorException e) {
						errors.put(e.getCode(), e.getMessage());
					}
				}
			}

			if (!errors.isEmpty()) {
				throw new JatbValidatorException(errors, "Alguns campos não passaram no critério de validação. Chame o método getErrors() para obter a relação dos erros encontrados.");
			}

		} catch (JatbValidatorException | JatbLayoutException e) {
			throw e;
		} catch (Throwable e) {
			throw new JatbLayoutException(e.getMessage(), e);
		}

		return instance;
	}

	private Object getValue(AccessibleObject member, TxtField txtField, String lineContent) {
		int beginIndex = txtField.startChar() - 1;
		int endIndex = txtField.endChar();
		Object value = null;

		// Recuperar o valor do campo
		String strValue = lineContent.substring(beginIndex, endIndex);
		if (!StringUtils.EMPTY.equals(txtField.defaultValue())) {
			value = StringUtils.isBlank(strValue) ? txtField.defaultValue() : strValue;
		} else {
			value = strValue;
		}

		// Aplicar as validações pré-definidas
		boolean enabledValidator = !skipValidator || txtField.forceValidator();
		if (enabledValidator && txtField.required()) {
			if ((StringUtils.isBlank(strValue) || Long.parseLong(strValue) == 0L) && (!isIn(txtField.acceptedValues()).matches(strValue))) {
				throwTxtValidatorException(member, txtField, TxtValidatorEnum.REQUIRED);
			}
		}

		if (enabledValidator && ArrayUtils.isNotEmpty(txtField.acceptedValues()) && !isIn(txtField.acceptedValues()).matches(strValue)) {
			throwTxtValidatorException(member, txtField, TxtValidatorEnum.ACCEPTED_VALUES);
		}

		// Aplicar as validações de negócio
		TxtBusinessRuleValidators txtBusinessRuleValidators = member.getAnnotation(TxtBusinessRuleValidators.class);
		if (enabledValidator && txtBusinessRuleValidators != null) {
			TxtBusinessRuleValidator[] validatorsField = txtBusinessRuleValidators.value();
			sortValidators(validatorsField);

			for (TxtBusinessRuleValidator validatorField : validatorsField) {
				if (validatorField != null) {
					if (!applyValidator(validatorField, txtField, value)) {
						throw new JatbValidatorException(StringUtils.isNotBlank(validatorField.errorCode()) ? validatorField.errorCode() : member.toString(), StringUtils.isNotBlank(validatorField
								.errorMessage()) ? validatorField.errorMessage() : validatorField.value().getName());
					}
				}
			}
		}

		TxtBusinessRuleValidator validatorField = member.getAnnotation(TxtBusinessRuleValidator.class);
		if (enabledValidator && validatorField != null) {
			if (!applyValidator(validatorField, txtField, value)) {
				throw new JatbValidatorException(StringUtils.isNotBlank(validatorField.errorCode()) ? validatorField.errorCode() : member.toString(), StringUtils.isNotBlank(validatorField
						.errorMessage()) ? validatorField.errorMessage() : validatorField.value().getName());
			}
		}

		// Adaptar o valor para um tipo java
		TxtJavaTypeAdapter adapterField = member.getAnnotation(TxtJavaTypeAdapter.class);
		if (adapterField != null) {
			value = applyAdapter(adapterField, value);
		}

		return value;
	}

	@SuppressWarnings("unchecked")
	private Object applyAdapter(TxtJavaTypeAdapter adapterField, Object value) {
		try {
			Constructor<? extends TxtAdapter<Object, Object>> constructor = (Constructor<? extends TxtAdapter<Object, Object>>) adapterField.value().getConstructor();
			TxtAdapter<Object, Object> instance = constructor.newInstance();
			Object object = instance.unmarshal(value);

			return object;

		} catch (Throwable e) {
			throw new JatbLayoutException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean applyValidator(TxtBusinessRuleValidator validatorField, TxtField txtField, Object value) {
		try {
			Constructor<? extends TxtValidator<Object, Object>> constructor = (Constructor<? extends TxtValidator<Object, Object>>) validatorField.value().getConstructor();
			TxtValidator<Object, Object> instance = constructor.newInstance();
			boolean isValid = instance.unmarshal(txtField, value);

			return isValid;

		} catch (JatbValidatorException e) {
			throw e;
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

	private <T> void validateLayout(Class<T> clazz, String lineContent) {
		TxtRoot txtRoot = (TxtRoot) clazz.getAnnotation(ANNOTATION_CLASS);
		int lineSize = txtRoot.lineSize();
		if (lineContent.length() != lineSize) {
			throw new JatbLayoutException("O tamanho da linha contida no arquivo não obedece ao tamanho definido na classe. Esperado: {0} - Encontrado: {1}", lineSize, lineContent.length());
		}
	}

	private void sortValidators(TxtBusinessRuleValidator[] validatorsField) {
		Arrays.sort(validatorsField, (o1, o2) -> o1.priority() < o2.priority() ? -1 : (o1.priority() == o2.priority() ? 0 : 1));
	}

	private void throwTxtValidatorException(AccessibleObject member, TxtField txtField, TxtValidatorEnum txtValidatorEnum) {
		String errorCode = member.toString();
		String errorMessage = txtValidatorEnum.toString();

		for (TxtCustomMessage customMessage : txtField.customMessages()) {
			if (txtValidatorEnum == customMessage.value()) {
				errorCode = StringUtils.isNotBlank(customMessage.errorCode()) ? customMessage.errorCode() : member.toString();
				errorMessage = StringUtils.isNotBlank(customMessage.errorMessage()) ? customMessage.errorMessage() : customMessage.value().toString();

				break;
			}
		}

		if (txtField.stopProcessingOnValidatorError()) {
			throw new JatbLayoutException(errorCode, errorMessage);
		}

		throw new JatbValidatorException(errorCode, errorMessage);
	}

}
