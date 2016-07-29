package br.com.afirmanet.questions.manager.application.additional;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster;

import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClassificacaoDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
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
	
	protected static final double CONFIDENCE_MINIMO = ApplicationPropertiesUtils.getValueAsDouble("index.manager.confidence.minimo");
	protected static final String CAMINHO_ZIP_ARQUIVO_FERIAS  = ApplicationPropertiesUtils.getValue("index.manager.caminho.arquivo.ferias");
	
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
		serviceRR.setUsernameAndPassword("80b1d296-9eda-4326-93cc-a36122dfa187","XCOtytypqONK");
		
		inicializar();
	}

	protected void uploadConfiguration(){
		List<SolrCluster> solrClusters = serviceRR.getSolrClusters().getSolrClusters();
		SolrCluster solrCluster = solrClusters.get(0);

		File configZip = new File(CAMINHO_ZIP_ARQUIVO_FERIAS);
		serviceRR.uploadSolrClusterConfigurationZip(solrCluster.getId(), "FERIAS", configZip);
		
	}
	
	protected void createCollection(){
		
	}
	
	protected void indexDocumentAndCommit(){
		
	}
	
	protected void searchAllDocs(){
		
	}
	
	protected void cleanupResources(){
		
	}
	
	protected void gravaPerguntaEncontrada(Classification classificacao, Integer sentimento) {
		ClassificacaoDAO classificacaoDAO = new ClassificacaoDAO(entityManager);
		Classificacao classificacaoEntity = new Classificacao();
		
		classificacaoEntity.setDataCadastro(LocalDateTime.now());
		classificacaoEntity.setConfidence(classificacao.getTopConfidence());
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
