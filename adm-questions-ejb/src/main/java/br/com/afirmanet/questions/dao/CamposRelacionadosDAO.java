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
import br.com.afirmanet.questions.entity.CamposRelacionados;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class CamposRelacionadosDAO extends GenericDAO<CamposRelacionados, Integer> implements Serializable {
	private static final long serialVersionUID = 6907863285648197379L;

	public CamposRelacionadosDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();

		
		return predicates;
	}
	
	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(CamposRelacionados entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(CamposRelacionados entity) {
			super.delete(entity);
	}


	public CamposRelacionados findByNome(String descricao) throws DaoException {
		CamposRelacionados retornoInsumo = null;
		
		try {
			CriteriaQuery<CamposRelacionados> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("campo")), descricao.toLowerCase()));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoInsumo = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		} catch (NoResultException e){
			retornoInsumo = null;
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
		return retornoInsumo;

	}
}
