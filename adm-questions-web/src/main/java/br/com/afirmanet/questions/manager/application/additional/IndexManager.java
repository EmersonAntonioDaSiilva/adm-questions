package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.manager.AbstractManager;
import br.com.afirmanet.core.producer.ApplicationManaged;

@Named
@ViewScoped
public class IndexManager extends AbstractManager implements Serializable {
	private static final long serialVersionUID = 7201661374971816987L;

	@Inject
	@ApplicationManaged
	protected EntityManager entityManager;

	@PostConstruct
	protected void inicializar() {

	}
	
}
