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
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.persistence.GenericDAO;
import br.com.afirmanet.questions.entity.UsuarioPerfil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor
@TransactionManagement(TransactionManagementType.CONTAINER)
public @Stateless class UsuarioPerfilDAO extends GenericDAO<UsuarioPerfil, Integer> implements Serializable {
	private static final long serialVersionUID = 6907863285648197379L;

	public UsuarioPerfilDAO(EntityManager entityManager) {
		super(entityManager);
	}

	private Collection<Predicate> createPredicates() {
		Collection<Predicate> predicates = new ArrayList<>();

		
		return predicates;
	}
	
	@Override
	@SuppressWarnings("unused")
	public Collection<Predicate> createPaginationPredicates(UsuarioPerfil entity) {
		Collection<Predicate> predicates = createPredicates();

		return super.createPaginationPredicates(entity);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(UsuarioPerfil entity) {
			super.delete(entity);
	}


	public UsuarioPerfil findByEmail(String email) throws DaoException {
		UsuarioPerfil retornoUsuarioPerfil = null;
		
		try {
			CriteriaQuery<UsuarioPerfil> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (email != null && !email.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("email")), email.toLowerCase()));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoUsuarioPerfil = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
			
		} catch (NoResultException e){
			retornoUsuarioPerfil = null;
			
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
		return retornoUsuarioPerfil;

	}

	public UsuarioPerfil findByIdCliente(String idCliente) {
		UsuarioPerfil retornoUsuarioPerfil = null;
		
		try {
			CriteriaQuery<UsuarioPerfil> criteriaQuery = createCriteriaQuery();
			Collection<Predicate> predicates = new ArrayList<>();

			if (idCliente != null && !idCliente.isEmpty()) {
				predicates.add(cb.equal(root.get("clientId"), idCliente));
			}

			if(!predicates.isEmpty()){
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));

				retornoUsuarioPerfil = entityManager.createQuery(criteriaQuery).getSingleResult();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return retornoUsuarioPerfil;
	}

}
