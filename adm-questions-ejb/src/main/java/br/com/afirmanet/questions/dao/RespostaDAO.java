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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Resposta;
import br.com.afirmanet.questions.entity.Resposta_;
import br.com.afirmanet.questions.entity.Topico;
import lombok.NoArgsConstructor;

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


	public Resposta findByTitulo(Resposta entity) throws DaoException {
		Resposta retornoInsumo = null;
		
		try {
			CriteriaQuery<Resposta> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (entity.getTitulo() != null && !entity.getTitulo().isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("titulo")), entity.getTitulo()));
				predicates.add(cb.equal(cb.lower(root.get("cliente")), entity.getCliente()));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoInsumo = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		} catch (NoResultException e){
			retornoInsumo=null;
			
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		}
		return retornoInsumo;

	}

	public String findByDescricao(String resposta)throws DaoException {
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
			throw new DaoException(e.getMessage(),e);
		}

		return "";
	}

	public List<Resposta> findByClienteAndTopico(Cliente cliente, Topico topico) throws DaoException {
		List<Resposta> retornoResposta = null;
		
		try {
			CriteriaQuery<Resposta> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("cliente"), cliente));
			predicates.add(cb.equal(root.get("topico"), topico));

			if(!predicates.isEmpty()){
				
				List<Order> orderList = getOrderList(br.com.afirmanet.core.persistence.Order.asc(Resposta_.titulo));

				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {})).orderBy(orderList);;

				retornoResposta = entityManager.createQuery(criteriaQuery).getResultList();
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		}
		return retornoResposta;
	}
	
	public List<Resposta> getDadosGeraArquivo(Collection<Predicate> predicates) throws DaoException {
		List<Resposta> retornoResposta = null;

		try {
			CriteriaQuery<Resposta> criteriaQuery = createCriteriaQuery();

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoResposta = entityManager.createQuery(criteriaQuery).getResultList();
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
		return retornoResposta;
	}
}
