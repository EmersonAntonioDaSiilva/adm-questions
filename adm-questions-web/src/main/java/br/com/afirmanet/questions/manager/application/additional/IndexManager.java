package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.producer.ApplicationManaged;

@Named
@ViewScoped
public class IndexManager extends NaturalLanguage implements Serializable {
	private static final long serialVersionUID = 7201661374971816987L;

	@Inject
	@ApplicationManaged
	protected EntityManager entityManager;

	@Override
	protected void inicializar() {

	}
	
}
