package br.com.afirmanet.questions.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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
import br.com.afirmanet.questions.dao.ClusterSolrDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.ClusterSolr;
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
	
	private EntityManager entityManager;
	private ClusterSolr clusterSolr;
	
	public ServiceRetrieveAndRank(Cliente cliente, EntityManager entityManager) throws ApplicationException {
		this.entityManager = entityManager;
		setEntityManager(entityManager);
		
		setTypeServico(TypeServicoEnum.RETRIEVE_AND_RANK);
		setCliente(cliente);

		ClusterSolrDAO clusterSolrDAO = new ClusterSolrDAO(entityManager);
		clusterSolr = clusterSolrDAO.findByCliente(getCliente());

		service = getServiceRR();
		getClusterSolr();
	}
	
	public void createClusterSolr() throws ApplicationException{
			// Recupera o identificador do cluster (se já foi criado)
			try {
				SolrCluster sincronizar = sincronizarConfiguracao();
				
				// se o identificador for nulo
				// é executado a rotina de criação da rotina do cluster
				if(sincronizar == null){
					String idClusterSolr = createCluster(clusterSolr.getNomeCluster(), clusterSolr.getUnitCluster()); // cria cluster
					uploadConfiguration(); // configuração (arquivos xml)
					
					createCollection(idClusterSolr, clusterSolr.getNomeConfig(), clusterSolr.getNomeCollection()); // criação da coleção
					indexDocumentAndCommit(idClusterSolr,  clusterSolr.getNomeCollection()); // indexa os documentos
				}
			} catch (Exception e) {
				throw new ApplicationException(e.getMessage(),e);
			}
	}
	
	public void updateClusterSolr() throws ApplicationException{
		// Recupera o identificador do cluster (se já foi criado)
		try {
			SolrCluster solrCluster = sincronizarConfiguracao();
			
			// se o identificador for nulo
			// é executado a rotina de criação da rotina do cluster
			if(solrCluster != null){
				
				deleteCollectionSolr();
				deleteConfigSolr();
				
				uploadConfiguration(); // configuração (arquivos xml)
				
				createCollection(solrCluster.getId(), clusterSolr.getNomeConfig(), clusterSolr.getNomeCollection()); // criação da coleção
				indexDocumentAndCommit(solrCluster.getId(), clusterSolr.getNomeCollection()); // indexa os documentos
			} else {
				throw new ApplicationException("Cluster não encontrado");
			}
			
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(),e);
		}
	}
	
	
	
	public Boolean existClusterConfig() {
		Boolean retorno = Boolean.FALSE;
		List<String> solrClusterConfigurations = service.getSolrClusterConfigurations(clusterSolr.getIdCluster());
		
		for (String solrConfig : solrClusterConfigurations) {
			if(clusterSolr.getNomeConfig().equals(solrConfig)) {
				retorno = Boolean.TRUE;
				break;
			}
		}
		
		return retorno;
	}
	
	public void uploadConfiguration() throws ApplicationException{
		try {
			
			String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
			caminho = caminho.concat("/resources/files/zip/solrconfig");
			
			File config = new File(caminho);
			service.uploadSolrClusterConfigurationDirectory(clusterSolr.getIdCluster(), clusterSolr.getNomeConfig(), config);
			
			
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
	}
	
	
	
	private String getClusterSolr() {
		String retorno = null;

		SolrClusters listaClusterSolr = service.getSolrClusters();
		if(listaClusterSolr.getSolrClusters().size() > 0)
		{
			retorno = listaClusterSolr.getSolrClusters().get(0).getId();
		}
		
		return retorno;
	}
	
	public SolrCluster sincronizarConfiguracao() {
		
		SolrCluster retorno = null;
		
		SolrClusters listaClusterSolr = service.getSolrClusters();
		if(listaClusterSolr.getSolrClusters().size() > 0)
		{
			
			for(SolrCluster cluster : listaClusterSolr.getSolrClusters()) {
				if(cluster.getName().equals(clusterSolr.getNomeCluster())) {
					retorno = cluster;
					break;
				}
			}
			
		}
		
		return retorno;
	}
	
	private SolrCluster getSolrCluster(String idCluster) {
		return service.getSolrCluster(idCluster);
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
		SolrCluster cluster = service.createSolrCluster(optionCluster);
		
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

	public void deleteClusterSolr() throws ApplicationException {
		service.deleteSolrCluster(clusterSolr.getIdCluster());
	}

	public void deleteConfigSolr() throws ApplicationException {
		service.deleteSolrClusterConfiguration(clusterSolr.getIdCluster(), clusterSolr.getNomeConfig());
	}

	public void deleteCollectionSolr() throws Exception {
		final CollectionAdminRequest.Delete deleteCollectionRequest = new CollectionAdminRequest.Delete();
		deleteCollectionRequest.setCollectionName(clusterSolr.getNomeCollection());
		
	    final CollectionAdminResponse response = deleteCollectionRequest.process(getSolrClient(clusterSolr.getIdCluster())); // Executa a processo de criação da coleção
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
		QueryResponse response;
		
		try {
			HttpSolrClient solrClient = getSolrClient(clusterSolr.getIdCluster());
			
			String pesquisa = "".concat(pergunta); // monta String da pesquisa
			SolrQuery query = new SolrQuery(pesquisa); // cria os critérios da pesquisa
			
			response = solrClient.query(clusterSolr.getNomeCollection(), query); // retorna pesquisa
		
		} catch (SolrServerException e) {
			throw new ApplicationException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage(), e);
		}
		
		log.info(response.toString());
		
		return  response;
		
		
	}
	
}
