package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;

import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.entity.Cliente;

@Named
@ViewScoped
public class ClienteManager extends GenericCRUD<Cliente, Integer, ClienteDAO> implements Serializable {
	private static final long serialVersionUID = 3925507169074921562L;
	
	@Override
	public void init() {
		showDeleteButton = true;
	}	

	@Override
	protected void beforeSave() {
		validarDados();
	}

	@Override
	protected void beforeUpdate() {
		validarDados();
	}
	

	
	private void validarDados() {
		ClienteDAO clienteDAO = new ClienteDAO(entityManager);
		Cliente byDescricao = clienteDAO.findByNome(entity.getDescricao());
		
		if(byDescricao != null && !entity.equals(byDescricao)){
			throw new ApplicationException("Já existe um registro em Cliente com esta Descição: " + entity.getDescricao());
		}
	}
}
