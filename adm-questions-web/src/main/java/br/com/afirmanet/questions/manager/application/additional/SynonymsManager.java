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
import br.com.afirmanet.questions.dao.SynonymsDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Synonyms;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;
import br.com.afirmanet.questions.utils.ArquivoUtils;
import br.com.afirmanet.questions.utils.SynonymsArquivoUtils;
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
		
		if(entity.getMapeado() && !entity.getDescricao().contains("=>")) {
			throw new ApplicationException("Padrão para sinônimos mapeando não conhecido, favor usar PALAVRA=>sinonimo1,sinonimo2...");
		}
	}
	
	private void validarRegistro() throws ApplicationException {
		try {
			SynonymsDAO synonymsDAO = new SynonymsDAO(entityManager);
			Synonyms byDescricao = synonymsDAO.findByDescricao(entity.getDescricao(), entity.getMapeado());
			
			if(byDescricao != null && !entity.equals(byDescricao)){
				throw new ApplicationException("Já existe um registro uma Synonyms com esta Descrição: " + entity.getDescricao());
			}
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(),e);
		}
	}
	
	public void gerarArquivoTxt() {
		SynonymsDAO synonymsDAO = new SynonymsDAO(entityManager);
		List<Synonyms> symnonyms = synonymsDAO.findAll();
		
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho + ApplicationPropertiesUtils.getValue("path.txt.solr");
		
		SynonymsArquivoUtils synonymsArquivo = new SynonymsArquivoUtils();
		File file = synonymsArquivo.criarDiretorioEArquivo(caminho, ExtensaoEnum.TXT.getSufixo(), SolrConfigEnum.SYNONYMS.getNomeArquivo());
		synonymsArquivo.gravarArquivoTxt(file, symnonyms);
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("generatedFile", new FacesMessage("Sucesso", "Arquivo gerado com sucesso"));
	}
}
