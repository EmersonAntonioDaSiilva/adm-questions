package br.com.afirmanet.questions.manager.application.additional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.lang.reflect.Type;
import com.google.common.reflect.TypeToken;
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

import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClassificacaoDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.manager.vo.SolrResult;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;
import lombok.Getter;
import lombok.Setter;

public abstract class Watson implements Serializable {
	private static final long serialVersionUID = 5946605316434150596L;

	protected static final Integer SENTIMENTO_POSITIVO = 1;
	protected static final Integer SENTIMENTO_IMPARCIAL = 0;
	protected static final Integer SENTIMENTO_NEGATIVO = -1;
	protected static final Integer SENTIMENTO_ENCONTRADA_NLC = 100;
	protected static final Integer SENTIMENTO_ENCONTRADA_DIALOG = 200;
	protected static final Integer SENTIMENTO_ENCONTRADA_RR = 300;

	protected static final double CONFIDENCE_MINIMO = ApplicationPropertiesUtils
			.getValueAsDouble("index.manager.confidence.minimo");
	protected static final String CAMINHO_ZIP_ARQUIVO_FERIAS = ApplicationPropertiesUtils
			.getValue("index.manager.caminho.arquivo.ferias");

	private static final String usernameRR = "80b1d296-9eda-4326-93cc-a36122dfa187";
	private static final String passwordRR = "XCOtytypqONK";

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

	protected void uploadConfiguration() {
		SolrCluster solrCluster = getSolrCluster();

		File configZip = new File(CAMINHO_ZIP_ARQUIVO_FERIAS);
		serviceRR.uploadSolrClusterConfigurationZip(solrCluster.getId(), "FERIAS", configZip);

	}

	private SolrCluster getSolrCluster() {

		return null;
	}

	protected void createCollection() {

	}

	protected void indexDocumentAndCommit() {

	}

	protected void searchAllDocs(String pergunta) {
//		HttpSolrClient solrClient = new HttpSolrClient(serviceRR.getSolrUrl(getSolrCluster().getId()),
//				HttpSolrClientUtils.createHttpClient(serviceRR.getEndPoint(), usernameRR, passwordRR));
//
//		solrUtils = new SolrUtils(solrClient, groundTruth, collectionName, rankerId);
//		
//		SolrQuery query = new SolrQuery(pergunta);
//		QueryResponse response = solrClient.query("example_collection", query);
//		Ranking ranking = serviceRR.rank("B2E325-rank-67", response);
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
