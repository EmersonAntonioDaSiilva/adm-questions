package br.com.afirmanet.questions.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.apache.solr.common.SolrInputDocument;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;
import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;
import com.ibm.watson.developer_cloud.document_conversion.v1.util.ConversionUtils;

import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import br.com.afirmanet.questions.factory.WatsonServiceFactory;
import lombok.Getter;

public class ServiceDocumentConversion extends WatsonServiceFactory implements Serializable {
	private static final long serialVersionUID = -452444688310099799L;

	@Getter
	private DocumentConversion service;

	public ServiceDocumentConversion(Cliente cliente, EntityManager entityManager){
		super(entityManager);
		
		setTypeServico(TypeServicoEnum.DOCUMENT_CONVERSION);
		setCliente(cliente);
		
		service = getServiceDC();
		
	}
	
	protected Collection<SolrInputDocument> getDadosDocumentConversion(){
		Collection<SolrInputDocument> retorno = Collections.emptyList();
		
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho + "/resources/files/document/";
		
		File arquivos[];
		File diretorio = new File(caminho);
		arquivos = diretorio.listFiles();

		JsonObject customConfigDC = getCustomConfigDC();
		
		for(int i = 0; i < arquivos.length; i++){
			String mediaTypeFromFile = ConversionUtils.getMediaTypeFromFile(arquivos[i]);
			Answers execute = service.convertDocumentToAnswer(arquivos[i]).execute();
			
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", execute.getAnswerUnits().get(0).getId());
			document.addField("title", execute.getAnswerUnits().get(0).getTitle());
			document.addField("body", execute.getAnswerUnits().get(0).getContent().get(0).getText());
			
			retorno.add(document);
		}
				
		return retorno;
	}
	
	private JsonObject getCustomConfigDC() {
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho + "/resources/files/json/configDocument.json";
		JsonObject retorno = null;
		
		try {
			JsonParser parser = new JsonParser();			
			BufferedReader br = new BufferedReader(new FileReader(caminho));

			retorno =  (JsonObject) parser.parse(br);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retorno;
	}

}
