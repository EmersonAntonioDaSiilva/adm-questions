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
import br.com.afirmanet.questions.dao.StopWordsDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.StopWords;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;
import br.com.afirmanet.questions.utils.StopWordsArquivoUtils;
import lombok.Getter;

@Named
@ViewScoped
public class StopWordsManager extends GenericCRUD<StopWords, Integer, StopWordsDAO> implements Serializable {
	private static final long serialVersionUID = 3925507169074921562L;
	
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
			StopWordsDAO stopWordsDAO = new StopWordsDAO(entityManager);
			StopWords byDescricao = stopWordsDAO.findByNome(entity.getDescricao());
			
			if(byDescricao != null && !entity.equals(byDescricao)){
				throw new ApplicationException("Já existe um registro uma StopWords com esta Descrição: " + entity.getDescricao());
			}
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(),e);
		}
	}
	
	public void gerarArquivoTxt() {
		StopWordsDAO stopWordsDAO = new StopWordsDAO(entityManager);
		List<StopWords> stopWords = stopWordsDAO.findAll();
		
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho + ApplicationPropertiesUtils.getValue("path.txt.solr");
		
		StopWordsArquivoUtils stopWordsArquivo = new StopWordsArquivoUtils();
		File file = stopWordsArquivo.criarDiretorioEArquivo(caminho, ExtensaoEnum.TXT.getSufixo(), SolrConfigEnum.STOPWORDS.getNomeArquivo());
		stopWordsArquivo.gravarArquivoTxt(file, stopWords);
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("generatedFile", new FacesMessage("Sucesso", "Arquivo gerado com sucesso"));
	}
}
