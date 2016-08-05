package br.com.afirmanet.questions.factory;

import javax.persistence.EntityManager;

import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.service.WatsonService;

import br.com.afirmanet.questions.dao.DadosWatsonDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.DadosWatson;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;
import lombok.Getter;
import lombok.Setter;

public abstract class WatsonServiceFactory  {
	public static final Integer SENTIMENTO_POSITIVO = 1;
	public static final Integer SENTIMENTO_IMPARCIAL = 0;
	public static final Integer SENTIMENTO_NEGATIVO = -1;
	public static final Integer SENTIMENTO_ENCONTRADA_NLC = 100;
	public static final Integer SENTIMENTO_ENCONTRADA_DIALOG = 200;
	public static final Integer SENTIMENTO_ENCONTRADA_RR = 300;

	public static final double CONFIDENCE_MINIMO = ApplicationPropertiesUtils
			.getValueAsDouble("index.manager.confidence.minimo");

	protected EntityManager entityManager;
	
	@Setter
	private TypeServicoEnum typeServico;
	
	@Setter
	@Getter
	private Cliente cliente;

	protected DadosWatson credenciais;
	
	private NaturalLanguageClassifier serviceNLC;
	private DialogService serviceDialog;
	private RetrieveAndRank serviceRR;
	private DocumentConversion serviceDC;
	
	public WatsonServiceFactory(EntityManager entityManager){
		this.entityManager = entityManager;
	}
	
	
	private WatsonService service(){
		WatsonService watsonService = null;
		DadosWatsonDAO dadosWatsonDAO = new DadosWatsonDAO(entityManager);
		credenciais = dadosWatsonDAO.findByClienteAndTypeServico(cliente, typeServico);
		
		if(TypeServicoEnum.NATURAL_LANGUAGE_CLASSIFIER.equals(typeServico)){
			watsonService = new NaturalLanguageClassifier(credenciais.getUsuario(), credenciais.getSenha());
			
		}else if(TypeServicoEnum.DIALOG.equals(typeServico)){
			watsonService = new DialogService(credenciais.getUsuario(), credenciais.getSenha());
			
		}else if(TypeServicoEnum.DOCUMENT_CONVERSION.equals(typeServico)){
			watsonService = new RetrieveAndRank(credenciais.getUsuario(), credenciais.getSenha());
			
		}else if(TypeServicoEnum.RETRIEVE_AND_RANK.equals(typeServico)){
			watsonService = new DocumentConversion(DocumentConversion.VERSION_DATE_2015_12_01);
			watsonService.setUsernameAndPassword(credenciais.getUsuario(), credenciais.getSenha());
		}
		
		return watsonService;
	}

	protected NaturalLanguageClassifier getServiceNLC() {
		serviceNLC = (NaturalLanguageClassifier) service();
		return serviceNLC;
	}

	protected DialogService getServiceDialog() {
		serviceDialog = (DialogService) service();
		return serviceDialog;
	}

	protected RetrieveAndRank getServiceRR() {
		serviceRR = (RetrieveAndRank) service();
		return serviceRR;
	}

	protected DocumentConversion getServiceDC() {
		serviceDC = (DocumentConversion) service();
		return serviceDC;
	}
}