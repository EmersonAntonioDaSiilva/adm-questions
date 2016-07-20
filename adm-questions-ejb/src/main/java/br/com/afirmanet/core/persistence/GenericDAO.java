package br.com.afirmanet.core.persistence;

import static br.com.afirmanet.core.enumeration.SearchRestrictionEnum.ILIKE;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.SingularAttribute;
import javax.transaction.NotSupportedException;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.type.LongType;

import com.google.common.base.Splitter;

import br.com.afirmanet.core.enumeration.OrderEnum;
import br.com.afirmanet.core.enumeration.SearchRestrictionEnum;
import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.util.DateUtils;
import br.com.afirmanet.core.util.PropertiesUtils;
import br.com.afirmanet.core.util.ReflectionUtils;

/**
 * A classe básica genérica para implementações de DAOs utilizando JPA 2. Deve ser estendida pelas implementações de
 * DAOs específicos. Utiliza o entity manager default <i>(entityManager)</i>. Existem métodos para salvar, alterar e
 * remover uma entidade, assim como vários métodos de pesquisa. Exemplo de uso:<br>
 * <code>
 * public class UserDao extends GenericDAO<User, Long> {
 *     ...
 * }
 * </code>
 *
 * @param <T>
 *        a entidade para a qual esta classe vai prover métodos de persistência
 * @param <ID>
 *        o tipo de dado (geralmente Long) usado como <i>id</i> da entidade
 *
 **/
public class GenericDAO<T, ID extends Serializable> {

	@Getter
	protected Class<T> entityClass;
	@Getter
	protected Root<T> root;
	protected CriteriaBuilder cb;
	protected CriteriaQuery<T> cq;

	@Getter
	protected Map<String, Path<?>> attibutesAliases = new HashMap<>();

	@Inject
	@Getter
	@Setter
	protected EntityManager entityManager;

	@Getter
	@Setter
	private Integer firstResult;

	@Getter
	@Setter
	private Integer maxResults;

	@Setter
	private SearchRestrictionEnum searchRestriction;
	
	private String idMethodName;
	private static Properties properties;
	
	static {
		properties = PropertiesUtils.load("Messages_pt_BR.properties");
	}
	
	public GenericDAO() {
		loadEntityClass();
	}

	public GenericDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
		loadEntityClass();
	}

	@SuppressWarnings("unchecked")
	private void loadEntityClass() {
		Type type = getClass().getGenericSuperclass();

		if (type instanceof ParameterizedType) {
			entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		} else {
			Type genericType = ((Class<?>) type).getGenericSuperclass();
			entityClass = (Class<T>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
		}

		idMethodName = "get" + StringUtils.capitalize(ReflectionUtils.findIdColumnName(entityClass));
	}

	@PostConstruct
	private void init() {
		resetCriteria();
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows
	public ID getId(T entity) {
		Method idMethod = entity.getClass().getMethod(idMethodName, new Class[0]);
		ID id = (ID) idMethod.invoke(entity, new Object[0]);

		return id;
	}

	public <X> X getSingleResult(TypedQuery<X> query) {
		try {
			return query.getSingleResult();

		} catch (NoResultException e) {
			return (X) null;
		}
	}

	/*------------------------------------------------------------------------
	 * EVENT SAVE
	 *-----------------------------------------------------------------------*/

	public T save(T entity) {
		try {
			entityManager.persist(entity);
			return entity;
		} catch (Exception e) {
			String message = PropertiesUtils.getValue(properties,"br.com.afirmanet.faces.message.INTERNAL_ERROR_SAVE");
			throw new ApplicationException(message, e);
		}
	}

	public T saveAndFlush(T entity) {
		save(entity);
		entityManager.flush();

		return entity;
	}

	public void save(Collection<T> entityList) {
		for (T entity : entityList) {
			save(entity);
		}
	}

	public void saveAndFlush(Collection<T> entityList) {
		save(entityList);
		entityManager.flush();
	}

	public void save(Collection<T> entityList, Integer flushWhen) {
		int count = 1;

		for (T entity : entityList) {
			save(entity);

			if (count % flushWhen == 0) {
				entityManager.flush();
				entityManager.clear();
			}

			count++;
		}
	}

	/*------------------------------------------------------------------------
	 * EVENT UPDATE
	 *-----------------------------------------------------------------------*/

	public T update(T entity) {
		try {
			entityManager.merge(entity);
			return entity;
		} catch (Exception e) {
			String message = PropertiesUtils.getValue(properties,"br.com.afirmanet.faces.message.INTERNAL_ERROR_UPDATE");
			throw new ApplicationException(message, e);

		}
	}

	public T updateAndFlush(T entity) {
		update(entity);
		entityManager.flush();
		return entity;
	}

	public void update(Collection<T> entityList) {
		for (T entity : entityList) {
			update(entity);
		}
	}

	public void updateAndFlush(Collection<T> entityList) {
		update(entityList);
		entityManager.flush();
	}

	/*------------------------------------------------------------------------
	 * EVENT DELETE
	 *-----------------------------------------------------------------------*/

	public int deleteById(ID id) {
		try {
			Query query = entityManager.createQuery("DELETE FROM " + entityClass.getSimpleName() + " WHERE id = :id");
			query.setParameter("id", id);

			return query.executeUpdate();
		} catch (Exception e) {
			String message = PropertiesUtils.getValue(properties,"br.com.afirmanet.faces.message.INTERNAL_ERROR_DELETE");
			throw new ApplicationException(message, e);

		}
	}

	public void delete(T entity) {
		try {
			entityManager.remove(entity);
		} catch (Exception e) {
			String message = PropertiesUtils.getValue(properties,"br.com.afirmanet.faces.message.INTERNAL_ERROR_DELETE");
			throw new ApplicationException(message, e);

		}
	}

	public void deleteAndFlush(T entity) {
		delete(entity);
		entityManager.flush();

	}

	public void delete(Collection<T> entityList) {
		for (T entity : entityList) {
			delete(entity);
		}
	}

	public void deleteAndFlush(Collection<T> entityList) {
		delete(entityList);
		entityManager.flush();
	}

	/*------------------------------------------------------------------------
	 * EVENT SEARCH
	 *-----------------------------------------------------------------------*/

	public void resetCriteria() {
		cb = entityManager.getCriteriaBuilder();
		cq = cb.createQuery(entityClass);
		root = cq.from(entityClass);
	}

	public CriteriaQuery<T> createCriteriaQuery() {
		cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = cb.createQuery(entityClass);
		root = criteriaQuery.from(entityClass);
		return criteriaQuery;
	}

	public TypedQuery<T> createTypedQuery(Map<SingularAttribute<T, ?>, Object> properties, Order... orders) {
		CriteriaQuery<T> criteriaQuery = createCriteriaQuery();

		Collection<Predicate> predicates = new ArrayList<>();
		for (Map.Entry<SingularAttribute<T, ?>, Object> property : properties.entrySet()) {
			predicates.add(cb.equal(root.get(property.getKey()), property.getValue()));
		}

		criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])).orderBy(orders);
		TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);

		return typedQuery;
	}

	public T getById(ID id) {
		return entityManager.find(entityClass, id);
	}

	public List<T> findAll() {
		TypedQuery<T> query = entityManager.createQuery("SELECT entity FROM " + entityClass.getSimpleName() + " entity", entityClass);
		return query.getResultList();
	}

	public List<T> findAll(br.com.afirmanet.core.persistence.Order... orders) {
		CriteriaQuery<T> criteriaQuery = createCriteriaQuery();

		List<Order> orderList = getOrderList(orders);

		criteriaQuery.select(root).orderBy(orderList);

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	public List<T> findBy(Collection<Predicate> predicates, br.com.afirmanet.core.persistence.Order... orders) {
		List<Order> orderList = getOrderList(orders);

		cq.select(root).where(predicates.toArray(new Predicate[predicates.size()])).orderBy(orderList);

		List<T> resultList = entityManager.createQuery(cq).getResultList();
		resetCriteria();

		return resultList;
	}

	@SuppressWarnings("unchecked")
	protected List<Order> getOrderList(br.com.afirmanet.core.persistence.Order... orders) {
		List<Order> orderList = new ArrayList<>();
		for (br.com.afirmanet.core.persistence.Order order : orders) {
			if (order.getOrderEnum() == OrderEnum.ASC) {
				orderList.add(cb.asc(root.get(order.getSingularAttribute())));
			} else {
				orderList.add(cb.desc(root.get(order.getSingularAttribute())));
			}
		}
		return orderList;
	}

	public List<T> findBy(Map<SingularAttribute<T, ?>, Object> properties) {
		CriteriaQuery<T> criteriaQuery = getMapCriteriaQuery(properties);
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	public List<T> findBy(Map<SingularAttribute<T, ?>, Object> properties, br.com.afirmanet.core.persistence.Order... orders) {
		CriteriaQuery<T> criteriaQuery = getMapCriteriaQuery(properties, orders);
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	public List<T> findBy(Predicate... predicates) {
		CriteriaQuery<T> criteriaQuery = createCriteriaQuery();
		criteriaQuery.select(root).where(predicates);

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	public T getBy(Predicate... predicates) {
		CriteriaQuery<T> criteriaQuery = createCriteriaQuery();
		criteriaQuery.select(root).where(predicates);

		return entityManager.createQuery(criteriaQuery).getSingleResult();
	}

	private CriteriaQuery<T> getMapCriteriaQuery(Map<SingularAttribute<T, ?>, Object> properties, br.com.afirmanet.core.persistence.Order... orders) {
		CriteriaQuery<T> criteriaQuery = createCriteriaQuery();

		Collection<Predicate> predicates = new ArrayList<>();
		for (Map.Entry<SingularAttribute<T, ?>, Object> property : properties.entrySet()) {
			predicates.add(cb.equal(root.get(property.getKey()), property.getValue()));
		}

		List<Order> orderList = getOrderList(orders);

		criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])).orderBy(orderList);
		return criteriaQuery;
	}

	public List<T> findBy(SingularAttribute<T, ?> attribute, Object value, br.com.afirmanet.core.persistence.Order... orders) {
		CriteriaQuery<T> criteriaQuery = createCriteriaQuery();

		List<Order> orderList = getOrderList(orders);

		criteriaQuery.select(root).where(cb.equal(root.get(attribute), value)).orderBy(orderList);
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ID> findIdBy(SingularAttribute<T, ?> attribute, Object value) {
		Query query = entityManager.createQuery(String.format("SELECT obj.id FROM %s obj WHERE obj.%s = :value", entityClass.getSimpleName(), attribute.getName()));
		query.setParameter("value", value);
		return query.getResultList();
	}

	public T getBy(Map<SingularAttribute<T, ?>, Object> properties) {
		try {
			CriteriaQuery<T> criteriaQuery = getMapCriteriaQuery(properties);
			return entityManager.createQuery(criteriaQuery).getSingleResult();

		} catch (NoResultException e) {
			return (T) null;
		}
	}

	public T getBy(SingularAttribute<T, ?> attribute, Object value) {
		try {
			CriteriaQuery<T> criteriaQuery = createCriteriaQuery();
			criteriaQuery.select(root).where(cb.equal(root.get(attribute), value));
			return entityManager.createQuery(criteriaQuery).getSingleResult();

		} catch (NoResultException e) {
			return (T) null;
		}
	}

	@SuppressWarnings("unchecked")
	public T getIgnoreCaseBy(SingularAttribute<T, ?> attribute, String value) {
		try {
			CriteriaQuery<T> criteriaQuery = createCriteriaQuery();
			criteriaQuery.select(root).where(cb.equal(cb.upper((Expression<String>) root.get(attribute)), value));
			return entityManager.createQuery(criteriaQuery).getSingleResult();

		} catch (NoResultException e) {
			return (T) null;
		}
	}

	public List<T> findAnywhereBy(SingularAttribute<T, String> attribute, String value) {
		CriteriaQuery<T> criteriaQuery = createCriteriaQuery();
		criteriaQuery.select(root).where(cb.like(cb.lower(root.get(attribute)), "%" + value.toLowerCase() + "%"));
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	public List<T> findByIn(SingularAttribute<T, ?> attribute, Collection<?> values, br.com.afirmanet.core.persistence.Order... orders) {
		CriteriaQuery<T> criteriaQuery = createCriteriaQuery();

		List<Order> orderList = getOrderList(orders);
		criteriaQuery.select(root).where(root.get(attribute).in(values)).orderBy(orderList);

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	public List<T> findByNotIn(SingularAttribute<T, ?> attribute, Collection<?> values, br.com.afirmanet.core.persistence.Order... orders) {
		CriteriaQuery<T> criteriaQuery = createCriteriaQuery();

		List<Order> orderList = getOrderList(orders);
		criteriaQuery.select(root).where(cb.not(root.get(attribute).in(values))).orderBy(orderList);

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	public List<T> findByIsNull(SingularAttribute<T, ?> attribute, br.com.afirmanet.core.persistence.Order... orders) {
		CriteriaQuery<T> criteriaQuery = createCriteriaQuery();

		List<Order> orderList = getOrderList(orders);
		criteriaQuery.select(root).where(root.get(attribute).isNull()).orderBy(orderList);

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	public List<T> findFirstResultBy(Map<SingularAttribute<T, ?>, Object> properties, Integer firtsResult, Order... orders) {
		TypedQuery<T> typedQuery = createTypedQuery(properties, orders);
		typedQuery.setFirstResult(firtsResult);
		return typedQuery.getResultList();
	}

	public List<T> findMaxResultsBy(Map<SingularAttribute<T, ?>, Object> properties, Integer maxResults, Order... orders) {
		TypedQuery<T> typedQuery = createTypedQuery(properties, orders);
		typedQuery.setMaxResults(maxResults);
		return typedQuery.getResultList();
	}

	public List<T> findBy(Map<SingularAttribute<T, ?>, Object> properties, Integer firtsResult, Integer maxResults, Order... orders) {
		TypedQuery<T> typedQuery = createTypedQuery(properties, orders);
		typedQuery.setFirstResult(firtsResult);
		typedQuery.setMaxResults(maxResults);
		return typedQuery.getResultList();
	}

	/**
	 * <p>
	 * Retorna o maior id referente a tabela associada a entity parametrizada nesta classe.
	 * </p>
	 *
	 * @return Maior id.
	 */

	@SuppressWarnings("unchecked")
	public ID findMaxId() {
		Query query = entityManager.createQuery(String.format("SELECT MAX(obj.id) FROM %s obj", entityClass.getSimpleName()));
		return (ID) query.getSingleResult();
	}

	/**
	 * <p>
	 * Retorna a última entidade inserida na tabela associada a entidade parametrizada.
	 * </p>
	 *
	 * @return última entidade cadastrada
	 */
	@SuppressWarnings("unchecked")
	public T getByMaxId() {
		Query query = entityManager.createQuery(String.format("SELECT entity FROM %s entity WHERE entity.id = (SELECT MAX(entity2.id) FROM %s entity2)", entityClass.getSimpleName(),
				entityClass.getSimpleName()));
		return (T) query.getSingleResult();
	}

	public Long getNextSequenceValue(String sequence) {
		Query query = entityManager.createNativeQuery("SELECT " + sequence + ".NEXTVAL AS ID FROM DUAL");
		query.unwrap(SQLQuery.class).addScalar("ID", LongType.INSTANCE);
		Long sequenceValue = (Long) query.getSingleResult();
		return sequenceValue;
	}

	/*------------------------------------------------------------------------
	 * EVENT PAGINATION
	 *-----------------------------------------------------------------------*/

	public Collection<Predicate> createPaginationPredicates(T searchParam) {
		Collection<Predicate> predicates = new ArrayList<>();

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(searchParam.getClass());
			PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();

			for (PropertyDescriptor property : properties) {
				if (!"class".equals(property.getName())) {
					Object propertyValue = property.getReadMethod().invoke(searchParam, (Object[]) null);
					String propertyName = property.getName();

					if (property.getPropertyType() == String.class) {
						if (StringUtils.isNotBlank((String) propertyValue) && !ReflectionUtils.isAnnotationPresent(searchParam.getClass(), property, Transient.class)) {
							if (SearchRestrictionEnum.EQUALS == searchRestriction) {
								predicates.add(cb.equal(root.<String> get(propertyName), propertyValue));
							} else if (SearchRestrictionEnum.LIKE == searchRestriction) {
								predicates.add(cb.like(root.<String> get(propertyName), "%" + propertyValue + "%"));
							} else if (ILIKE == searchRestriction) {
								predicates.add(cb.like(cb.upper(root.<String> get(propertyName)), "%" + propertyValue.toString().trim().toUpperCase() + "%"));
							} else {
								throw new NotSupportedException(String.format("Restrição de consulta [%s] ainda não implementada!", searchRestriction));
							}
						}
					} else if (propertyValue != null && PersistenceHelper.isValidType(property, searchParam) && !ReflectionUtils.isAnnotationPresent(searchParam.getClass(), property, Transient.class)) {
						predicates.add(cb.equal(root.<Object> get(propertyName), propertyValue));
					}
				}
			}

		} catch (Throwable e) {
			throw new PersistenceException(e);
		}

		return predicates;
	}

	public List<Order> createPaginationOrders() {
		return new ArrayList<>();
	}

	public List<T> getResultList(Collection<Predicate> predicates, List<Order> orders) {
		if (!orders.isEmpty()) {
			cq.select(root).where(predicates.toArray(new Predicate[predicates.size()])).orderBy(orders);
		} else {
			cq.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
		}

		TypedQuery<T> typedQuery = entityManager.createQuery(cq);
		typedQuery.setFirstResult(getFirstResult());
		typedQuery.setMaxResults(getMaxResults());

		return typedQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public Long getResultCount(Collection<Predicate> predicates) {
		Collection<Predicate> newPredicates = predicates;

		// O JPA não permite fazer count com entities associada com fetch, havendo a necessidade de limpar os fetches, porém caso não tenha nenhum predicate associado ao fetch específico, o mesmo não é utilizado na query gerada para realizar a paginação, havendo a necessidade de realizar um predicate genérico para resolver isso.
		if (CollectionUtils.isEmpty(predicates) && CollectionUtils.isNotEmpty(root.getFetches())) {
			Iterator<Fetch<T, ?>> iterator = root.getFetches().iterator();
			while (iterator.hasNext()) {
				From<?, ?> from = ((From<?, ?>) iterator.next());
				newPredicates.add(cb.or(from.isNull(), from.isNotNull()));
			}
		}

		root.getFetches().clear();
		cq.select((Selection<? extends T>) cb.count(root)).where(newPredicates.toArray(new Predicate[newPredicates.size()]));
		return (Long) entityManager.createQuery(cq).getSingleResult();
	}

	// Predicates

	public Predicate createPaginationPredicate(String attribute, Object value) throws Exception {
		Iterable<String> iterable = Splitter.on('.').split(attribute);
		return createPaginationPredicate(entityClass, iterable.iterator(), value, root);
	}

	@SuppressWarnings("unchecked")
	protected <Y> Predicate createPaginationPredicate(Path<Y> path, Y object) {
		Predicate predicate = null;

		if (object instanceof String) {
			if (SearchRestrictionEnum.LIKE == searchRestriction) {
				predicate = cb.like((Expression<String>) path, "%" + object + "%");

			} else if (SearchRestrictionEnum.ILIKE == searchRestriction) {
				predicate = cb.like(cb.upper((Expression<String>) path), "%" + object.toString().trim().toUpperCase() + "%");

			} else {
				predicate = cb.equal(path, object);
			}

		} else if (object instanceof List) {
			predicate = cb.in(path).value(object);

		} else if (object instanceof LocalDateTime) {
			LocalDateTime start = ((LocalDateTime) object).truncatedTo(ChronoUnit.HOURS);
			LocalDateTime end = start.plusDays(1);
			predicate = cb.between((Expression<LocalDateTime>) path, start, end);

		} else {
			predicate = cb.equal(path, object);
		}

		return predicate;
	}

	protected Predicate createPaginationPredicate(Class<?> root, Iterator<String> attributes, Object value, FetchParent<?, ?> from) throws Exception {
		String attributeName = attributes.next();
		Field filterField = root.getDeclaredField(attributeName);
		boolean isEntity = ReflectionUtils.isEntity(filterField.getType());
		FetchParent<?, ?> joined = null;

		if (isEntity) {
			joined = from.fetch(attributeName, JoinType.INNER);
			((Join<?, ?>) joined).alias(attributeName);

			if (attributes.hasNext()) {
				return createPaginationPredicate(filterField.getType(), attributes, value, joined);
			}
		}

		Object parsedValue = parseValue(value, filterField.getType());
		if (parsedValue != null) {
			return createPaginationPredicate(((From<?, ?>) from).get(attributeName), parsedValue);
		}

		return null;
	}

	protected Object parseValue(Object value, Class<?> type) throws Exception {
		if (value.getClass().isArray()) {
			Object[] values = (Object[]) value;
			List<Object> list = new ArrayList<>();
			for (Object val : values) {
				list.add(parseValue(val, type));
			}

			return list.isEmpty() ? null : list;

		} else if (LocalDate.class.equals(type)) {
			return DateUtils.toLocalDate((java.util.Date) value);

		} else if (LocalDateTime.class.equals(type)) {
			return DateUtils.toLocalDateTime((java.util.Date) value);
		}

		return ConvertUtils.convert(value, type);
	}

	// Orders

	public Order createPaginationOrder(String attribute, OrderEnum orderEnum) throws Exception {
		Iterable<String> iterable = Splitter.on('.').split(attribute);
		return createPaginationOrder(entityClass, iterable.iterator(), orderEnum, root);
	}

	protected Order createPaginationOrder(SingularAttribute<T, ?> attribute, OrderEnum orderEnum) {
		return createPaginationOrder(root.get(attribute), orderEnum);
	}

	protected Order createPaginationOrder(Path<?> path, OrderEnum orderEnum) {
		if (orderEnum == OrderEnum.DESC) {
			return cb.desc(path);
		}

		return cb.asc(path);
	}

	protected Order createPaginationOrder(Class<?> root, Iterator<String> attributes, OrderEnum orderEnum, FetchParent<?, ?> from) throws Exception {
		String attributeName = attributes.next();
		Field filterField = root.getDeclaredField(attributeName);
		boolean isEntity = ReflectionUtils.isEntity(filterField.getType());
		FetchParent<?, ?> joined = null;

		if (isEntity) {
			joined = from.fetch(attributeName, JoinType.INNER);
			((Join<?, ?>) joined).alias(attributeName);

			if (attributes.hasNext()) {
				return createPaginationOrder(filterField.getType(), attributes, orderEnum, joined);
			}
		}

		return createPaginationOrder(((From<?, ?>) from).get(attributeName), orderEnum);
	}

}
