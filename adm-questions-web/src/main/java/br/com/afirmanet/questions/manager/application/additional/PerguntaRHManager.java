package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.omnifaces.cdi.ViewScoped;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classifiers;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.manager.AbstractManager;
import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.factory.WatsonServiceFactory;
import br.com.afirmanet.questions.service.ServiceNLC;
import br.com.afirmanet.questions.service.ServiceRetrieveAndRank;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class PerguntaRHManager extends AbstractManager implements Serializable {
	private static final long serialVersionUID = 7201661374971816987L;

	private static final String RESPOSTA_PADRAO = ApplicationPropertiesUtils.getValue("index.manager.resposta.padrao");
	private static final String RESPOSTA_SAUDACOES = ApplicationPropertiesUtils
			.getValue("index.manager.resposta.saudacoes");

	@Inject
	@ApplicationManaged
	private EntityManager entityManager;

	@Getter
	@Setter
	private String pergunta;

	@Getter
	@Setter
	private String resposta;

	@Getter
	@Setter
	private String definicao;

	@Getter
	@Setter
	private boolean likeBox;

	@Getter
	@Setter
	private Classification classificacao;

	@Getter
	private List<Topico> lstTopico;

	private ServiceRetrieveAndRank serviceRetrieveAndRank;
	private ServiceNLC service;
	private Cliente cliente;
	private Topico topico;

	@PostConstruct
	protected void inicializar() {
		try {
			ClienteDAO clieteDAO = new ClienteDAO(entityManager);
			cliente = clieteDAO.findByNome("m.watson");

			service = new ServiceNLC(cliente, entityManager);
			serviceRetrieveAndRank = new ServiceRetrieveAndRank(cliente, entityManager);

			TopicoDAO topicoDAO = new TopicoDAO(entityManager);
			lstTopico = topicoDAO.findbyCliente(cliente);
			topico = lstTopico.get(0);

			resposta = RESPOSTA_SAUDACOES;
		} catch (ApplicationException e) {
			addErrorMessage(e.getMessage(), e);
		}
	}

	@Transactional
	public void btPergunta(){
		limparVariaveis();
		
		if(pergunta != null &&  !"".equals(pergunta)){
			classificacao = service.getService().classify(getIdClassificacao(), pergunta);
			
			if (classificacao.getClasses().get(0).getConfidence().compareTo(WatsonServiceFactory.CONFIDENCE_MINIMO_NLC) == -1) {
				service.gravaPerguntaEncontrada(topico, classificacao, WatsonServiceFactory.SENTIMENTO_NEGATIVO);
				this.likeBox = false;
				
				if(!WatsonServiceFactory.NAO_SE_APLICA.equals(classificacao.getClasses().get(0).getName()) && classificacao.getClasses().get(0).getConfidence().compareTo(WatsonServiceFactory.CONFIDENCE_MINIMO_RR) == 1){
					if(!searchRetrieve(pergunta)){
						definicao = RESPOSTA_PADRAO;
						definicao += " No momento tenho conhecimento apenas deste tópico = " + topico.getDescricao();
					}
				}else{
					definicao = RESPOSTA_PADRAO;
					definicao += " No momento tenho conhecimento apenas deste tópico = " + topico.getDescricao();
				}
			} else {
				service.gravaPerguntaEncontrada(topico, classificacao, WatsonServiceFactory.SENTIMENTO_ENCONTRADA_NLC);
				
				resposta = classificacao.getTopClass();
				RespostaDAO respostaDAO = new RespostaDAO(entityManager);
				definicao = respostaDAO.findByDescricao(resposta);
				this.likeBox = true;
			}
		}
	}

	private boolean searchRetrieve(String pergunta) {
		Boolean retorno = Boolean.FALSE;
		try {
			QueryResponse queryResponse = serviceRetrieveAndRank.searchAllDocs(pergunta);

			SolrDocumentList results = queryResponse.getResults();
			SolrDocument solrDocument = results.get(0);

			Object fieldValue = solrDocument.getFieldValue("body");
			definicao = fieldValue.toString();
			definicao = definicao.substring(1, definicao.length() - 1);

			retorno = Boolean.TRUE;
		} catch (ApplicationException e) {
			addErrorMessage(e.getMessage(), e);
		} catch (Exception e) {
			addErrorMessage(e.getMessage(), e);
		}

		return retorno;
	}

	@Transactional
	public void sentimentoImparcial() {
		service.gravaPerguntaEncontrada(topico, classificacao, WatsonServiceFactory.SENTIMENTO_IMPARCIAL);
		limparVariaveis();
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("sentimentoMessage",
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Obrigado pela sua opinião", "Obrigado pela sua opinião"));

		pergunta = "";
	}

	@Transactional
	public void sentimentoPositivo() {
		service.gravaPerguntaEncontrada(topico, classificacao, WatsonServiceFactory.SENTIMENTO_POSITIVO);
		limparVariaveis();
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("sentimentoMessage",
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Obrigado pela sua opinião", "Obrigado pela sua opinião"));
		pergunta = "";
	}

	private void limparVariaveis() {
		resposta = RESPOSTA_SAUDACOES;
		definicao = "";
		classificacao = null;
		likeBox = false;
	}

	private String getIdClassificacao() {
		Classifiers classifiers;
		try {
			classifiers = service.getService().getClassifiers();
			List<Classifier> lstClassifiers = classifiers.getClassifiers();
			Classifier classifier = lstClassifiers.get(0);

			return classifier.getId();
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return "";

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return "";
		}
	}
}
