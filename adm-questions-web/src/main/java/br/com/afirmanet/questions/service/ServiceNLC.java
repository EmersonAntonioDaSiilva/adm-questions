package br.com.afirmanet.questions.service;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.questions.dao.ClassificacaoDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import br.com.afirmanet.questions.factory.WatsonServiceFactory;
import lombok.Getter;

public class ServiceNLC extends WatsonServiceFactory implements Serializable {
	private static final long serialVersionUID = -452444688310099799L;

	@Getter
	private NaturalLanguageClassifier service;

	public ServiceNLC(Cliente cliente, EntityManager entityManager) throws ApplicationException {
		super(entityManager);
		setTypeServico(TypeServicoEnum.NATURAL_LANGUAGE_CLASSIFIER);
		setCliente(cliente);
		
		service = getServiceNLC();
		
	}

	public void gravaPerguntaEncontrada(Topico topico, Classification classificacao, Integer sentimento) {
		ClassificacaoDAO classificacaoDAO = new ClassificacaoDAO(entityManager);
		Classificacao classificacaoEntity = new Classificacao();

		classificacaoEntity.setDataCadastro(LocalDateTime.now());
		classificacaoEntity.setConfidence(classificacao.getClasses().get(0).getConfidence());
		classificacaoEntity.setPergunta(classificacao.getText());
		classificacaoEntity.setResposta(classificacao.getTopClass());
		classificacaoEntity.setSentimento(sentimento);
		classificacaoEntity.setCliente(getCliente());
		classificacaoEntity.setTopico(topico);
		classificacaoEntity.setClassifier(classificacao.getId());

		classificacaoDAO.save(classificacaoEntity);
	}
}
