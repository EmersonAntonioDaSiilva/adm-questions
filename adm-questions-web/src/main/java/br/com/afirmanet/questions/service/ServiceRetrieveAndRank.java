package br.com.afirmanet.questions.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster.Status;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusterOptions;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusters;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import br.com.afirmanet.questions.factory.WatsonServiceFactory;
import br.com.afirmanet.questions.utils.HttpSolrClientUtils;
import lombok.Getter;

public class ServiceRetrieveAndRank extends WatsonServiceFactory implements Serializable {
	private static final long serialVersionUID = -452444688310099799L;

	@Getter
	private RetrieveAndRank service;
	
	private static String nomeCluster = "MAGNA_RH_SOLR",
	  					  nomeConfig = "CONF_RH",
			  			  nomeColection = "COLLEC_RH_FERIAS";
	
	@Getter
	private String idClusterSolr;
	

	public ServiceRetrieveAndRank(Cliente cliente, EntityManager entityManager) throws ApplicationException {
		super(entityManager);
		
		setTypeServico(TypeServicoEnum.RETRIEVE_AND_RANK);
		setCliente(cliente);
		
		service = getServiceRR();
		createClusterSolr();
	}
	
	private void createClusterSolr() throws ApplicationException{
		
		try{
			// Recupera o identificador do cluster (se já foi criado)
			getClusterSolr();
			
			// se o identificador for nulo
			// é executado a rotina de criação da rotina do cluster
			if(idClusterSolr == null){
				createCluster(1); // cria cluster
				uploadConfiguration(); // configuração (arquivos xml)
				createCollection(); // criação da coleção
				indexDocumentAndCommit(); // indexa os documentos
			}
			
		}catch(Exception e){
			throw new ApplicationException(e);
		}
		
	}
	
	private void uploadConfiguration() {
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho.concat("/resources/files/zip/solrconfig.zip");
		
		File configZip = new File(caminho);
		service.uploadSolrClusterConfigurationZip(idClusterSolr, nomeConfig, configZip);
	}
	
	private void getClusterSolr() {
		
		SolrClusters listaClusterSolr = service.getSolrClusters().execute();
		if(listaClusterSolr.getSolrClusters().size() > 0)
		{
			idClusterSolr = listaClusterSolr.getSolrClusters().get(0).getId();
		}
		else
		{
			idClusterSolr = null;
		}
	}
	
	private SolrCluster getSolrCluster(String idCluster) {
		return service.getSolrCluster(idCluster).execute();
	}

	private void createCluster(Integer unit) {
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
		
		idClusterSolr = cluster.getId();
		while (cluster.getStatus() == Status.NOT_AVAILABLE) {
		   
		   try{
			   Thread.sleep(30000);
			   cluster = getSolrCluster(cluster.getId());
		   }
		   catch(InterruptedException e){
			   idClusterSolr = null;
		   }
	    }
		
	}

	//TODO trocar os metodos deprecation por um em operação
	@SuppressWarnings("deprecation")
	private void createCollection() throws Exception {
		// Criação da collection
		/*final CollectionAdminRequest.Create createCollectionRequest = CollectionAdminRequest.createCollection(nomeColection, nomeConfig, 1, 1);*/
		
		final CollectionAdminRequest.Create createCollectionRequest = new CollectionAdminRequest.Create();
	    createCollectionRequest.setCollectionName(nomeColection);
	    createCollectionRequest.setConfigName(nomeConfig);
	    
	    final CollectionAdminResponse response = createCollectionRequest.process(getSolrClient()); // Executa a processo de criação da coleção
	    if (!response.isSuccess()) {
	    	throw new IllegalStateException("Falha ao criar collection: "+ response.getErrorMessages().toString());
	    }
	}
	
	private HttpSolrClient getSolrClient() {
		HttpSolrClient.Builder builderHttpSolrClient = new HttpSolrClient.Builder(service.getSolrUrl(idClusterSolr));
		builderHttpSolrClient.withHttpClient(HttpSolrClientUtils.createHttpClient(service.getSolrUrl(idClusterSolr), credenciais.getUsuario(), credenciais.getSenha()));
		return builderHttpSolrClient.build();
	}

	private void indexDocumentAndCommit() throws Exception{
		// Instância do Solr
		HttpSolrClient solrCliente = getSolrClient();
		
		// Lista de documentos
		ServiceDocumentConversion serviceDocumentConversion = new ServiceDocumentConversion(getCliente(), entityManager);
		Collection<SolrInputDocument> listDocument = serviceDocumentConversion.getDadosDocumentConversion(); 
		
		// Avalia se tem documento a ser indexado
		if(listDocument.size() > 0){
			solrCliente.add(nomeColection,listDocument);
		    
			// Commit da coleção 
		    solrCliente.commit(nomeColection);
		}
	}
	
	public QueryResponse searchAllDocs(String pergunta) throws ApplicationException {
		QueryResponse response;
		
		try {
			HttpSolrClient solrClient = getSolrClient();
			
			String pesquisa = "".concat(pergunta); // monta String da pesquisa
			SolrQuery query = new SolrQuery(pesquisa); // cria os critérios da pesquisa
			
			response = solrClient.query(nomeColection, query); // retorna pesquisa
		
		} catch (SolrServerException e) {
			throw new ApplicationException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage(), e);
		}
		
		return  response;
		
		
	}
	
}
