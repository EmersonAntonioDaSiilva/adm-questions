package br.com.afirmanet.questions.manager.application.additional;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.questions.constante.ExtensaoEnum;
import br.com.afirmanet.questions.constante.SolrConfigEnum;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.ProtWordsDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.ProtWords;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;
import br.com.afirmanet.questions.utils.ProtWordsArquivoUtils;
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
	public void prepareInsert() {
		super.prepareInsert();
		
		if(searchParam.getCliente() != null){
			entity.setCliente(searchParam.getCliente());
		}
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
	
	public void gerarArquivoTxt() {
		ProtWordsDAO protWordsDAO = new ProtWordsDAO(entityManager);
		List<ProtWords> protwords = protWordsDAO.findAll();
		
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho + ApplicationPropertiesUtils.getValue("path.txt.solr");
		
		ProtWordsArquivoUtils protWordsArquivo = new ProtWordsArquivoUtils();
		File file = protWordsArquivo.criarDiretorioEArquivo(caminho, ExtensaoEnum.TXT.getSufixo(), SolrConfigEnum.PROTOWORDS.getNomeArquivo());
		protWordsArquivo.gravarArquivoTxt(file, protwords);
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("generatedFile", new FacesMessage("Sucesso", "Arquivo gerado com sucesso"));
	}
}
