package br.com.afirmanet.core.util;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Classe utilitária para manipulação de objetos gerenciados pelo JPA (Java Persistence API) .
 * </p>
 */
@Slf4j
public final class PersistenceUtils {

	private static Map<String, EntityManagerFactory> entityManagerFactoryMap = new HashMap<>();

	private PersistenceUtils() {
		super();
	}

	public static EntityManagerFactory getEntityManagerFactory(String entityManagerFactoryName) {
		EntityManagerFactory entityManagerFactory = entityManagerFactoryMap.get(entityManagerFactoryName);
		try {
			if (entityManagerFactory == null) {
				log.debug("Recuperando o EntityManagerFactory do contexto");
				InitialContext initialContext = new InitialContext();
				entityManagerFactory = (EntityManagerFactory) initialContext.lookup(entityManagerFactoryName);
				entityManagerFactoryMap.put(entityManagerFactoryName, entityManagerFactory);
			}

		} catch (NamingException e) {
			log.error("Erro ao recuperar o EntityManagerFactory do contexto", e);
		}

		return entityManagerFactory;
	}

	public static EntityManager getEntityManager(String entityManagerFactoryName) {
		EntityManager entityManager = getEntityManagerFactory(entityManagerFactoryName).createEntityManager();
		return entityManager;
	}

	public static UserTransaction joinTransaction(EntityManager entityManager) {
		try {
			UserTransaction userTransaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
			userTransaction.begin();

			entityManager.joinTransaction();

			return userTransaction;

		} catch (NamingException | NotSupportedException | SystemException e) {
			throw new br.com.afirmanet.core.exception.SystemException(e);
		}
	}
}
