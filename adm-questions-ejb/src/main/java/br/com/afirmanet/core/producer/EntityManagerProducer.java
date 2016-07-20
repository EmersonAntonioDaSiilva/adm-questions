package br.com.afirmanet.core.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class EntityManagerProducer {

	@PersistenceContext
	private EntityManager entityManager;

	@Produces
	@Default
	@RequestScoped
	public EntityManager createEntityManager() {
		return entityManager;
	}

}
