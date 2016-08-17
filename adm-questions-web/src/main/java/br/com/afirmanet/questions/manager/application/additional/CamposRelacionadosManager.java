package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.enumeration.CrudActionEnum;
import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.questions.dao.CamposRelacionadosDAO;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.CamposRelacionados;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Resposta;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.enums.TypeDocEnum;
import lombok.Getter;

@Named
@ViewScoped
public class CamposRelacionadosManager extends GenericCRUD<CamposRelacionados, Integer, CamposRelacionadosDAO> implements Serializable {
	private static final long serialVersionUID = 3925507169074921562L;
	
	@Getter
	private List<Cliente> lstCliente;
		
	@Getter
	private List<Topico> lstTopico;
	
	@Getter
	private List<TypeDocEnum> lstTypoDocEnum;
	

	@Override
	public void init() {
		showDeleteButton = true;
		
		ClienteDAO clienteDAO = new ClienteDAO(entityManager);
		lstCliente = clienteDAO.findAll();

		lstTypoDocEnum = new ArrayList<>();
		lstTypoDocEnum.addAll(Arrays.asList(TypeDocEnum.values()));
	}	

	@Override
	protected void beforeSave() {
		validarDados();
	}

	@Override
	protected void beforeUpdate() {
		validarDados();
	}
	
	@Override
	protected void afterUpdate() {
		beforeDetail();
	}
	
	public void carregaDescricaoClasse()throws ApplicationException{
		try {
			TopicoDAO classeDAO = new TopicoDAO(entityManager);
			
			if(currentAction.equals(CrudActionEnum.SEARCH)){
				lstTopico = classeDAO.findbyCliente(searchParam.getCliente());
			}else{
				lstTopico = classeDAO.findbyCliente(entity.getCliente());
			}
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(),e);
		}
	}
	
	private void validarDados() throws ApplicationException {
		try {
			RespostaDAO respostaDAO = new RespostaDAO(entityManager);
			Resposta byDescricao = respostaDAO.findByNome(entity.getCampo());
			
			if(byDescricao != null && !entity.equals(byDescricao)){
				throw new ApplicationException("Já existe um registro em Resposta com esta Descrição: " + entity.getCampo());
			}
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(),e);
		}
	}
}
