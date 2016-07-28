package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import lombok.Getter;
import lombok.Setter;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClassificacaoDAO;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classifiers;

@Named
@ViewScoped
public class PerguntaRHManager extends NaturalLanguage implements Serializable {
	private static final long serialVersionUID = 7201661374971816987L;
	
	private static final String RESPOSTA_PADRAO = ApplicationPropertiesUtils.getValue("index.manager.resposta.padrao"); 
	private static final double CONFIDENCE_MINIMO = ApplicationPropertiesUtils.getValueAsDouble("index.manager.confidence.minimo");
	private static final String RESPOSTA_SAUDACOES = ApplicationPropertiesUtils.getValue("index.manager.resposta.saudacoes");
		
	@Inject
	@ApplicationManaged
	protected EntityManager entityManager;
	
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
	@Setter
	private Cliente cliente;
	
	@Getter
	private List<Topico> lstTopico;
	
	@Getter
	@Setter
	private Topico topico;
	
	@Override
	protected void inicializar() {
		ClienteDAO clieteDAO = new ClienteDAO(entityManager);
		cliente = clieteDAO.findByNome("m.watson");

		TopicoDAO topicoDAO = new TopicoDAO(entityManager);
		lstTopico = topicoDAO.findbyCliente(cliente);
		topico = lstTopico.get(0);
		
		resposta = RESPOSTA_SAUDACOES;
	}
	
	@Transactional
	public void btPergunta(){
		limparVariaveis();
		
		if(pergunta != null &&  !"".equals(pergunta)){
			classificacao = getServiceNLC().classify(getIdClassificacao(), pergunta);
			
			if (classificacao.getTopConfidence().compareTo(CONFIDENCE_MINIMO) == -1) {
				gravaPerguntaNaoEncontrada(classificacao, SENTIMENTO_NEGATIVO);
				
				System.out.println (classificacao);
				definicao = RESPOSTA_PADRAO;
				
				definicao += " No momento tenho conhecomento apenas deste tópico = " + topico.getDescricao();
				
				this.likeBox = false;
			} else {
				resposta = classificacao.getTopClass();
				RespostaDAO respostaDAO = new RespostaDAO(entityManager);
				definicao = respostaDAO.findByDescricao(resposta);
				this.likeBox = true;
			}
		}
	}

	private void gravaPerguntaNaoEncontrada(Classification classificacao, Integer sentimento) {
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

	@Transactional
	public void sentimentoImparcial() {
		gravaPerguntaNaoEncontrada(classificacao, SENTIMENTO_IMPARCIAL);
		limparVariaveis();
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("sentimentoMessage", new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Obrigado pela sua opinião", "Obrigado pela sua opinião"));

		pergunta = "";
	}
	
	@Transactional
	public void sentimentoPositivo() {
		gravaPerguntaNaoEncontrada(classificacao, SENTIMENTO_POSITIVO);
		limparVariaveis();
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("sentimentoMessage", new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Obrigado pela sua opinião", "Obrigado pela sua opinião"));
		pergunta = "";
	}

	private void limparVariaveis(){
		resposta = RESPOSTA_SAUDACOES;
		definicao = "";
		classificacao = null;
		likeBox = false;
	}
	
	private String getIdClassificacao() {
		Classifiers classifiers;
		try {
			classifiers = getServiceNLC().getClassifiers();
			List<Classifier> lstClassifiers = classifiers.getClassifiers();
			Classifier classifier = lstClassifiers.get(0);
			
			return classifier.getId();
		} catch(RuntimeException e) {
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
