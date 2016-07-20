package br.com.afirmanet.questions.manager;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import br.com.afirmanet.core.manager.AbstractManager;

@Named
@SessionScoped
public class HorizontalMenuManager extends AbstractManager implements Serializable {

	private static final long serialVersionUID = 6424169987014727200L;


	@PostConstruct
	public void criarMenus() {

	}
}
