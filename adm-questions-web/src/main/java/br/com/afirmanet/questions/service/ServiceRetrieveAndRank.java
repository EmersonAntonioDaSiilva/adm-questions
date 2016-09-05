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
import br.com.afirmanet.core.util.ApplicationPropertiesUtils;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import br.com.afirmanet.questions.factory.WatsonServiceFactory;
import br.com.afirmanet.questions.utils.HttpSolrClientUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceRetrieveAndRank extends WatsonServiceFactory implements Serializable {
	private static final long serialVersionUID = -452444688310099799L;

	@Getter
	private RetrieveAndRank service;
	
	private static final String NOME_CLUSTER = ApplicationPropertiesUtils.getValue("service.retrieve.and.rank.nome.cluster"),
			  					NOME_CONFIG = ApplicationPropertiesUtils.getValue("service.retrieve.and.rank.nome.config"),
			  					NOME_COLECTION = ApplicationPropertiesUtils.getValue("service.retrieve.and.rank.nome.colection");
	
	@Getter
	private String idClusterSolr;
	
	private EntityManager entityManager;

	public ServiceRetrieveAndRank(Cliente cliente, EntityManager entityManager) throws ApplicationException {
		this.entityManager = entityManager;
		setEntityManager(entityManager);
		
		setTypeServico(TypeServicoEnum.RETRIEVE_AND_RANK);
		setCliente(cliente);
		
		service = getServiceRR();
		createClusterSolr();
	}
	
	private void createClusterSolr() throws ApplicationException{
		
			// Recupera o identificador do cluster (se já foi criado)
			try {
				getClusterSolr();
				
				// se o identificador for nulo
				// é executado a rotina de criação da rotina do cluster
				if(idClusterSolr == null){
					createCluster(1); // cria cluster
					uploadConfiguration(); // configuração (arquivos xml)
					
					// service.createRanker("rankRH", training);
					service.createRanker("rankRH", new File("blitzkrieg.txt"));
					
					createCollection(); // criação da coleção
					indexDocumentAndCommit(); // indexa os documentos
				}
			} catch (Exception e) {
				throw new ApplicationException(e.getMessage(),e);
			}
		
	}
	
	private void uploadConfiguration() throws ApplicationException{
		try {
			String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
			caminho = caminho.concat("/resources/files/zip/solrconfig.zip");
			
			File configZip = new File(caminho);
			service.uploadSolrClusterConfigurationZip(idClusterSolr, NOME_CONFIG, configZip);
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
	}
	
	private void getClusterSolr() {
		
		SolrClusters listaClusterSolr = service.getSolrClusters();
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
		return service.getSolrCluster(idCluster);
	}

	private void createCluster(Integer unit) {
		SolrClusterOptions optionCluster = null;
		
		// Se a unit for null ele não definirá a unit size do cluster
		/// Com isso o cluster fica limitado a 500mb para testes
		if(unit == null)
		{
			optionCluster = new SolrClusterOptions(NOME_CLUSTER);
		}
		else
		{
			optionCluster = new SolrClusterOptions(NOME_CLUSTER,unit);
		}
		
		// Criação do cluster
		SolrCluster cluster = service.createSolrCluster(optionCluster);
		
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
	    createCollectionRequest.setCollectionName(NOME_COLECTION);
	    createCollectionRequest.setConfigName(NOME_CONFIG);
	    
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
			solrCliente.add(NOME_COLECTION,listDocument);
		    
			// Commit da coleção 
		    solrCliente.commit(NOME_COLECTION);
		}
	}
	
	public QueryResponse searchAllDocs(String pergunta) throws ApplicationException {
		QueryResponse response;
		
		try {
			HttpSolrClient solrClient = getSolrClient();
			
			String pesquisa = "".concat(pergunta); // monta String da pesquisa
			SolrQuery query = new SolrQuery(pesquisa); // cria os critérios da pesquisa
			
			response = solrClient.query(NOME_COLECTION, query); // retorna pesquisa
		
		} catch (SolrServerException e) {
			throw new ApplicationException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage(), e);
		}
		
		log.info(response.toString());
		
		return  response;
		
		
	}
	
}
