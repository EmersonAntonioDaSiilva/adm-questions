package br.com.afirmanet.questions.manager;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.primefaces.model.menu.MenuModel;

import br.com.afirmanet.core.manager.AbstractManager;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class HorizontalMenuManager extends AbstractManager implements Serializable {

	private static final long serialVersionUID = 6424169987014727200L;

	@Getter
	@Setter
	private MenuModel menuModel;

	@PostConstruct
	public void criarMenus() {

	}
}
