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
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.ProtWords;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ProtWordsDAO  extends GenericDAO<ProtWords, Integer> implements Serializable {
	
	private static final long serialVersionUID = 6173759683888815887L;

	public ProtWordsDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();
		return predicates;
	}
	
	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(ProtWords entity) {
		Collection<Predicate> predicates = createPredicates();
		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(ProtWords entity) {
			super.delete(entity);
	}


	public ProtWords findByNome(String descricao) throws DaoException {
		ProtWords retornoProtWords = null;
		
		try {
			CriteriaQuery<ProtWords> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("descricao")), descricao.toLowerCase()));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoProtWords = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		}
		catch(NoResultException e){
			// Nada a fazer
		}catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
		return retornoProtWords;

	}

	public List<ProtWords> findbyCliente(Cliente cliente) throws DaoException {
		List<ProtWords> retornoProtWords= new ArrayList<>();
		
		try {
			CriteriaQuery<ProtWords> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("cliente"), cliente));

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoProtWords = entityManager.createQuery(criteriaQuery).getResultList();
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		}

		return retornoProtWords;		
	}

}
