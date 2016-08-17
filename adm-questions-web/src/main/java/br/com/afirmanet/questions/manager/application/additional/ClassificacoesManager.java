package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.faces.primefaces.model.LazyEntityModel;
import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClassificacaoDAO;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Resposta;
import br.com.afirmanet.questions.entity.Topico;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ClassificacoesManager implements Serializable {
	private static final long serialVersionUID = 3925507169074921562L;
	
	@Inject
	@ApplicationManaged
	protected EntityManager entityManager;
	
	@Getter
	private Cliente cliente;

	@Getter
	private List<Topico> lstTopico;
	
	@Getter
	@Setter
	private Topico topico;
		
	@Getter
	@Setter
	private List<Classificacao> selecionadosClassificacao;
	
	@Getter
	@Setter
	private Resposta resposta;	
	
	@Getter
	private LazyEntityModel<Classificacao, Integer> lstClassificacao;
	
	@Getter
	private LazyEntityModel<Resposta, Integer> lstResposta;	
	
	@PostConstruct
	public void init() {		
		ClienteDAO clieteDAO = new ClienteDAO(entityManager);
		cliente = clieteDAO.findByNome("m.watson");
		
		TopicoDAO topicoDAO = new TopicoDAO(entityManager);
		lstTopico = topicoDAO.findbyCliente(cliente);
		
		
		topico = lstTopico.get(0);
		carregaDadosClassificacao();
	}
	
	public void carregaDadosClassificacao() throws ApplicationException{
		try {
			ClassificacaoDAO classificacaoDAO = new ClassificacaoDAO(entityManager);		
			Classificacao classificacao = new Classificacao();
			classificacao.setCliente(cliente);
			classificacao.setTopico(topico);		
			lstClassificacao = new LazyEntityModel<>(classificacaoDAO, classificacao);

			RespostaDAO respostaDAO = new RespostaDAO(entityManager);
			Resposta resposta = new Resposta();
			resposta.setCliente(cliente);
			resposta.setTopico(topico);		
			lstResposta = new LazyEntityModel<>(respostaDAO, resposta);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			throw new ApplicationException(e.getMessage(),e);
		}
	
	}
}
