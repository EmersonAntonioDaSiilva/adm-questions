package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.time.LocalDateTime;
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
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.UsuarioPerfil;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class DialogRH extends NaturalLanguage implements Serializable {
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
	
	@Override
	protected void inicializar() {
		ClienteDAO clieteDAO = new ClienteDAO(entityManager);
		cliente = clieteDAO.findByNome("m.watson");

		TopicoDAO classeDAO = new TopicoDAO(entityManager);
		lstClasse = classeDAO.findbyCliente(cliente);
		
		setTitulo("Chat com o RH da Magna Sistemas");
	}
	
	@Transactional
	public void btPergunta(){
		limparVariaveis();
		
		if(pergunta != null &&  !"".equals(pergunta)){
			conversation = getServiceDialog().converse(getVariavelPerfil());
		}
	}

	private Map<String, Object> getVariavelPerfil() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put(DialogService.DIALOG_ID, getIdDialog());
		params.put(DialogService.CLIENT_ID, usuarioPerfil.getClientId());
		params.put(DialogService.INPUT, getCongratulacoes());
		params.put(DialogService.CONVERSATION_ID, usuarioPerfil.getConversationId());
		
		return params;
	}

	private Object getCongratulacoes() {
		String saucoes = "Ola, ";
		LocalDateTime dateTime = LocalDateTime.now();

		if(dateTime.getHour() >= Hora_0 && dateTime.getHour() < Hora_6){ 
			// 00h as 06h = Boa madrugad
			saucoes = "Boa madrugada, ";
		} else if(dateTime.getHour() >= Hora_6 && dateTime.getHour() < Hora_12){ 
			// 06h as 12h = Bom dia
			saucoes = "Bom dia, ";
		} else if(dateTime.getHour() >= Hora_12 && dateTime.getHour() < Hora_18){ 
			// 12h as 18h = Boa tarde
			saucoes = "Boa tarde, ";
		} else if(dateTime.getHour() >= Hora_18){ 
			// // 18h as 00h = Boa noite
			saucoes = "Boa noite, ";
		}

		return saucoes;
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
