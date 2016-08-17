package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import lombok.Getter;

@Named
@ViewScoped
public class TopicoManager extends GenericCRUD<Topico, Integer, TopicoDAO> implements Serializable {
	private static final long serialVersionUID = 3925507169074921562L;
	
	@Getter
	private List<Cliente> lstCliente;

	
	@Override
	public void init() {
		showDeleteButton = true;
		lstCliente = carregaDescricao();
	}	

	@Override
	protected void beforeSave() {
		validarDados();
	}

	@Override
	protected void beforeUpdate() {
		validarDados();
	}
	
	public List<Cliente> carregaDescricao() throws ApplicationException{
		try {
			List<Cliente> lstDescricaoCliente;

			ClienteDAO clienteDAO = new ClienteDAO(entityManager);
			//lstDescricaoCliente= clienteDAO.findAll(Order.asc(Cliente_.descricao));
			
			lstDescricaoCliente = clienteDAO.findAll();
			
			return lstDescricaoCliente;
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(),e);
		}
	}
	
	
	private void validarDados() throws ApplicationException {
		try {
			TopicoDAO classeDAO = new TopicoDAO(entityManager);
			Topico byDescricao = classeDAO.findByNome(entity.getDescricao());
			
			if(byDescricao != null && !entity.equals(byDescricao)){
				throw new ApplicationException("Já existe um registro em Cliente com esta Descrição: " + entity.getDescricao());
			}
		} catch (DaoException e) {
			throw new ApplicationException(e.getMessage(),e);
		}
	}
}
