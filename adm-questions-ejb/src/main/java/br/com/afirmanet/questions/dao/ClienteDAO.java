package br.com.afirmanet.questions.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.questions.entity.Cliente;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class ClienteDAO extends GenericDAO<Cliente, Integer> implements Serializable {
	private static final long serialVersionUID = 6907863285648197379L;

	public ClienteDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();

		return predicates;
	}

	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(Cliente entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(Cliente entity) {
		super.delete(entity);
	}

	public Cliente findByNome(String descricao) throws DaoException {
		Cliente retornoCliente = null;
		try {
			CriteriaQuery<Cliente> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("descricao")), descricao.toLowerCase()));
			}

			if (!predicates.isEmpty()) {
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoCliente = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		} catch (NoResultException e) {
			retornoCliente = null;

		} catch (Exception e) {

			throw new DaoException(e.getMessage(), e);

		}
		return retornoCliente;

	}
}
