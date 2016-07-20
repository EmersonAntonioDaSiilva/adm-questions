package br.com.afirmanet.core.web.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.PropertyUtils;

import br.com.afirmanet.core.exception.SystemException;
import br.com.afirmanet.core.web.Formatter;

/**
 * <p>
 * Classe utilitária para auxiliar no desenvolvimento de aplicações que utilizam JSF.
 * </p>
 */
public final class JsfUtils {

	public static final Object NULL_VALUE = "";
	public static final String NULL_DESCRIPTION = "Selecione aqui...";

	/**
	 * <p>
	 * Classe utilitária não deve ter construtor público ou default.
	 * </p>
	 */
	private JsfUtils() {
		// Classe utilitária não deve ter construtor público ou default
	}

	/**
	 * <p>
	 * Cria um item, instância de SelectItem, nulo, ou seja, cria aquele famoso label dos combox 'SELECIONE AQUI...'.
	 * </p>
	 *
	 * @return Item default para ser adicionado a um combox.
	 */
	public static SelectItem createEmptyItem() {
		return new SelectItem(NULL_VALUE, NULL_DESCRIPTION);
	}

	/**
	 * <p>
	 * Cria um item, instância de SelectItem, nulo, ou seja, cria aquele famoso label dos combox 'SELECIONE AQUI...'.
	 * </p>
	 *
	 * @param emptyItemLabel
	 *        label do item vazio
	 *
	 * @return Item default para ser adicionado a um combox.
	 */
	public static SelectItem createEmptyItem(String emptyItemLabel) {
		return new SelectItem(NULL_VALUE, emptyItemLabel);
	}

	/**
	 * <p>
	 * Converte uma lista de vetor de objetos em uma lista de SelectItem, facilitando a população de combos.
	 * </p>
	 *
	 * @param values
	 *        Lista de vetor de objetos que se deseja converter em lista de SelectItem.
	 * @return Lista de SelectItem.
	 */
	public static List<SelectItem> convertToSelectItemList(List<Object[]> values) {
		return convertToSelectItemList(values, true, null);
	}

	/**
	 * <p>
	 * Converte uma lista de vetor de objetos em uma lista de SelectItem, facilitando a população de combos.
	 * </p>
	 *
	 * @param values
	 *        Lista de vetor de objetos que se deseja converter em lista de SelectItem.
	 * @param nullable
	 *        True para adição do item default 'Selecione aqui...' e false caso contrário.
	 * @return Lista de SelectItem.
	 */
	public static List<SelectItem> convertToSelectItemList(List<Object[]> values, boolean nullable) {
		return convertToSelectItemList(values, nullable, null);
	}

	/**
	 * <p>
	 * Converte uma lista de vetor de objetos em uma lista de SelectItem, facilitando a população de combos.
	 * </p>
	 * <p>
	 * <b>NOTA:</b> Caso não deseje que o primeiro item seja populado com o item de default 'Selecione aqui...', basta
	 * informar no segundo Parâmetro o valor <code>false</code>.
	 *
	 * @param values
	 *        Lista de vetor de objetos que se deseja converter em lista de SelectItem.
	 * @param nullable
	 *        True para adição do item default 'Selecione aqui...' e false caso contrário.
	 * @param emptyItemLabel
	 *        label do primeiro item (item vazio)
	 * @return Lista de SelectItem.
	 */
	public static List<SelectItem> convertToSelectItemList(List<Object[]> values, boolean nullable, String emptyItemLabel) {
		List<SelectItem> lista = new ArrayList<>();

		if (nullable) {
			if (emptyItemLabel != null) {
				lista.add(createEmptyItem(emptyItemLabel));
			} else {
				lista.add(createEmptyItem());
			}
		}

		for (Object[] key : values) {
			lista.add(new SelectItem(key[0], key[1].toString()));
		}

		return lista;
	}

	public static <T> List<SelectItem> convertToSelectItemList(List<T> values, String propertyLabel, String propertyValue) {
		try {
			List<SelectItem> selectItemList = new ArrayList<>();

			for (T object : values) {
				String label = (String) PropertyUtils.getProperty(object, propertyLabel);
				String value = (String) PropertyUtils.getProperty(object, propertyValue);

				selectItemList.add(new SelectItem(label, value));
			}

			return selectItemList;

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new SystemException(e);
		}
	}

	public static <T> List<SelectItem> convertToSelectItemList(List<T> values, String propertyLabel, String propertyValue, Formatter formatter) {
		try {
			List<SelectItem> selectItemList = new ArrayList<>();

			for (T object : values) {
				String value = formatter.format((String) PropertyUtils.getProperty(object, propertyValue));
				String label = formatter.format((String) PropertyUtils.getProperty(object, propertyLabel));

				selectItemList.add(new SelectItem(value, label));
			}

			return selectItemList;

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new SystemException(e);
		}
	}

}