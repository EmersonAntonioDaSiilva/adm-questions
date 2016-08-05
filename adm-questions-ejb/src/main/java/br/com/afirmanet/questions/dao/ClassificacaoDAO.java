package br.com.afirmanet.questions.dao;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

import br.com.afirmanet.core.enumeration.OrderEnum;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class ClassificacaoDAO extends GenericDAO<Classificacao, Integer> implements Serializable {
	private static final long serialVersionUID = 6907863285648197379L;

	public ClassificacaoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();

		
		return predicates;
	}
	
	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(Classificacao entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(Classificacao entity) {
			super.delete(entity);
	}


	public Classificacao findByNome(String descricao) {
		Classificacao retornoClassificacao = null;
		
		try {
			CriteriaQuery<Classificacao> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("descricao")), descricao.toLowerCase()));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoClassificacao = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return retornoClassificacao;

	}

	public List<Classificacao> findByClienteAndTopico(Cliente cliente, Topico topico) {
		List<Classificacao> retornoClassificacao = null;
		
		try {
			CriteriaQuery<Classificacao> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("cliente"), cliente));
			predicates.add(cb.equal(root.get("topico"), topico));

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoClassificacao = entityManager.createQuery(criteriaQuery).getResultList();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return retornoClassificacao;
	}
	
	public List<Classificacao> findByRangeOfDataCadastro(Cliente cliente, Topico topico, LocalDate dataInicio, LocalDate dataFim) {
		List<Classificacao> retornoClassificacao = null;
		
		try {
			CriteriaQuery<Classificacao> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();
			Collection<Order> orders = new ArrayList<>();
			
			predicates.add(cb.equal(root.get("cliente"), cliente));
			predicates.add(cb.equal(root.get("topico"), topico));
			
			LocalDateTime dataHoraInicio = LocalDateTime.of(dataInicio, LocalTime.MIDNIGHT);
			LocalDateTime dataHoraFim = LocalDateTime.of(dataFim, LocalTime.of(23, 59, 59));
			
			predicates.add(cb.between(root.get("dataCadastro"), dataHoraInicio, dataHoraFim));
			
			orders.add(cb.asc(root.get("dataCadastro")));
			
			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {})).orderBy(orders.toArray(new Order[] {}));
				
				retornoClassificacao = entityManager.createQuery(criteriaQuery).getResultList();
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return retornoClassificacao;
	}
}
