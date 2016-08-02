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
import br.com.afirmanet.questions.entity.Resposta;
import br.com.afirmanet.questions.entity.Topico;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class RespostaDAO extends GenericDAO<Resposta, Integer> implements Serializable {
	private static final long serialVersionUID = 6907863285648197379L;

	public RespostaDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();

		
		return predicates;
	}
	
	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(Resposta entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(Resposta entity) {
			super.delete(entity);
	}


	public Resposta findByNome(String descricao) {
		Resposta retornoInsumo = null;
		
		try {
			CriteriaQuery<Resposta> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("titulo")), descricao.toLowerCase()));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoInsumo = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return retornoInsumo;

	}

	public String findByDescricao(String resposta) {
		Resposta retornoInsumo = null;
		
		try {
			CriteriaQuery<Resposta> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (resposta != null && !resposta.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("titulo")), resposta.toLowerCase()));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoInsumo = entityManager.createQuery(criteriaQuery).getSingleResult();
				return retornoInsumo.getDefinicao();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return "";
	}

	public List<Resposta> findByClienteAndTopico(Cliente cliente, Topico topico) {
		List<Resposta> retornoResposta = null;
		
		try {
			CriteriaQuery<Resposta> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("cliente"), cliente));
			predicates.add(cb.equal(root.get("topico"), topico));

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoResposta = entityManager.createQuery(criteriaQuery).getResultList();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return retornoResposta;
	}
	
	public List<Resposta> getDadosGeraArquivo(Collection<Predicate> predicates) {
		List<Resposta> retornoResposta = null;

		try {
			CriteriaQuery<Resposta> criteriaQuery = createCriteriaQuery();

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoResposta = entityManager.createQuery(criteriaQuery).getResultList();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return retornoResposta;
	}
}
