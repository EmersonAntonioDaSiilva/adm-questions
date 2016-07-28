package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;

import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClassificacaoDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;
import lombok.Getter;
import lombok.Setter;

public abstract class NaturalLanguage implements Serializable {
	private static final long serialVersionUID = 5946605316434150596L;

	protected static final Integer SENTIMENTO_POSITIVO = 1;
	protected static final Integer SENTIMENTO_IMPARCIAL = 0;
	protected static final Integer SENTIMENTO_NEGATIVO = -1;
	protected static final Integer SENTIMENTO_ENCONTRADA_NLC = 100;
	protected static final Integer SENTIMENTO_ENCONTRADA_DIALOG = 200;
	
	protected static final double CONFIDENCE_MINIMO = ApplicationPropertiesUtils.getValueAsDouble("index.manager.confidence.minimo");
	
	@Inject
	@ApplicationManaged
	protected EntityManager entityManager;	
	
	@Getter
	private NaturalLanguageClassifier serviceNLC;

	@Getter
	private DialogService serviceDialog;
	
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
		
		inicializar();
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
