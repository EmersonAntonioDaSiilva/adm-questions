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

import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.DadosWatson;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class DadosWatsonDAO extends GenericDAO<DadosWatson, Integer> implements Serializable {
	private static final long serialVersionUID = 6907863285648197379L;

	public DadosWatsonDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();

		
		return predicates;
	}
	
	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(DadosWatson entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(DadosWatson entity) {
			super.delete(entity);
	}

	public DadosWatson findByClienteAndTypeServico(Cliente cliente, TypeServicoEnum typeServico) {
		DadosWatson retornoDadosWatson = null;
		
		try {
			CriteriaQuery<DadosWatson> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("cliente"), cliente));
			predicates.add(cb.equal(root.get("typeServico"), typeServico));
			
			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoDadosWatson = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return retornoDadosWatson;
	}
	
	public List<DadosWatson> getDadosGeraArquivo(Collection<Predicate> predicates) {
		List<DadosWatson> retornoDadosWatson = null;

		try {
			CriteriaQuery<DadosWatson> criteriaQuery = createCriteriaQuery();

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoDadosWatson = entityManager.createQuery(criteriaQuery).getResultList();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return retornoDadosWatson;
	}
}
