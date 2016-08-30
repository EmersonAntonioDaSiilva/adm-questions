package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.SynonymsDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Synonyms;
import lombok.Getter;

@Named
@ViewScoped
public class SynonymsManager  extends GenericCRUD<Synonyms, Integer, SynonymsDAO> implements Serializable {
	
	private static final long serialVersionUID = 8204659315590143728L;
	
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
		validarDados(); // verifica se os campos estão preenchidos
		validarRegistro(); // verifica o registro já foi cadastrado
	}

	@Override
	protected void beforeUpdate() {
		validarDados(); // verifica se os campos estão preenchidos
		validarRegistro(); // verifica o registro já foi cadastrado
	}
	
	private void validarDados() throws ApplicationException {
		if(entity.getCliente() == null){
			throw new ApplicationException("Cliente não informado.");
		}
		
		if(entity.getDescricao() == null){
			throw new ApplicationException("Grupo de Synonyms não informado.");
		}
	}
	
	private void validarRegistro() throws ApplicationException {
		try {
			SynonymsDAO synonymsDAO = new SynonymsDAO(entityManager);
			Synonyms byDescricao = synonymsDAO.findByDescricao(entity.getDescricao());
			
			if(byDescricao != null && !entity.equals(byDescricao)){
				throw new ApplicationException("Já existe um registro uma Synonyms com esta Descrição: " + entity.getDescricao());
			}
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(),e);
		}
	}
}
