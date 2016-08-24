package br.com.afirmanet.questions.manager.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude={"idConversation", "idDialog", "idCliente", "lstInterlocucaoVO", "horario", "locucao"})
public class ConversaVO implements Serializable {
	private static final long serialVersionUID = -6114713261531714391L;

	private String idConversation;
	private String idDialog;
	private String idCliente;
	private String email;
	private String nome;
	private String dataAdmin;
	private String dataNasci;
	private String horario;
	private String locucao;
	private List<InterlocucaoVO> lstInterlocucaoVO;
}
