package br.com.afirmanet.core.faces.primefaces.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.afirmanet.core.enumeration.OrderEnum;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.core.util.ApplicationPropertiesUtils;

/**
 * <p>
 * Extensão para {@link org.primefaces.model.LazyDataModel}, para popular um datatable paginando pelo banco de dados,
 * evitando que todo o conteúdo seja carregado de uma só vez.
 * </p>
 *
 * @param <T>
 *        a entidade relacionada aos dados que seram populados
 * @param <ID>
 *        o tipo de dado (geralmente Long) usado como <i>id</i> da entidade
 *
 */
public class LazyEntityModel<T, ID extends Serializable> extends LazyDataModel<T> {

	private static final long serialVersionUID = -7401522547637194668L;

	private transient GenericDAO<T, ID> dao;
	private T searchParam;
	private Map<String, Object> filters = new HashMap<>();

	@Getter
	@Setter
	private int rows;

	private Map<String, T> wrappedData = new HashMap<>();

	public LazyEntityModel(GenericDAO<T, ID> dao, T searchParam) {
		super();
		this.dao = dao;
		this.searchParam = searchParam;
		rows = ApplicationPropertiesUtils.getValueAsInteger("paginator.rows", "10");

		setRowCount(-1);
	}

	@Override
	@SneakyThrows(Exception.class)
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		beforePagination();

		setRowCount(-1);
		dao.resetCriteria();
		dao.setFirstResult(first);
		dao.setMaxResults(pageSize);
		this.filters = filters;

		List<T> entities = dao.getResultList(getPredicates(filters, searchParam), getOrders(sortField, sortOrder));

		wrappedData = new HashMap<>();
		for (T entity : entities) {
			Object rowKey = getRowKey(entity);
			wrappedData.put(rowKey.toString(), entity);
		}

		afterPagination();

		return entities;
	}

	@Override
	public Object getRowKey(T entity) {
		try {
			return dao.getId(entity);

		} catch (Exception e) {
			throw new javax.faces.FacesException("Falha ao obter o número da linha", e);
		}
	}

	@Override
	public T getRowData(String rowKey) {
		if (rowKey == null) {
			return (T) null;
		}

		T entity = wrappedData.get(rowKey);
		return entity;
	}

	@Override
	@SneakyThrows(Exception.class)
	public int getRowCount() {
		if (super.getRowCount() == -1) {
			dao.resetCriteria();
			setRowCount(dao.getResultCount(getPredicates(filters, searchParam)).intValue());
		}

		return super.getRowCount();
	}

	/**
	 * Executado antes da query de paginação
	 */
	public void beforePagination() {
		//...
	}

	/**
	 * Executado após da query de paginação
	 */
	public void afterPagination() {
		//...
	}

	// Orders
	private List<Order> getOrders(String sortField, SortOrder sortOrder) throws Exception {
		Collection<Order> orders = new ArrayList<>();
		if (sortField != null) {
			OrderEnum orderEnum = sortOrder == SortOrder.ASCENDING ? OrderEnum.ASC : OrderEnum.DESC;
			orders.add(dao.createPaginationOrder(sortField, orderEnum));

		} else {
			orders.addAll(dao.createPaginationOrders());
		}

		return (List<Order>) orders;
	}

	// Predicates
	private Collection<Predicate> getPredicates(Map<String, Object> filters, T searchParam) throws Exception {
		Collection<Predicate> predicates = new ArrayList<>();
		Predicate predicate = null;
		for (Entry<String, Object> entrySet : filters.entrySet()) {
			predicate = dao.createPaginationPredicate(entrySet.getKey(), entrySet.getValue());
			if (predicate != null) {
				predicates.add(predicate);
			}
		}

		predicates.addAll(dao.createPaginationPredicates(searchParam));

		return predicates;
	}

}
