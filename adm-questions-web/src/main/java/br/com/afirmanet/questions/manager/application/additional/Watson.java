package br.com.afirmanet.questions.manager.application.additional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import lombok.Getter;
import lombok.Setter;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import br.com.afirmanet.core.manager.AbstractManager;
import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClassificacaoDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.manager.vo.SolrResult;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;
import br.com.afirmanet.questions.utils.HttpSolrClientUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;
import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;
import com.ibm.watson.developer_cloud.document_conversion.v1.util.ConversionUtils;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster.Status;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusterOptions;

public abstract class Watson extends AbstractManager implements Serializable {
	private static final long serialVersionUID = 5946605316434150596L;

	protected static final Integer SENTIMENTO_POSITIVO = 1;
	protected static final Integer SENTIMENTO_IMPARCIAL = 0;
	protected static final Integer SENTIMENTO_NEGATIVO = -1;
	protected static final Integer SENTIMENTO_ENCONTRADA_NLC = 100;
	protected static final Integer SENTIMENTO_ENCONTRADA_DIALOG = 200;
	protected static final Integer SENTIMENTO_ENCONTRADA_RR = 300;

	protected static final double CONFIDENCE_MINIMO = ApplicationPropertiesUtils
			.getValueAsDouble("index.manager.confidence.minimo");
	

	private static final String usernameRR = "0ff46d04-61ec-460f-9eff-7d2e3f9c26e2";
	private static final String passwordRR = "CnUt6mMlHass";

	@Inject
	@ApplicationManaged
	protected EntityManager entityManager;

	@Getter
	private NaturalLanguageClassifier serviceNLC;

	@Getter
	private DialogService serviceDialog;

	@Getter
	private RetrieveAndRank serviceRR;

	@Getter
	private DocumentConversion serviceDC;
	
	@Getter
	@Setter
	private Cliente cliente;

	@Getter
	@Setter
	private Topico topico;

	@PostConstruct
	public void init() {
		serviceNLC = new NaturalLanguageClassifier();
		serviceNLC.setUsernameAndPassword("571a35dd-ad5d-42a0-8775-d44d9152a9bd", "A4nFDssYwktc");

		serviceDialog = new DialogService();
		serviceDialog.setUsernameAndPassword("e0572543-d32c-4ef0-af7e-7186245ada9d", "4mJSU0JnG87X");
		
		serviceRR = new RetrieveAndRank();
		serviceRR.setUsernameAndPassword(usernameRR, passwordRR);

		serviceDC = new DocumentConversion(DocumentConversion.VERSION_DATE_2015_12_01);
		serviceDC.setUsernameAndPassword("bf53ed57-6340-4d79-8b48-fa81183e47a3","8krJUjillsLD");
				
		
		getDadosDocumentConversion();
		
		inicializar();
	}

	protected JsonArray getDadosDocumentConversion(){
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho + "/resources/files/document/";
		
		File arquivos[];
		File diretorio = new File(caminho);
		arquivos = diretorio.listFiles();

		JsonObject customConfigDC = getCustomConfigDC();
		JsonArray jsonArray = new JsonArray();
		
		Gson gson = new Gson();
		for(int i = 0; i < arquivos.length; i++){
			String mediaTypeFromFile = ConversionUtils.getMediaTypeFromFile(arquivos[i]);
			Answers execute = serviceDC.convertDocumentToAnswer(arquivos[i]).execute();
			
			SolrResult solrResult = new SolrResult();
			solrResult.setId(execute.getAnswerUnits().get(0).getId());
			solrResult.setTitle(execute.getAnswerUnits().get(0).getTitle());
			solrResult.setBody(execute.getAnswerUnits().get(0).getContent().get(0).getText());
			
			
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(gson.toJson(solrResult));

			jsonArray.add(jsonElement);
		}
				
		return jsonArray;
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

	protected void uploadConfiguration(String idCluster, String nomeConfig) {
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho.concat("/resources/files/zip/solrconfig.zip");
		
		File configZip = new File(caminho);
		serviceRR.uploadSolrClusterConfigurationZip(idCluster, nomeConfig, configZip).execute();
	}

	private SolrCluster getSolrCluster(String idCluster) {
		return serviceRR.getSolrCluster(idCluster).execute();
	}

	protected String createCluster(String nomeCluster, Integer unit) {
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
		SolrCluster cluster = serviceRR.createSolrCluster(optionCluster).execute();
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

	protected void createCollection(String idCluster, String nomeConfig, String nomeColection) throws Exception {
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
	
	protected HttpSolrClient getSolrClient(String idCluster) {
		HttpSolrClient.Builder builderHttpSolrClient = new HttpSolrClient.Builder(serviceRR.getSolrUrl(idCluster));
		builderHttpSolrClient.withHttpClient(HttpSolrClientUtils.createHttpClient(serviceRR.getSolrUrl(idCluster), usernameRR, passwordRR));
		return builderHttpSolrClient.build();
	}

	protected void indexDocumentAndCommit(String idCluster, String nomeCollection) throws Exception{
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
		
		JsonArray jsonArray = getDadosDocumentConversion();
    	
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

	protected QueryResponse searchAllDocs(String idCluster,String collection,String pergunta) throws Exception {
		HttpSolrClient solrClient = getSolrClient(idCluster);
		
		String pesquisa = "*:".concat(pergunta); // monta String da pesquisa
		SolrQuery query = new SolrQuery(pesquisa); // cria os critérios da pesquisa
		return  solrClient.query(collection, query); // retorna pesquisa
	}

	protected void cleanupResources() {

	}

	protected void gravaPerguntaEncontrada(Classification classificacao, Integer sentimento) {
		ClassificacaoDAO classificacaoDAO = new ClassificacaoDAO(entityManager);
		Classificacao classificacaoEntity = new Classificacao();

		classificacaoEntity.setDataCadastro(LocalDateTime.now());
		classificacaoEntity.setConfidence(classificacao.getClasses().get(0).getConfidence());
		classificacaoEntity.setPergunta(classificacao.getText());
		classificacaoEntity.setResposta(classificacao.getTopClass());
		classificacaoEntity.setSentimento(sentimento);
		classificacaoEntity.setCliente(cliente);
		classificacaoEntity.setTopico(topico);
		classificacaoEntity.setClassifier(classificacao.getId());

		classificacaoDAO.save(classificacaoEntity);
	}

	protected abstract void inicializar();
}
