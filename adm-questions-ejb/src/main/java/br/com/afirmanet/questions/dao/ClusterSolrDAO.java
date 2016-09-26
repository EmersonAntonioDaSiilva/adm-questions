package br.com.afirmanet.questions.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.ClusterSolr;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class ClusterSolrDAO extends GenericDAO<ClusterSolr, Integer> implements Serializable {
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
	public Collection<Predicate> createPaginationPredicates(ClusterSolr entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(ClusterSolr entity) {
			super.delete(entity);
	}


	public ClusterSolr findByNome(String descricao) throws DaoException {
		ClusterSolr retornoClassificacao = null;
		
		try {
			CriteriaQuery<ClusterSolr> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("nomeCluster")), descricao.toLowerCase()));
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
	
	public ClusterSolr findByCliente(Cliente cliente) throws DaoException {
		ClusterSolr retornoClusters = null;
		
		try {
			CriteriaQuery<ClusterSolr> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();
			
			if (cliente != null) {
				predicates.add(cb.equal(root.get("cliente"), cliente));
			}
			
			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoClusters = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
			
		} catch(Exception e) {
			throw new DaoException(e.getMessage());
		}
		
		return retornoClusters;
	}

}
