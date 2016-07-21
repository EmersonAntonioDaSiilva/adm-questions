package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.omnifaces.cdi.ViewScoped;

import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import com.ibm.watson.developer_cloud.dialog.v1.model.Dialog;

import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.entity.UsuarioPerfil;
import br.com.afirmanet.questions.manager.vo.DialogVO;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class DialogRHManager extends NaturalLanguage implements Serializable {
	private static final long serialVersionUID = 7201661374971816987L;

	private static final Integer Hora_0 = 0;
	private static final Integer Hora_6 = 6;
	private static final Integer Hora_12 = 12;
	private static final Integer Hora_18 = 18;

	
	@Inject
	@ApplicationManaged
	protected EntityManager entityManager;
	
	@Getter
	@Setter
	private String pergunta;
	
	@Getter
	@Setter
	private Conversation conversation;
	
	private Cliente cliente;
	
	@Getter
	@Setter
	private Topico classe;
	
	@Getter
	@Setter
	private List<Topico> lstClasse;
	
	@Getter
	@Setter	
	private UsuarioPerfil usuarioPerfil;
	
	
	@Getter
	@Setter
	private List<DialogVO> lstDialog;
	
	
	@Override
	protected void inicializar() {
		ClienteDAO clieteDAO = new ClienteDAO(entityManager);
		cliente = clieteDAO.findByNome("m.watson");

		TopicoDAO classeDAO = new TopicoDAO(entityManager);
		lstClasse = classeDAO.findbyCliente(cliente);
		
		setTitulo("Chat com o RH da Magna Sistemas");
		
		lstDialog = new ArrayList<>();
	}
	
	@Transactional
	public void btPergunta(){
		limparVariaveis();
		
		if(pergunta != null &&  !"".equals(pergunta)){
			conversation = getServiceDialog().converse(getDialog());

			DialogVO dialogVO = new DialogVO();
			dialogVO.setPessoa("M.Watson");
			dialogVO.setDialogo(conversation.getResponse().get(0));
			lstDialog.add(dialogVO);
			
			usuarioPerfil.setClientId(conversation.getClientId().toString());
			usuarioPerfil.setConversationId(conversation.getId().toString());
			usuarioPerfil.setDialogId(conversation.getDialogId());
			
		}
	}

	private Map<String, Object> getDialog() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put(DialogService.DIALOG_ID, getIdDialog());
		params.put(DialogService.CLIENT_ID, usuarioPerfil.getClientId());
		params.put(DialogService.INPUT, getDialogoUsuario());
		params.put(DialogService.CONVERSATION_ID, usuarioPerfil.getConversationId());
		
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
		//Pega todos os Dialog configurados
		List<Dialog> dialogs = getServiceDialog().getDialogs();
		
		//Identifica o Dialog senecionado para a conversa
		List<Dialog> filter = dialogs.stream().filter(p -> p.getName().compareTo(classe.getDescricao()) == 0).collect(Collectors.toList());;
		
		//Retorna o id do Dialog encontrado
		return filter.get(0).getId();
		
	}
}
