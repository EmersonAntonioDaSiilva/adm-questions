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
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.StopWords;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class StopWordsDAO extends GenericDAO<StopWords, Integer> implements Serializable {
	private static final long serialVersionUID = 6907863285648197379L;

	public StopWordsDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();

		
		return predicates;
	}
	
	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(StopWords entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(StopWords entity) {
			super.delete(entity);
	}


	public StopWords findByNome(String descricao) throws DaoException {
		StopWords retornoStopWords = null;
		
		try {
			CriteriaQuery<StopWords> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("descricao")), descricao.toLowerCase()));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoStopWords = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		}
		catch(NoResultException ee){
		}
		catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
		return retornoStopWords;

	}

	public List<StopWords> findbyCliente(Cliente cliente) throws DaoException {
		List<StopWords> retornoStopWords= new ArrayList<>();
		
		try {
			CriteriaQuery<StopWords> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("cliente"), cliente));

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoStopWords = entityManager.createQuery(criteriaQuery).getResultList();
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		}

		return retornoStopWords;		
	}
}
