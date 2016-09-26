package br.com.afirmanet.questions.factory;

import javax.persistence.EntityManager;

import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.service.WatsonService;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.questions.dao.DadosWatsonDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.DadosWatson;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;
import lombok.Getter;
import lombok.Setter;

public abstract class WatsonServiceFactory  {
	
	public static final Integer SENTIMENTO_POSITIVO = ApplicationPropertiesUtils.getValueAsInteger("watson.service.factory.sentimento.positivo");
	public static final Integer SENTIMENTO_IMPARCIAL = ApplicationPropertiesUtils.getValueAsInteger("watson.service.factory.sentimento.imparcial");
	public static final Integer SENTIMENTO_NEGATIVO = ApplicationPropertiesUtils.getValueAsInteger("watson.service.factory.sentimento.negativo");
	public static final Integer SENTIMENTO_ENCONTRADA_NLC = ApplicationPropertiesUtils.getValueAsInteger("watson.service.factory.sentimento.encontrada.nlc");
	public static final Integer SENTIMENTO_ENCONTRADA_DIALOG = ApplicationPropertiesUtils.getValueAsInteger("watson.service.factory.sentimento.encontrada.dialog");
	public static final Integer SENTIMENTO_ENCONTRADA_RR = ApplicationPropertiesUtils.getValueAsInteger("watson.service.factory.sentimento.encontrada.rr");
	
	public static final double CONFIDENCE_MINIMO_NLC = ApplicationPropertiesUtils.getValueAsDouble("index.manager.confidence.minimo.nlc");
	public static final double CONFIDENCE_MINIMO_RR = ApplicationPropertiesUtils.getValueAsDouble("index.manager.confidence.minimo.rr");
	public static final String NAO_SE_APLICA = ApplicationPropertiesUtils.getValue("index.manager.nao.se.aplica");
	
	@Setter
	private EntityManager entityManager;
	
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
	private TextToSpeech serviceTTS;

	private WatsonService service() throws ApplicationException {
		WatsonService watsonService = null;

		try {
			DadosWatsonDAO dadosWatsonDAO = new DadosWatsonDAO(entityManager);
			credenciais = dadosWatsonDAO.findByClienteAndTypeServico(cliente, typeServico);
			
			if(credenciais != null){
				if(TypeServicoEnum.NATURAL_LANGUAGE_CLASSIFIER.equals(typeServico)){
					watsonService = new NaturalLanguageClassifier();
	
				}else if(TypeServicoEnum.DIALOG.equals(typeServico)){
					watsonService = new DialogService();
					
				}else if(TypeServicoEnum.DOCUMENT_CONVERSION.equals(typeServico)){
					watsonService = new DocumentConversion(DocumentConversion.VERSION_DATE_2015_12_01);
					
				}else if(TypeServicoEnum.RETRIEVE_AND_RANK.equals(typeServico)){
					watsonService = new RetrieveAndRank();
				}else if(TypeServicoEnum.TEXT_TO_SPEECH.equals(typeServico)){
					watsonService = new TextToSpeech();
				}
				watsonService.setUsernameAndPassword(credenciais.getUsuario(), credenciais.getSenha());
			}
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(), e); 
		}
		
		return watsonService;
	}

	protected NaturalLanguageClassifier getServiceNLC() throws ApplicationException {
		serviceNLC = (NaturalLanguageClassifier) service();
		return serviceNLC;
	}

	protected DialogService getServiceDialog() throws ApplicationException {
		serviceDialog = (DialogService) service();
		return serviceDialog;
	}

	protected RetrieveAndRank getServiceRR() throws ApplicationException {
		serviceRR = (RetrieveAndRank) service();
		return serviceRR;
	}

	protected DocumentConversion getServiceDC() throws ApplicationException {
		serviceDC = (DocumentConversion) service();
		return serviceDC;
	}

	protected TextToSpeech getServiceTTS() throws ApplicationException {
		serviceTTS = (TextToSpeech) service();
		return serviceTTS;
	}
}
