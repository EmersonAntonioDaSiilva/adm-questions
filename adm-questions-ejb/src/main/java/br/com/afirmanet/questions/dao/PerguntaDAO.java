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

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.questions.entity.Pergunta;
import br.com.afirmanet.questions.entity.Resposta;


@Slf4j
@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class PerguntaDAO extends GenericDAO<Pergunta, Integer> implements Serializable {
	private static final long serialVersionUID = 6907863285648197379L;

	public PerguntaDAO(EntityManager entityManager) {
		super(entityManager);
	}

	
	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();

		
		return predicates;
	}

	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(Pergunta entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(Pergunta entity) {
			super.delete(entity);
	}

	public Pergunta findByNome(String descricao) throws DaoException {
		Pergunta retornoInsumo = null;
		
		try {
			CriteriaQuery<Pergunta> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("descricao")), descricao.toLowerCase()));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoInsumo = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		}
		return retornoInsumo;

	}


	public Pergunta findPerguntaByDescricaoAndResposta(Pergunta byPergunta) throws DaoException {
		Pergunta retornoInsumo = null;
		
		try {
			CriteriaQuery<Pergunta> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(cb.lower(root.get("descricao")), byPergunta.getDescricao().toLowerCase()));
			predicates.add(cb.equal(root.get("resposta"), byPergunta.getResposta()));
			
			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoInsumo = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		}catch (NoResultException e){
			retornoInsumo = null;
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
		return retornoInsumo;
	}
	
	
	public List<Pergunta> findAllByResposta(Resposta byResposta) throws DaoException {
		List<Pergunta> retornoInsumo = null;
		
		try {
			CriteriaQuery<Pergunta> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("resposta"), byResposta));
			
			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoInsumo = entityManager.createQuery(criteriaQuery).getResultList();
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		}
		return retornoInsumo;
	}
	
}
