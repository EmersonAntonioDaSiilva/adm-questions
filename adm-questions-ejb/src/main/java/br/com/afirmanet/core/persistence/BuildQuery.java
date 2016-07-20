package br.com.afirmanet.core.persistence;

import static com.google.common.collect.Lists.partition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.afirmanet.core.util.StringUtils;

public class BuildQuery {

	private EntityManager entityManager;
	private Map<String, List<Object>> parameters = new HashMap<>();
	private Map<String, String> columnNames = new HashMap<>();
	private int max = 1000;

	public BuildQuery(EntityManager em) {
		entityManager = em;
	}

	public BuildQuery(EntityManager em, int max) {
		this(em);
		this.max = max;
	}

	public void setParameter(String parameterName, Collection<? extends Object> values, String columnName) {
		parameters.put(parameterName, new ArrayList<>(values));
		columnNames.put(parameterName, columnName);
	}

	private String buildJpql(String jpql) {
		Iterator<String> i = parameters.keySet().iterator();
		while (i.hasNext()) {
			String parameterName = i.next();
			String columnName = columnNames.get(parameterName);
			if (StringUtils.contains(jpql, ":" + parameterName)) {
				String in = buildIn(parameterName, columnName);
				jpql = jpql.replace(":" + parameterName, in);
			}
		}
		return jpql;
	}

	public Query createQuery(String jpql) {
		jpql = buildJpql(jpql);
		try {
			Query query = entityManager.createQuery(jpql);
			Iterator<String> iterator = parameters.keySet().iterator();
			while (iterator.hasNext()) {
				String parameterName = iterator.next();
				List<Object> values = parameters.get(parameterName);
				List<List<Object>> list = partition(values, max);
				for (int i = 1; i <= list.size(); i++) {
					query.setParameter(parameterName + i, list.get(i - 1));
				}
			}
			return query;
		} catch (IllegalStateException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}

	private String buildIn(String parameterName, String columnName) {
		List<Object> values = parameters.get(parameterName);
		List<List<Object>> list = partition(values, max);
		StringBuilder in = new StringBuilder("(");
		for (int i = 1; i <= list.size(); i++) {
			StringBuilder s = new StringBuilder().append(columnName).append(" in (:").append(parameterName).append(i).append(")");
			in.append(i == 1 ? s : " or " + s);
		}
		in.append(")");
		return in.toString();
	}

}
