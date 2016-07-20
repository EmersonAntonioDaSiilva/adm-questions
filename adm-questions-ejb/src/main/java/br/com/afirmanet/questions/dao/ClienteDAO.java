package br.com.afirmanet.questions.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.questions.entity.Cliente;


@Slf4j
@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class ClienteDAO extends GenericDAO<Cliente, Integer> implements Serializable {
	private static final long serialVersionUID = 6907863285648197379L;

	public ClienteDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();

		
		return predicates;
	}
	
	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(Cliente entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(Cliente entity) {
			super.delete(entity);
	}


	public Cliente findByNome(String descricao) {
		Cliente retornoInsumo = null;
		
		try {
			CriteriaQuery<Cliente> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (descricao != null && !descricao.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("descricao")), descricao.toLowerCase()));
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
}
