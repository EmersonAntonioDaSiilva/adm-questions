package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.enumeration.CrudActionEnum;
import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.DadosWatsonDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.DadosWatson;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import lombok.Getter;

@Named
@ViewScoped
public class DadosWatsonManager extends GenericCRUD<DadosWatson, Integer, DadosWatsonDAO> implements Serializable {
	private static final long serialVersionUID = 5877400765940464387L;

	@Inject
	@ApplicationManaged
	protected EntityManager entityManager;

	@Getter
	private List<Cliente> lstCliente;
	
	@Getter
	private List<Topico> lstTopico;	
	
	@Getter
	private List<TypeServicoEnum> lstTypeServicoEnum;
	
	
	@PostConstruct
	public void init() {		
		ClienteDAO clienteDAO = new ClienteDAO(entityManager);
		lstCliente = clienteDAO.findAll();
		
		lstTypeServicoEnum = new ArrayList<>();
		lstTypeServicoEnum.addAll(Arrays.asList(TypeServicoEnum.values()));
		Collections.sort(lstTypeServicoEnum, (o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	@Override
	public void prepareInsert() {
		super.prepareInsert();
		
		if(searchParam.getCliente() != null){
			entity.setCliente(searchParam.getCliente());
		}
		
		if(searchParam.getTopico() != null){
			entity.setTopico(searchParam.getTopico());
		}
		
		if(searchParam.getTypeServico() != null){
			entity.setTypeServico(searchParam.getTypeServico());
		}
	}

	
	public void carregaTopicos(){
		try {
			TopicoDAO classeDAO = new TopicoDAO(entityManager);

			if(currentAction.equals(CrudActionEnum.SEARCH)){
				lstTopico = classeDAO.findbyCliente(searchParam.getCliente());
			}else{
				lstTopico = classeDAO.findbyCliente(entity.getCliente());
			}
		} catch (DaoException e) {
			addErrorMessage(e.getMessage(),e);
		}
	}	
	
	@Override
	protected void beforeSave() {
		entity.setDataCadastro(LocalDate.now());
	}
}
