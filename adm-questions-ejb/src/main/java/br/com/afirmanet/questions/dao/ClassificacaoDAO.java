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

import br.com.afirmanet.core.exception.DaoException;
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


	public Classificacao findByNome(String descricao) throws DaoException {
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
			throw new DaoException(e.getMessage(),e);
		}
		return retornoClassificacao;

	}

	public List<Classificacao> findByClienteAndTopico(Cliente cliente, Topico topico) throws DaoException {
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
			throw new DaoException(e.getMessage(),e);
		}
		return retornoClassificacao;
	}
	
	public List<Classificacao> findByRangeOfDataCadastro(Cliente cliente, Topico topico, LocalDateTime dataInicio, LocalDateTime dataFim)throws DaoException {
		return buscaDataCadastroEntreDuasDatas(cliente, topico,  dataInicio, dataFim);
	}
	public List<Classificacao> findByRangeOfDataCadastro(Cliente cliente, Topico topico, LocalDate dataInicio, LocalDate dataFim) throws DaoException{
		
		LocalDateTime dataHoraInicio = LocalDateTime.of(dataInicio, LocalTime.MIN);
		LocalDateTime dataHoraFim = LocalDateTime.of(dataFim, LocalTime.MAX);
		
		return buscaDataCadastroEntreDuasDatas(cliente, topico,  dataHoraInicio, dataHoraFim);
	}
	
	private List<Classificacao> buscaDataCadastroEntreDuasDatas(Cliente cliente, Topico topico, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim) {
		List<Classificacao> retornoClassificacao = null;
		
		try {
			CriteriaQuery<Classificacao> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();
			Collection<Order> orders = new ArrayList<>();
			
			predicates.add(cb.equal(root.get("cliente"), cliente));
			predicates.add(cb.equal(root.get("topico"), topico));
			predicates.add(cb.between(root.get("dataCadastro"), dataHoraInicio, dataHoraFim));
			
			orders.add(cb.asc(root.get("dataCadastro")));
			
			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {})).orderBy(orders.toArray(new Order[] {}));
				
				retornoClassificacao = entityManager.createQuery(criteriaQuery).getResultList();
			}
			
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
		
		return retornoClassificacao;
	}

	public List<Classificacao> findByDataCadastroESentimento(Cliente cliente, Topico topico, LocalDate dataHIni, LocalDate dataHFim, Integer sentimento) {
		
		LocalDateTime dataHoraInicio = LocalDateTime.of(dataHIni, LocalTime.MIN);
		LocalDateTime dataHoraFim = LocalDateTime.of(dataHFim, LocalTime.MAX);
		
		return buscaDataCadastroSentimento(cliente, topico, dataHoraInicio, dataHoraFim, sentimento);
	}
	
	public List<Classificacao> findByDataCadastroESentimento(Cliente cliente, Topico topico, LocalDateTime dataHIni, LocalDateTime dataHFim, Integer sentimento) {
		
		LocalDateTime dataHoraInicio = LocalDateTime.of(dataHIni.toLocalDate(), LocalTime.MIN);
		LocalDateTime dataHoraFim = LocalDateTime.of(dataHFim.toLocalDate(), LocalTime.MAX);
		
		return buscaDataCadastroSentimento(cliente, topico, dataHoraInicio, dataHoraFim, sentimento);
	}
	
	private List<Classificacao> buscaDataCadastroSentimento(Cliente cliente, Topico topico, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, Integer sentimento) throws DaoException {
		List<Classificacao> retornoClassificacao = null;
		
		try {
			CriteriaQuery<Classificacao> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();
			Collection<Order> orders = new ArrayList<>();
			
			predicates.add(cb.isNull(root.get("dataClassificacao")));
			predicates.add(cb.equal(root.get("cliente"), cliente));
			predicates.add(cb.equal(root.get("topico"), topico));
			predicates.add(cb.equal(root.get("sentimento"), sentimento));
			predicates.add(cb.between(root.get("dataCadastro"), dataHoraInicio, dataHoraFim));
			
			orders.add(cb.asc(root.get("dataCadastro")));
			
			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {})).orderBy(orders.toArray(new Order[] {}));
				
				retornoClassificacao = entityManager.createQuery(criteriaQuery).getResultList();
			}
			
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		}
		
		return retornoClassificacao;
	}
}
