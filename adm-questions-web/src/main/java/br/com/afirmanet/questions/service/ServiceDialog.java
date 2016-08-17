package br.com.afirmanet.questions.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import com.ibm.watson.developer_cloud.dialog.v1.model.Dialog;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.exception.DaoException;
import br.com.afirmanet.core.util.ApplicationPropertiesUtils;
import br.com.afirmanet.core.util.DateUtils;
import br.com.afirmanet.core.util.TimeUtils;
import br.com.afirmanet.questions.dao.ClassificacaoDAO;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.dao.UsuarioPerfilDAO;
import br.com.afirmanet.questions.entity.Classificacao;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.entity.UsuarioPerfil;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import br.com.afirmanet.questions.factory.WatsonServiceFactory;
import br.com.afirmanet.questions.manager.vo.DialogVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/serviceDialog")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ServiceDialog extends WatsonServiceFactory implements Serializable {
	private static final long serialVersionUID = -452444688310099799L;

	private static final String INFORMAR_NOME = ApplicationPropertiesUtils.getValue("dialogRH.manager.informar.nome");
	private static final String INFORMAR_DATA_NASCIMENTO = ApplicationPropertiesUtils
			.getValue("dialogRH.manager.informar.data.nascimento");
	private static final String INFORMAR_DATA_ADMISSAO = ApplicationPropertiesUtils
			.getValue("dialogRH.manager.informar.data.admissao");
	private static final int PESQUISAR_DB = ApplicationPropertiesUtils
			.getValueAsInteger("dialogRH.manager.pesquisar.db");

	private EntityManager entityManager;

	@PersistenceUnit(unitName = "question-ds")
	private EntityManagerFactory entityManagerFactory;

	private DialogService service;

	private ServiceRetrieveAndRank serviceRetrieveAndRank;
	private Conversation conversation;
	private UsuarioPerfil usuarioPerfil;

	private Topico topico;
	private Cliente cliente;

	private void initDados() throws ApplicationException {
		entityManager = entityManagerFactory.createEntityManager();
		setEntityManager(entityManager);

		ClienteDAO clieteDAO = new ClienteDAO(entityManager);
		cliente = clieteDAO.findByNome("m.watson");

		TopicoDAO topicoDAO = new TopicoDAO(entityManager);
		topico = topicoDAO.findbyCliente(cliente).get(0);

		setTypeServico(TypeServicoEnum.DIALOG);
		setCliente(cliente);

		service = getServiceDialog();
		serviceRetrieveAndRank = new ServiceRetrieveAndRank(cliente, entityManager);

	}

	private void gravaPerguntaEncontrada(Topico topico, Classification classificacao, Integer sentimento)
			throws DaoException {
		try {
			ClassificacaoDAO classificacaoDAO = new ClassificacaoDAO(entityManager);
			Classificacao classificacaoEntity = new Classificacao();

			classificacaoEntity.setDataCadastro(LocalDateTime.now());
			classificacaoEntity.setConfidence(classificacao.getTopConfidence());
			classificacaoEntity.setPergunta(classificacao.getText());
			classificacaoEntity.setResposta(classificacao.getTopClass());
			classificacaoEntity.setSentimento(sentimento);
			classificacaoEntity.setCliente(getCliente());
			classificacaoEntity.setTopico(topico);
			classificacaoEntity.setClassifier(classificacao.getId());

			classificacaoDAO.save(classificacaoEntity);
		} catch (Exception e) {
			throw new DaoException(
					"Não foi possível gravar a classificação, " + classificacao.getId() + " do Serviço Dialog", e);
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

				jsonElement = json.get("confidente");
				Double confidente = jsonElement.getAsDouble();

				Classification classificacao = new Classification();
				classificacao.setText(converse.getInput());
				classificacao.setTopClass(topClass);
				classificacao.setTopConfidence(confidente);
				classificacao.setId(converse.getDialogId());

				if (WatsonServiceFactory.CONFIDENCE_MINIMO_NLC > confidente
						&& WatsonServiceFactory.CONFIDENCE_MINIMO_RR < confidente) {
					gravaPerguntaEncontrada(topico, classificacao, WatsonServiceFactory.SENTIMENTO_NEGATIVO);
					resposta = searchRetrieve(converse.getInput());

				} else if (WatsonServiceFactory.CONFIDENCE_MINIMO_NLC <= confidente) {
					gravaPerguntaEncontrada(topico, classificacao, WatsonServiceFactory.SENTIMENTO_POSITIVO);

					RespostaDAO respostaDAO = new RespostaDAO(entityManager);
					resposta = respostaDAO.findByDescricao(topClass);

				} else {
					gravaPerguntaEncontrada(topico, classificacao, WatsonServiceFactory.SENTIMENTO_NEGATIVO);

					RespostaDAO respostaDAO = new RespostaDAO(entityManager);
					resposta = respostaDAO.findByDescricao(topClass);
				}
			}
		} catch (JsonSyntaxException e) {
			log.warn(e.getMessage());
		}

		return resposta;
	}

	private String searchRetrieve(String pergunta) {
		QueryResponse queryResponse = serviceRetrieveAndRank.searchAllDocs(pergunta);

		SolrDocumentList results = queryResponse.getResults();

		SolrDocument solrDocument = results.get(0);
		Object fieldValue = solrDocument.getFieldValue("body");
		String resposta = fieldValue.toString();
		resposta = resposta.substring(1, resposta.length() - 1);

		return resposta;
	}

	private Conversation getDialog() throws ApplicationException {
		conversation = new Conversation();

		if (usuarioPerfil != null) {
			if (usuarioPerfil.getClientId() != null) {
				conversation.setClientId(Integer.parseInt(usuarioPerfil.getClientId()));
			}

			if (usuarioPerfil.getConversationId() != null) {
				conversation.setId(usuarioPerfil.getConversationId());
			}
		}

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
			String pergunta = conversation.getInput();

			if (resposta.indexOf(INFORMAR_NOME) >= 0) {
				usuarioPerfil.setNome(pergunta);
				isUpdate = Boolean.TRUE;
			}

			if (resposta.indexOf(INFORMAR_DATA_NASCIMENTO) >= 0) {

				try {
					date = LocalDate.parse(pergunta, formatter);
				} catch (Exception e) {
					isUpdate = Boolean.FALSE;
					throw new ApplicationException(
							"Data de nascimento inválida. Favor digitar novamente no formato: dd/mm/aaaa", e);
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
					throw new ApplicationException(
							"Data de admissão inválida. Favor digitar novamente no formato dd/mm/aaaa", e);
				}

				if (date.isAfter(dateAtual) || date.isBefore(dateAdmissao)) {
					throw new ApplicationException("Data de admissão inválida. Digite um valor válido");
				}

				usuarioPerfil.setDataAdmissao(date);
				isUpdate = Boolean.TRUE;
			}

		}

		if (usuarioPerfil.getEmail() != null && !"".equals(usuarioPerfil.getEmail()) && usuarioPerfil.getId() == null) {
			isUpdate = Boolean.TRUE;
		}

		if (isUpdate) {
			usuarioPerfil.setClientId(conversation.getClientId().toString());
			usuarioPerfil.setConversationId(conversation.getId());
			usuarioPerfil.setDialogId(conversation.getDialogId());

			UsuarioPerfilDAO usuarioPerfilDAO = new UsuarioPerfilDAO(entityManager);

			if (usuarioPerfil.getId() == null) {
				usuarioPerfilDAO.save(usuarioPerfil);
			} else {
				usuarioPerfilDAO.update(usuarioPerfil);
			}

		}
	}

	private String getIdDialog() {
		// Pega todos os Dialog configurados
		List<Dialog> dialogs = service.getDialogs();

		// Retorna o id do Dialog encontrado
		return dialogs.get(0).getId();
	}

	private void getProfileUser() {
		UsuarioPerfilDAO usuarioPerfilDAO = new UsuarioPerfilDAO(entityManager);
		UsuarioPerfil profile = null;

		if (usuarioPerfil.getEmail() != null && !usuarioPerfil.getEmail().isEmpty()) {
			profile = usuarioPerfilDAO.findByEmail(usuarioPerfil.getEmail());
		} else if (usuarioPerfil.getClientId() != null) {
			profile = usuarioPerfilDAO.findByIdCliente(usuarioPerfil.getClientId());
		}

		if(profile != null){
			usuarioPerfil = profile;
			HashMap<String, String> profileValues = new HashMap<>();
			profileValues.put("NOME", usuarioPerfil.getNome());
			profileValues.put("DATA_ADM", DateUtils.format(profile.getDataAdmissao()));
			profileValues.put("DATA_NASC", DateUtils.format(profile.getDataNascimento()));
			service.updateProfile(profile.getDialogId(), Integer.parseInt(profile.getClientId()),
					profileValues);
		}
	}

	@POST
	@Path("/dialog")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public DialogVO dialog(final DialogVO pergunta) {
		initDados();

		usuarioPerfil = new UsuarioPerfil();
		usuarioPerfil.setClientId(pergunta.getIdCliente());

		getProfileUser();

		conversation = getDialog();
		conversation = service.converse(conversation, pergunta.getDialogo());
		updateUsuarioPerfil(conversation);

		DialogVO dialogVO = new DialogVO();
		dialogVO.setHorario(TimeUtils.timeNow());
		dialogVO.setPessoa("M.Watson");
		dialogVO.setDialogo(tratarRespostas(conversation));

		return dialogVO;
	}

	@GET
	@Path("/dialogForeHand/{email}")
	public DialogVO dialogForeHand(@PathParam("email") String email) {
		initDados();

		usuarioPerfil = new UsuarioPerfil();
		usuarioPerfil.setEmail(email);

		getProfileUser();

		conversation = getDialog();
		conversation = service.converse(conversation, "Oi");

		updateUsuarioPerfil(conversation);

		DialogVO dialogVO = new DialogVO();
		dialogVO.setHorario(TimeUtils.timeNow());
		dialogVO.setIdConversation(conversation.getId().toString());
		dialogVO.setIdDialog(conversation.getDialogId());
		dialogVO.setIdCliente(conversation.getClientId().toString());
		dialogVO.setPessoa("M.Watson");
		dialogVO.setDialogo(tratarRespostas(conversation));

		return dialogVO;
	}
}
