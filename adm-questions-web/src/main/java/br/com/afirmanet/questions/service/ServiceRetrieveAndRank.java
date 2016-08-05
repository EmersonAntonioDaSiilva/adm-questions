package br.com.afirmanet.questions.service;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusterOptions;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster.Status;

import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import br.com.afirmanet.questions.factory.WatsonServiceFactory;
import br.com.afirmanet.questions.manager.vo.SolrResult;
import br.com.afirmanet.questions.utils.HttpSolrClientUtils;
import lombok.Getter;

public class ServiceRetrieveAndRank extends WatsonServiceFactory implements Serializable {
	private static final long serialVersionUID = -452444688310099799L;

	@Getter
	private RetrieveAndRank service;

	public ServiceRetrieveAndRank(Cliente cliente, EntityManager entityManager){
		super(entityManager);
		
		setTypeServico(TypeServicoEnum.RETRIEVE_AND_RANK);
		setCliente(cliente);
		
		service = getServiceRR();
		
	}
	
	public void uploadConfiguration(String idCluster, String nomeConfig) {
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho.concat("/resources/files/zip/solrconfig.zip");
		
		File configZip = new File(caminho);
		service.uploadSolrClusterConfigurationZip(idCluster, nomeConfig, configZip).execute();
	}

	private SolrCluster getSolrCluster(String idCluster) {
		return service.getSolrCluster(idCluster).execute();
	}

	public String createCluster(String nomeCluster, Integer unit) {
		SolrClusterOptions optionCluster = null;
		
		// Se a unit for null ele não definirá a unit size do cluster
		/// Com isso o cluster fica limitado a 500mb para testes
		if(unit == null)
		{
			optionCluster = new SolrClusterOptions(nomeCluster);
		}
		else
		{
			optionCluster = new SolrClusterOptions(nomeCluster,unit);
		}
		
		// Criação do cluster
		SolrCluster cluster = service.createSolrCluster(optionCluster).execute();
		while (cluster.getStatus() == Status.NOT_AVAILABLE) {
		   
		   try{
			   Thread.sleep(30000);
			   cluster = getSolrCluster(cluster.getId());
		   }
		   catch(InterruptedException e){
			   return null; 
		   }
	    }
		
		// Retorno do id do cluster
		return cluster.getId();
	}

	public void createCollection(String idCluster, String nomeConfig, String nomeColection) throws Exception {
		// Criação da collection
		/*final CollectionAdminRequest.Create createCollectionRequest = CollectionAdminRequest.createCollection(nomeColection, nomeConfig, 1, 1);*/
		
		final CollectionAdminRequest.Create createCollectionRequest = new CollectionAdminRequest.Create();
	    createCollectionRequest.setCollectionName(nomeColection);
	    createCollectionRequest.setConfigName(nomeConfig);
	    
	    final CollectionAdminResponse response = createCollectionRequest.process(getSolrClient(idCluster));
	    if (!response.isSuccess()) {
	    	throw new IllegalStateException("Falha ao criar collection: "+ response.getErrorMessages().toString());
	    }
	}
	
	public HttpSolrClient getSolrClient(String idCluster) {
		HttpSolrClient.Builder builderHttpSolrClient = new HttpSolrClient.Builder(service.getSolrUrl(idCluster));
		builderHttpSolrClient.withHttpClient(HttpSolrClientUtils.createHttpClient(service.getSolrUrl(idCluster), credenciais.getUsuario(), credenciais.getSenha()));
		return builderHttpSolrClient.build();
	}

	public void indexDocumentAndCommit(String idCluster, String nomeCollection) throws Exception{
		// Instância do Solr
		HttpSolrClient solrCliente = getSolrClient(idCluster);
		
		// Lista de documentos
		solrCliente.add(nomeCollection, getDocuments());
	    
		// Commit da coleção 
	    solrCliente.commit(nomeCollection);
	}
	
	private Collection<SolrInputDocument> getDocuments(){
		
		Collection<SolrInputDocument> colecao = Collections.emptyList();
		
		Gson gson = new Gson();
		
		ServiceDocumentConversion serviceDocumentConversion = new ServiceDocumentConversion(getCliente(), entityManager);
		
		JsonArray jsonArray = serviceDocumentConversion.getDadosDocumentConversion();
    	
		if(jsonArray.size() > 0)
		{
			colecao = new ArrayList<SolrInputDocument>();
			
			for(JsonElement jsonElement :  jsonArray){
				JsonObject JsonObject =  jsonElement.getAsJsonObject();
				SolrResult fromJson2 = gson.fromJson(JsonObject, SolrResult.class);
				
				SolrInputDocument document = new SolrInputDocument();
				document.addField("id", fromJson2.getId());
    			document.addField("title", fromJson2.getTitle());
    			document.addField("body", fromJson2.getBody());
    			colecao.add(document);
				
			}
		}
    	
		return colecao;
	}

	public QueryResponse searchAllDocs(String idCluster,String collection,String pergunta) throws Exception {
		HttpSolrClient solrClient = getSolrClient(idCluster);
		
		String pesquisa = "*:".concat(pergunta); // monta String da pesquisa
		SolrQuery query = new SolrQuery(pesquisa); // cria os critérios da pesquisa
		return  solrClient.query(collection, query); // retorna pesquisa
	}

	public void cleanupResources() {

	}	
}
