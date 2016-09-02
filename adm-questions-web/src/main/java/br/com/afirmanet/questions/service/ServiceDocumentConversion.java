package br.com.afirmanet.questions.service;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.apache.solr.common.SolrInputDocument;

import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;
import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import br.com.afirmanet.questions.factory.WatsonServiceFactory;
import lombok.Getter;

public class ServiceDocumentConversion extends WatsonServiceFactory implements Serializable {
	private static final long serialVersionUID = -452444688310099799L;

	@Getter
	private DocumentConversion service;

	public ServiceDocumentConversion(Cliente cliente, EntityManager entityManager) throws ApplicationException{
		setEntityManager(entityManager); 
		
		setTypeServico(TypeServicoEnum.DOCUMENT_CONVERSION);
		setCliente(cliente);
		
		service = getServiceDC();
		
	}
	
	protected Collection<SolrInputDocument> getDadosDocumentConversion() throws DaoException{
		Collection<SolrInputDocument> retorno;
		retorno = Collections.emptyList();

		try {
			
			String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
			caminho = caminho + "/resources/files/document/";
			
			File arquivos[];
			File diretorio = new File(caminho);
			arquivos = diretorio.listFiles();

			//JsonObject customConfigDC = getCustomConfigDC();
			
			if(arquivos.length > 0){
				
				retorno = new ArrayList<SolrInputDocument>();
				
				for(int i = 0; i < arquivos.length; i++){
					// TODO Colocar as regras de validação para os documentos
					//String mediaTypeFromFile = ConversionUtils.getMediaTypeFromFile(arquivos[i]);
					Answers execute = service.convertDocumentToAnswer(arquivos[i]).execute();
					
					SolrInputDocument document = new SolrInputDocument();
					document.addField("id", execute.getAnswerUnits().get(0).getId());
					document.addField("title", execute.getAnswerUnits().get(0).getTitle());
					document.addField("body", execute.getAnswerUnits().get(0).getContent().get(0).getText());
					
					retorno.add(document);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DaoException("Não foi possível retornar os documentos", e);
		}
				
		return retorno;
	}
	
//	private JsonObject getCustomConfigDC() {
//		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
//		caminho = caminho + "/resources/files/json/configDocument.json";
//		JsonObject retorno = null;
//		
//		try {
//			JsonParser parser = new JsonParser();			
//			BufferedReader br = new BufferedReader(new FileReader(caminho));
//
//			retorno =  (JsonObject) parser.parse(br);
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return retorno;
//	}

}
