package br.com.afirmanet.questions.constante;

import lombok.Getter;
import lombok.Setter;

public enum ServiceResponseError {
	BAD_REQUEST(
			400, 
			"Bad Request", 
			"Dados ausentes ou mal formados ou o conjunto de dados "
			+ "é muito pequeno. Provavelmente causado por falta de "
			+ "dados de Training ou CSV mal formado."
	),
	NAO_AUTORIZADO(
			401, 
			"Não Autorizado", 
			"Nenhuma chave de API fornecido, ou a chave de "
			+ "API fornecida não era válido."
	),
	PROIBIDO(
			403, 
			"Proibido",
			"O pedido não é permitido, o que pode ser causado "
			+ "por um ponto final incorreto. Quando você vê esta "
			+ "resposta, a solicitação não alcançou o serviço de "
			+ "Linguagem Natural classificador."
	),
	NAO_ENCONTRADO(
			404,
			"Não Encontrado",
			"O item ou parâmetro solicitado não existe."
	),
	METODO_NAO_PERMITIDO(
			405, 
			"Método Não Permitido",
			"O método de solicitação não é suportado. Verifique o "
			+ "cabeçalho de resposta Permitir para os métodos HTTP "
			+ "que o método oferece suporte. Quando você vê esta "
			+ "resposta, a solicitação não alcançou o serviço de "
			+ "Linguagem Natural classificador."
	),
	NAO_ACEITAVEL(
			406, 
			"Não é aceitável", 
			"A solicitação inclui muitas restrições. "
			+ "Quando você vê esta resposta, a solicitação "
			+ "não alcançou o serviço de Linguagem Natural classificador."),
			
	CONFLITOS(
			409, 
			"Conflitos", 
			"Provavelmente causado por um problema de servidor "
			+ "temporário ou dados de treinamento inválidos. "
			+ "Certifique-se de que os seus dados de treinamento "
			+ "adere aos requisitos de formato e de dados e reenviar "
			+ "o pedido para tentar novamente."
	),
	TIPO_MIDIA_INCOMPATIVEL(
			415, 
			"Tipo de mídia incompatível", 
			"O pedido está em um tipo de mídia que o servidor não "
			+ "entende. Quando você vê esta resposta, a solicitação "
			+ "não alcançou o serviço de Linguagem Natural classificador."
	),
	ERRO_INTERNO_SERVIDOR(
			500, 
			"Erro interno do servidor", 
			"Algo está errado em nosso servidor. Reenviar o pedido mais tarde."
	);
	
	@Getter
	@Setter
	private int codigo;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private String mensagem;

	private ServiceResponseError(int codigo, String descricao, String mensagem) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.mensagem = mensagem;
	}
}
