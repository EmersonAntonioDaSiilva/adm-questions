package br.com.afirmanet.questions.manager.application.additional;

<<<<<<< HEAD
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import lombok.Getter;
import lombok.Setter;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.manager.AbstractManager;
import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.core.util.DateUtils;
import br.com.afirmanet.core.util.TimeUtils;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.dao.UsuarioPerfilDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.entity.UsuarioPerfil;
import br.com.afirmanet.questions.factory.WatsonServiceFactory;
import br.com.afirmanet.questions.manager.vo.DialogVO;
import br.com.afirmanet.questions.service.ServiceDialog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import com.ibm.watson.developer_cloud.dialog.v1.model.Dialog;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
=======
import src.main.java.br.com.afirmanet.questions.factory.WatsonServiceFactory;
import src.main.java.br.com.afirmanet.questions.manager.vo.DialogVO;
import src.main.java.br.com.afirmanet.questions.service.ServiceDialog;
>>>>>>> origin/desenv

@Named
@ViewScoped
public class DialogRHManager extends AbstractManager implements Serializable {
	private static final long serialVersionUID = 7201661374971816987L;

	private static final String INFORMAR_NOME = "informar o seu nome:";
	private static final String INFORMAR_DATA_NASCIMENTO = "informar a sua data de nascimento:";
	private static final String INFORMAR_DATA_ADMISSAO = "informar a sua data de admissão:";

	private static final int PESQUISAR_DB = 1;

	@Inject
	@ApplicationManaged
	private EntityManager entityManager;

	@Getter
	@Setter
	private String pergunta;

	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private Conversation conversation;

	@Getter
	@Setter
	private List<Topico> lstTopico;

	@Getter
	@Setter
	private UsuarioPerfil usuarioPerfil;

	@Getter
	@Setter
	private List<DialogVO> lstDialog;

	@Getter
	@Setter
	private Boolean actionUsuarioPerfil;

	@Getter
	@Setter
	private Boolean actionDialog;

	private ServiceDialog serviceDialog;
	private Cliente cliente;
	private Topico topico;

	@PostConstruct
	protected void inicializar() {

		ClienteDAO clieteDAO = new ClienteDAO(entityManager);
		cliente = clieteDAO.findByNome("m.watson");

		serviceDialog = new ServiceDialog(cliente, entityManager);

		TopicoDAO topicoDAO = new TopicoDAO(entityManager);
		lstTopico = topicoDAO.findbyCliente(cliente);
		topico = lstTopico.get(0);

		lstDialog = new ArrayList<>();

		actionUsuarioPerfil = Boolean.TRUE;
		actionDialog = Boolean.FALSE;

	}

	@Transactional
	public void btPergunta() throws ApplicationException {
		try{
			limparVariaveis();
	
			if (pergunta != null && !"".equals(pergunta)) {
	
				conversation = getDialog(conversation);
				conversation = serviceDialog.getService().converse(conversation, getDialogoUsuario()).execute();
	
				DialogVO dialogVO = new DialogVO();
				dialogVO.setPessoa(TimeUtils.timeNow() + " - M.Watson: ");
				dialogVO.setDialogo(dialogVO.getPessoa() + tratarRespostas(conversation));
	
				lstDialog.add(dialogVO);
				pergunta = "";
			}
			
		}catch(ApplicationException e){
			addErrorMessage(e.getMessage());
		}
	}

	private String tratarRespostas(Conversation converse) {
		String resposta = converse.getResponse().get(0);

		try {
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(resposta);

			JsonElement jsonElement = json.get("pesquisaBD");
			int pesquisaBD = jsonElement.getAsInt();

			if (PESQUISAR_DB == pesquisaBD) {
				jsonElement = json.get("classificacao");
				String topClass = jsonElement.getAsString();
				RespostaDAO respostaDAO = new RespostaDAO(entityManager);
				resposta = respostaDAO.findByDescricao(topClass);

				Classification classificacao = new Classification();
				classificacao.setText(converse.getInput());
				classificacao.setTopClass(topClass);
				classificacao.setId(converse.getDialogId());

				
				serviceDialog.gravaPerguntaEncontrada(topico, classificacao, WatsonServiceFactory.SENTIMENTO_ENCONTRADA_DIALOG);
				
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return resposta;
	}

	@Transactional
	public void btEmail() throws ApplicationException {
		try {
			if (email != null && !"".equals(email)) {

				actionUsuarioPerfil = Boolean.FALSE;
				actionDialog = Boolean.TRUE;

				UsuarioPerfilDAO usuarioPerfilDAO = new UsuarioPerfilDAO(entityManager);
				usuarioPerfil = usuarioPerfilDAO.findByEmail(email);

				if (usuarioPerfil == null) {
					usuarioPerfil = new UsuarioPerfil();
				} else {
					HashMap<String, String> profileValues = new HashMap<>();
					profileValues.put("NOME", usuarioPerfil.getNome());
					profileValues.put("DATA_ADM", DateUtils.format(usuarioPerfil.getDataAdmissao()));
					profileValues.put("DATA_NASC", DateUtils.format(usuarioPerfil.getDataNascimento()));
					serviceDialog.getService().updateProfile(usuarioPerfil.getDialogId(), usuarioPerfil.getClientId(),
							profileValues);
				}

				conversation = getDialog(conversation);
				conversation = serviceDialog.getService().converse(conversation, "Oi").execute();

				DialogVO dialogVO = new DialogVO();
				dialogVO.setPessoa(TimeUtils.timeNow() + " - M.Watson: ");
				dialogVO.setDialogo(dialogVO.getPessoa() + conversation.getResponse().get(0));
				lstDialog.add(dialogVO);
			}
		} catch (ApplicationException e) {
			addErrorMessage(e.getMessage());
		}
	}

	private Conversation getDialog(Conversation conversation) throws ApplicationException {

		if (conversation == null) {
			conversation = new Conversation();
		}

		if (usuarioPerfil != null) {
			if (usuarioPerfil.getClientId() != null) {
				conversation.setClientId(usuarioPerfil.getClientId());
			}

			if (usuarioPerfil.getConversationId() != null) {
				conversation.setId(usuarioPerfil.getConversationId());
			}
		}

		updateUsuarioPerfil(conversation);

		conversation.setDialogId(getIdDialog());
		conversation.setConfidence(WatsonServiceFactory.CONFIDENCE_MINIMO_NLC);

		return conversation;
	}

	private void updateUsuarioPerfil(Conversation conversation) throws ApplicationException {
		Boolean isUpdate = Boolean.FALSE;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate dateAtual = LocalDate.now();
		LocalDate date;

		// Variáveis auxiliares dataNascimento
		String minDateNascS = ("01/01/1930");
		String maxDateNascS = ("01/01/2001");
		LocalDate minDateNasc = LocalDate.parse(minDateNascS, formatter);
		LocalDate maxDateNasc = LocalDate.parse(maxDateNascS, formatter);

		// Variáveis auxiliares dataAdmissão
		String dateAdmissaoS = ("01/01/1996");
		LocalDate dateAdmissao = LocalDate.parse(dateAdmissaoS, formatter);

		if (conversation.getResponse() != null) {
			String resposta = conversation.getResponse().get(0);

			if (resposta.indexOf(INFORMAR_NOME) >= 0) {
				usuarioPerfil.setNome(pergunta);
				isUpdate = Boolean.TRUE;
			}

			if (resposta.indexOf(INFORMAR_DATA_NASCIMENTO) >= 0) {

				try {
					date = LocalDate.parse(pergunta, formatter);
				} catch (Exception e) {
					isUpdate = Boolean.FALSE;
					throw new ApplicationException("Data de nascimento inválida. Favor digitar novamente no formato: dd/mm/aaaa", e);
				}

				if ((date.isBefore(minDateNasc)) || date.isAfter(maxDateNasc)) {
					throw new ApplicationException("Data de nascimento incorreta. Digite um valor válido");
				}

				usuarioPerfil.setDataNascimento(date);
				isUpdate = Boolean.TRUE;
			}

			if (resposta.indexOf(INFORMAR_DATA_ADMISSAO) >= 0) {

				try {
					date = LocalDate.parse(pergunta, formatter);

				} catch (Exception e) {
					isUpdate = Boolean.FALSE;
					throw new ApplicationException("Data de admissão inválida. Favor digitar novamente no formato dd/mm/aaaa", e);
				}

				if (date.isAfter(dateAtual) || date.isBefore(dateAdmissao)) {
					throw new ApplicationException("Data de admissão inválida. Digite um valor válido");
				}

				usuarioPerfil.setDataAdmissao(date);
				isUpdate = Boolean.TRUE;
			}

		}

		if (email != null && !"".equals(email) && usuarioPerfil.getId() == null) {
			isUpdate = Boolean.TRUE;
		}

		if (isUpdate) {
			usuarioPerfil.setClientId(conversation.getClientId());
			usuarioPerfil.setConversationId(conversation.getId());
			usuarioPerfil.setDialogId(conversation.getDialogId());
			usuarioPerfil.setEmail(email);

			UsuarioPerfilDAO usuarioPerfilDAO = new UsuarioPerfilDAO(entityManager);

			if (usuarioPerfil.getId() == null) {
				usuarioPerfilDAO.save(usuarioPerfil);
			} else {
				usuarioPerfilDAO.update(usuarioPerfil);
			}

		}
	}

	private String getDialogoUsuario() {
		DialogVO dialogVO = new DialogVO();

		String pessoa = TimeUtils.timeNow() + " - ";
		pessoa += usuarioPerfil.getNome() != null ? usuarioPerfil.getNome() : "EU";
		pessoa += ": ";

		dialogVO.setPessoa(pessoa);
		dialogVO.setDialogo(dialogVO.getPessoa() + pergunta);
		lstDialog.add(dialogVO);
		return pergunta;
	}

	private void limparVariaveis() {

	}

	private String getIdDialog() {
		// Pega todos os Dialog configurados
		List<Dialog> dialogs = serviceDialog.getService().getDialogs().execute();

		// Retorna o id do Dialog encontrado
		return dialogs.get(0).getId();
	}
}
