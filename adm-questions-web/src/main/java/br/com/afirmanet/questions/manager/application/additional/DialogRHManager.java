package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.omnifaces.cdi.ViewScoped;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import com.ibm.watson.developer_cloud.dialog.v1.model.Dialog;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;

import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.dao.UsuarioPerfilDAO;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.entity.UsuarioPerfil;
import br.com.afirmanet.questions.manager.vo.DialogVO;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class DialogRHManager extends NaturalLanguage implements Serializable {
	private static final long serialVersionUID = 7201661374971816987L;
	
	private static final int PESQUISAR_DB = 1;
	
	@Inject
	@ApplicationManaged
	protected EntityManager entityManager;
	
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
	
	@Override
	protected void inicializar() {
		ClienteDAO clieteDAO = new ClienteDAO(entityManager);
		setCliente(clieteDAO.findByNome("m.watson"));

		TopicoDAO topicoDAO = new TopicoDAO(entityManager);
		lstTopico = topicoDAO.findbyCliente(getCliente());
		setTopico(lstTopico.get(0));
		
		lstDialog = new ArrayList<>();
		
		actionUsuarioPerfil = Boolean.TRUE;
		actionDialog  = Boolean.FALSE;	

	}
	
	@Transactional
	public void btPergunta(){
		limparVariaveis();
		
		if(pergunta != null &&  !"".equals(pergunta)){
			conversation = getDialog();
			Conversation converse = getServiceDialog().converse(conversation, getDialogoUsuario());

			DialogVO dialogVO = new DialogVO();
			dialogVO.setPessoa("M.Watson");
			dialogVO.setDialogo(tratarRespostas(converse));

			lstDialog.add(dialogVO);
			pergunta = "";
		}
	}

	private String tratarRespostas(Conversation converse) {
		String resposta = converse.getResponse().get(0);

		try {
			JsonParser parser = new JsonParser();			
			JsonObject json = (JsonObject) parser.parse(resposta);
			
			JsonElement jsonElement = json.get("pesquisaBD");
			int pesquisaBD = jsonElement.getAsInt();

			if(PESQUISAR_DB == pesquisaBD){
				jsonElement = json.get("classificacao");
				String topClass = jsonElement.getAsString();
				RespostaDAO respostaDAO = new RespostaDAO(entityManager);
				resposta = respostaDAO.findByDescricao(topClass);
				
				Classification classificacao =new Classification();
				classificacao.setTopConfidence(CONFIDENCE_MINIMO);
				classificacao.setText(converse.getInput());
				classificacao.setTopClass(topClass);
				classificacao.setId(converse.getDialogId());
				
				gravaPerguntaEncontrada(classificacao, SENTIMENTO_ENCONTRADA_DIALOG);
				
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return resposta;
	}

	@Transactional
	public void btEmail(){
		if(email != null &&  !"".equals(email)){
			actionUsuarioPerfil = Boolean.FALSE;
			actionDialog  = Boolean.TRUE;	
			
			UsuarioPerfilDAO usuarioPerfilDAO = new UsuarioPerfilDAO(entityManager);
			usuarioPerfil = usuarioPerfilDAO.findByEmail(email);
			
			
			conversation = getDialog();
			Conversation converse = getServiceDialog().converse(conversation, "Oi");
			
			DialogVO dialogVO = new DialogVO();
			dialogVO.setPessoa("M.Watson");
			dialogVO.setDialogo(converse.getResponse().get(0));
			lstDialog.add(dialogVO);
			
			usuarioPerfil.setClientId(converse.getClientId());
			usuarioPerfil.setConversationId(converse.getId());
			usuarioPerfil.setDialogId(converse.getDialogId());
			
			usuarioPerfilDAO.update(usuarioPerfil);
			
			
		}
	}	
	
	private Conversation getDialog() {
		Conversation params = new Conversation();

		if(usuarioPerfil != null){
			if(usuarioPerfil.getClientId() != null){
				params.setClientId(usuarioPerfil.getClientId());
			}
		}

		params.setDialogId(getIdDialog());
		params.setConfidence(CONFIDENCE_MINIMO);
		params.setId(usuarioPerfil.getConversationId());
		
		return params;
	}

	private String getDialogoUsuario() {
		DialogVO dialogVO = new DialogVO();
		dialogVO.setPessoa(usuarioPerfil.getNome() != null ? usuarioPerfil.getNome() : "EU");
		dialogVO.setDialogo(pergunta);
		lstDialog.add(dialogVO);
		return pergunta;
	}

	private void limparVariaveis(){

	}
	
	private String getIdDialog() {
//		String retorno = "961aa6a0-b781-4908-b58c-e191a5da4791";
		
		//Pega todos os Dialog configurados
		List<Dialog> dialogs = getServiceDialog().getDialogs();

		//Retorna o id do Dialog encontrado
		return dialogs.get(0).getId();
		
//		return retorno;
		
	}
}
