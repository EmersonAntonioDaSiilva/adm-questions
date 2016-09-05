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
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.persistence.GenericDAO;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class ClusterSolrDAO extends GenericDAO<ClusterSolrDAO, Integer> implements Serializable {
	private static final long serialVersionUID = 6907863285648197379L;

	public ClusterSolrDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();

		
		return predicates;
	}
	
	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(ClusterSolrDAO entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(ClusterSolrDAO entity) {
			super.delete(entity);
	}


	public ClusterSolrDAO findByNome(String descricao)throws DaoException{
		ClusterSolrDAO retornoClassificacao = null;
		
		try {
			CriteriaQuery<ClusterSolrDAO> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("nome_cluster")), descricao.toLowerCase()));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoClassificacao = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
		return retornoClassificacao;

	}

}
