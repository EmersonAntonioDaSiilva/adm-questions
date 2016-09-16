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
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrConfigs;

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
	
	private static final String NOME_COLECTION = ApplicationPropertiesUtils.getValue("service.retrieve.and.rank.nome.colection");
	
	private EntityManager entityManager;

	public ServiceRetrieveAndRank(Cliente cliente, EntityManager entityManager) throws ApplicationException {
		this.entityManager = entityManager;
		setEntityManager(entityManager);
		
		setTypeServico(TypeServicoEnum.RETRIEVE_AND_RANK);
		setCliente(cliente);
		
		service = getServiceRR();
		getClusterSolr();
	}
	
	public void createClusterSolr(String nomeCluster, Integer unidadeCluster, String nomeConfig, String nomeCollection) throws ApplicationException{
			// Recupera o identificador do cluster (se já foi criado)
			try {
				SolrCluster sincronizar = sincronizarConfiguracao(nomeCluster);
				
				// se o identificador for nulo
				// é executado a rotina de criação da rotina do cluster
				if(sincronizar == null){
					String idClusterSolr = createCluster(nomeCluster, unidadeCluster); // cria cluster
					uploadConfiguration(idClusterSolr, nomeConfig); // configuração (arquivos xml)
					
					createCollection(idClusterSolr, nomeConfig, nomeCollection); // criação da coleção
					indexDocumentAndCommit(idClusterSolr,  nomeCollection); // indexa os documentos
				}
			} catch (Exception e) {
				throw new ApplicationException(e.getMessage(),e);
			}
	}
	
	public void updateClusterSolr(String nomeCluster, String nomeConfig, String nomeCollection) throws ApplicationException{
		// Recupera o identificador do cluster (se já foi criado)
		try {
			SolrCluster solrCluster = sincronizarConfiguracao(nomeCluster);
			
			// se o identificador for nulo
			// é executado a rotina de criação da rotina do cluster
			if(solrCluster != null){
				
				deleteCollectionSolr(solrCluster.getId(), nomeCollection);
				deleteConfigSolr(solrCluster.getId(), nomeConfig);
				
				uploadConfiguration(solrCluster.getId(), nomeConfig); // configuração (arquivos xml)
				
				createCollection(solrCluster.getId(), nomeConfig, nomeCollection); // criação da coleção
				indexDocumentAndCommit(solrCluster.getId(),  nomeCollection); // indexa os documentos
			} else {
				throw new ApplicationException("Cluster não encontrado");
			}
			
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(),e);
		}
	}
	
	
	
	public Boolean existClusterConfig(String solrClusterId, String nomeConfig) {
		Boolean retorno = Boolean.FALSE;
		SolrConfigs solrClusterConfigurations = service.getSolrClusterConfigurations(solrClusterId).execute();
		
		for (String solrConfig : solrClusterConfigurations.getSolrConfigs()) {
			if(nomeConfig.equals(solrConfig)) {
				retorno = Boolean.TRUE;
				break;
			}
		}
		
		return retorno;
	}
	
	public void uploadConfiguration(String idClusterSolr, String nomeConfig) throws ApplicationException{
		try {
			
			String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
			caminho = caminho.concat("/resources/files/zip/solrconfig");
			
			File config = new File(caminho);
			service.uploadSolrClusterConfigurationDirectory(idClusterSolr, nomeConfig, config).execute();
			// service.uploadSolrClusterConfigurationZip(idClusterSolr, NOME_CONFIG, configZip);
			
			
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
	}
	
	
	
	private String getClusterSolr() {
		String retorno = null;
		
		SolrClusters listaClusterSolr = service.getSolrClusters().execute();
		if(listaClusterSolr.getSolrClusters().size() > 0)
		{
			retorno = listaClusterSolr.getSolrClusters().get(0).getId();
		}
		
		return retorno;
	}
	
	public SolrCluster sincronizarConfiguracao(String nomeCluster) {
		
		SolrCluster retorno = null;
		
		SolrClusters listaClusterSolr = service.getSolrClusters().execute();
		if(listaClusterSolr.getSolrClusters().size() > 0)
		{
			
			for(SolrCluster cluster : listaClusterSolr.getSolrClusters()) {
				if(cluster.getName().equals(nomeCluster)) {
					retorno = cluster;
					break;
				}
			}
			
		}
		
		return retorno;
	}
	
	private SolrCluster getSolrCluster(String idCluster) {
		return service.getSolrCluster(idCluster).execute();
	}

	private String createCluster(String nomeCluster, Integer unit) {
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
		
		String idClusterSolr = cluster.getId();
		while (cluster.getStatus() == Status.NOT_AVAILABLE) {
		   
		   try{
			   Thread.sleep(30000);
			   cluster = getSolrCluster(cluster.getId());
		   }
		   catch(InterruptedException e){
			   idClusterSolr = null;
		   }
	    }
		
		return idClusterSolr;
		
	}

	public void deleteClusterSolr(String idClusterSolr) throws ApplicationException {
		service.deleteSolrCluster(idClusterSolr).execute();
	}

	public void deleteConfigSolr(String idClusterSolr, String configName) throws ApplicationException {
		service.deleteSolrClusterConfiguration(idClusterSolr, configName).execute();
	}

	public void deleteCollectionSolr(String idClusterSolr, String nomeCollection) throws Exception {
		final CollectionAdminRequest.Delete deleteCollectionRequest = new CollectionAdminRequest.Delete();
		deleteCollectionRequest.setCollectionName(nomeCollection);
		
	    final CollectionAdminResponse response = deleteCollectionRequest.process(getSolrClient(idClusterSolr)); // Executa a processo de criação da coleção
	    if (!response.isSuccess()) {
	    	throw new IllegalStateException("Falha ao criar collection: "+ response.getErrorMessages().toString());
	    }
	}

	//TODO trocar os metodos deprecation por um em operação
	@SuppressWarnings("deprecation")
	private void createCollection(String idCluster,String nomeConfig, String nomeCollection) throws Exception {
		// Criação da collection 
		/*final CollectionAdminRequest.Create createCollectionRequest = CollectionAdminRequest.createCollection(nomeColection, nomeConfig, 1, 1);*/
		
		final CollectionAdminRequest.Create createCollectionRequest = new CollectionAdminRequest.Create();
	    createCollectionRequest.setCollectionName(nomeCollection);
	    createCollectionRequest.setConfigName(nomeConfig);
	    
	    final CollectionAdminResponse response = createCollectionRequest.process(getSolrClient(idCluster)); // Executa a processo de criação da coleção
	    if (!response.isSuccess()) {
	    	throw new IllegalStateException("Falha ao criar collection: "+ response.getErrorMessages().toString());
	    }
	}
	
	private HttpSolrClient getSolrClient(String idCluster) {
		HttpSolrClient.Builder builderHttpSolrClient = new HttpSolrClient.Builder(service.getSolrUrl(idCluster));
		builderHttpSolrClient.withHttpClient(HttpSolrClientUtils.createHttpClient(service.getSolrUrl(idCluster), credenciais.getUsuario(), credenciais.getSenha()));
		return builderHttpSolrClient.build();
	}

	private void indexDocumentAndCommit(String idCluster, String nomeCollection) throws Exception {
		// Instância do Solr
		HttpSolrClient solrCliente = getSolrClient(idCluster);
		
		// Lista de documentos
		ServiceDocumentConversion serviceDocumentConversion = new ServiceDocumentConversion(getCliente(), entityManager);
		Collection<SolrInputDocument> listDocument = serviceDocumentConversion.getDadosDocumentConversion(); 
		
		// Avalia se tem documento a ser indexado
		if(listDocument.size() > 0){
			solrCliente.add(nomeCollection,listDocument);
		    
			// Commit da coleção 
		    solrCliente.commit(nomeCollection);
		}
	}
	
	public QueryResponse searchAllDocs(String pergunta) throws ApplicationException {
		return searchAllDocs(getClusterSolr(), pergunta);
	}
	
	public QueryResponse searchAllDocs(String idCluster, String pergunta) throws ApplicationException {
		QueryResponse response;
		
		try {
			HttpSolrClient solrClient = getSolrClient(idCluster);
			
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
