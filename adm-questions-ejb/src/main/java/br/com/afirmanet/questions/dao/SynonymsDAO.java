package br.com.afirmanet.questions.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import br.com.afirmanet.questions.entity.Synonyms;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SynonymsDAO extends GenericDAO<Synonyms, Integer> implements Serializable {
	
	private static final long serialVersionUID = 3049018584560975642L;

	public SynonymsDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();
		return predicates;
	}
	
	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(Synonyms entity) {
		Collection<Predicate> predicates = createPredicates();
		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(Synonyms entity) {
		super.delete(entity);
	}


	public Synonyms findByDescricao(String descricao, Boolean mapeado) throws DaoException {
		Synonyms retornoSynonyms = null;
		
		try {
			CriteriaQuery<Synonyms> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("descricao")), descricao.toLowerCase()));
				predicates.add(cb.equal(root.get("mapeado"), mapeado));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoSynonyms = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		} catch (NoResultException e) {
			// Nada a fazer
		}
		catch (Exception e) {
				throw new DaoException(e.getMessage());
		}
		return retornoSynonyms;

	}

	public List<Synonyms> findbyCliente(Cliente cliente) throws DaoException {
		List<Synonyms> retornoSynonyms= new ArrayList<>();
		
		try {
			CriteriaQuery<Synonyms> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("cliente"), cliente));

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoSynonyms = entityManager.createQuery(criteriaQuery).getResultList();
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		}

		return retornoSynonyms;		
	}

}
