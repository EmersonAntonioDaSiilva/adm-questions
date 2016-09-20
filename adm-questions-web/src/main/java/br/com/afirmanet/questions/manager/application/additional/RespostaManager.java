package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.enumeration.CrudActionEnum;
import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.interceptor.Message;
import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.PerguntaDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Pergunta;
import br.com.afirmanet.questions.entity.Resposta;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class RespostaManager extends GenericCRUD<Resposta, Integer, RespostaDAO> implements Serializable {
	private static final long serialVersionUID = 3925507169074921562L;

	@Getter
	private List<Cliente> lstCliente;

	@Getter
	private List<Topico> lstTopico;

	@Getter
	@Setter
	private String descricaoPergunta;

	@Getter
	@Setter
	private List<Pergunta> perguntas;

	@Override
	public void init() {
		showDeleteButton = true;
		descricaoPergunta = "";

		ClienteDAO clienteDAO = new ClienteDAO(entityManager);
		lstCliente = clienteDAO.findAll();
	}

	@Override
	public void prepareInsert() {
		super.prepareInsert();
		
		if(searchParam.getCliente() != null){
			entity.setCliente(searchParam.getCliente());
		}
		
		if(searchParam.getTopico() != null){
			entity.setTopico(searchParam.getTopico());
		}
	}
	
	@Override
	protected void beforeSave() {
		descricaoPergunta = "";
		perguntas = null;

		validarDados();
	}
	
	@Override
	protected void beforeUpdate() {
		descricaoPergunta = "";
		perguntas = null;
		validarDados();
	}

	@Override
	protected void afterUpdate() {
		beforeDetail();
	}

	public void carregaDescricaoClasse() throws DaoException {
		try {
			TopicoDAO classeDAO = new TopicoDAO(entityManager);

			if (currentAction.equals(CrudActionEnum.SEARCH)) {
				lstTopico = classeDAO.findbyCliente(searchParam.getCliente());
			} else {
				lstTopico = classeDAO.findbyCliente(entity.getCliente());
			}
		} catch (Exception e) {
			addErrorMessage(e.getMessage(), e);
		}
	}

	private void validarDados() throws ApplicationException {
		try {
			RespostaDAO respostaDAO = new RespostaDAO(entityManager);
			Resposta byDescricao = respostaDAO.findByNome(entity.getTitulo());

			if (byDescricao != null && !entity.equals(byDescricao)) {
				throw new ApplicationException("Já existe um registro em Resposta com esta Descrição: "
						+ entity.getTitulo());
			}
		} catch (DaoException e) {
			throw new ApplicationException(e.getMessage(), e);
		}
	}

	@Override
	protected void beforeDetail() {
		PerguntaDAO perguntaDAO = new PerguntaDAO(entityManager);
		perguntas = perguntaDAO.findAllByResposta(entity);
	}

	@Transactional
	public void insertPergunta() throws ApplicationException {
		if (descricaoPergunta==null)
			addErrorMessage("Favor inserir uma descrição");
		
		else if (validarDadosPergunta()) {
			PerguntaDAO perguntaDAO = new PerguntaDAO(entityManager);
			Pergunta byPergunta = new Pergunta();
			byPergunta.setResposta(entity);
			byPergunta.setDescricao(descricaoPergunta);

			perguntaDAO.save(byPergunta);

			descricaoPergunta = "";
			beforeDetail();
		} else {
			throw new ApplicationException("Já existe um registro em Pergunta com esta Descrição: " + descricaoPergunta);
		}
	}

	@Message
	@Transactional
	public void deletePergunta(Pergunta dellPergunta) {
		try {
			PerguntaDAO perguntaDAO = new PerguntaDAO(entityManager);
			perguntaDAO.deleteById(perguntaDAO.getId(dellPergunta));

			beforeDetail();
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(), e);
		}
	}

	private boolean validarDadosPergunta() throws ApplicationException {
		boolean retorno = true;

		try {

			PerguntaDAO perguntaDAO = new PerguntaDAO(entityManager);
			Pergunta byPergunta = new Pergunta();
			byPergunta.setResposta(entity);
			byPergunta.setDescricao(descricaoPergunta);
			byPergunta = perguntaDAO.findPerguntaByDescricaoAndResposta(byPergunta);

			if (byPergunta != null) {
				retorno = false;
			}

			return retorno;

		} catch (DaoException e) {
			throw new ApplicationException(e.getMessage());
		}

	}
}
