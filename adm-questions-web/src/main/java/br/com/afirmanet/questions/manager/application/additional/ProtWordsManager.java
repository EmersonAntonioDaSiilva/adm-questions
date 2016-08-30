package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.ProtWordsDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.ProtWords;
import lombok.Getter;

@Named
@ViewScoped
public class ProtWordsManager extends GenericCRUD<ProtWords, Integer, ProtWordsDAO> implements Serializable {

	private static final long serialVersionUID = -2311457469252338822L;
	
	
	@Getter
	private List<Cliente> lstCliente;

	
	@Override
	public void init() {
		showDeleteButton = true;
		
		ClienteDAO clienteDAO = new ClienteDAO(entityManager);
		lstCliente = clienteDAO.findAll();
	}	

	@Override
	protected void beforeSave() {
		validarDados();
	}

	@Override
	protected void beforeUpdate() {
		validarDados();
	}	
	
	private void validarDados() throws ApplicationException {
		try {
			ProtWordsDAO protWordsDAO = new ProtWordsDAO(entityManager);
			ProtWords byDescricao = protWordsDAO.findByNome(entity.getDescricao());
			
			if(byDescricao != null && !entity.equals(byDescricao)){
				throw new ApplicationException("Já existe um registro uma ProtWords com esta Descrição: " + entity.getDescricao());
			}
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(),e);
		}
	}
}
