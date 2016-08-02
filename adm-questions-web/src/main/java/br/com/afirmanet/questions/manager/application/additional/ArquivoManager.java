package br.com.afirmanet.questions.manager.application.additional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Pergunta;
import br.com.afirmanet.questions.entity.Resposta;
import br.com.afirmanet.questions.entity.Topico;

import com.google.common.io.Files;

@Named
@ViewScoped
public class ArquivoManager extends GenericCRUD<Resposta, Integer, RespostaDAO> implements Serializable {

	private static final long serialVersionUID = -5823701722365522817L;
	
	@Getter
	@Setter
	private Cliente cliente;
	
	@Getter
	@Setter
	private Topico topico;
	
	@Getter
	@Setter
	private List<Pergunta> perguntas;
	
	@Getter
	private List<Cliente> lstCliente;
	
	@Getter
	private List<Topico> lstTopico;
	
	@Getter
	private boolean boxPesquisarPergunta;
	
	@Override
	public void init() {
		lstCliente = carregaDescricaoCliente();
		boxPesquisarPergunta = false;
		showInsertButton = false;
	}	
	
	private List<Cliente> carregaDescricaoCliente() {
		List<Cliente> lstDescricaoCliente;
		ClienteDAO clienteDAO = new ClienteDAO(entityManager);
		lstDescricaoCliente = clienteDAO.findAll();
		
		return lstDescricaoCliente;
	}
	
	private List<Topico> carregarTopicosPorCliente() {
		List<Topico> lstTopico;
		TopicoDAO topicoDAO = new TopicoDAO(entityManager);
		
		Cliente cliente = searchParam.getCliente();
		
		if (cliente != null) {	
			lstTopico = topicoDAO.findbyCliente(cliente);
			return lstTopico;
		} else {
			return new ArrayList<Topico>();
		}
	}
	
	private void recuperarResposta() {
		List<Resposta> respostas = null;
		
		if(entityList != null) {
			respostas = (List<Resposta>) entityList.getWrappedData();
		}
		
		File file = criarDiretorioEArquivoCSV();  
		gravarRespostasCSV(file, respostas);
		
//		RespostaDAO respostaDAO = new RespostaDAO(entityManager);
//		Collection<Predicate> createPaginationPredicates = respostaDAO.createPaginationPredicates(searchParam);
//		
//		List<Resposta> entities = respostaDAO.getResultList(createPaginationPredicates, null);
//		
//		System.out.println("Lista de Resposta");
//		for (Resposta resposta : entities) {
//			System.out.println(resposta.getDefinicao() + " - " + resposta.getCliente().getDescricao());
//		}
		System.out.println();
	}

	private File criarDiretorioEArquivoCSV() {
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho + "/resources/files/csv/respostas/";
		File pastas = new File(caminho);

		pastas.mkdirs();
		LocalDateTime dataHoraAtual = LocalDateTime.now();
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
		String nomeArquivo = dataHoraAtual.format(pattern);
		
		File file = new File(caminho + nomeArquivo + ".csv");
		try {
			file.createNewFile();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
	}

	private void gravarRespostasCSV(File file, List<Resposta> respostas) {
		boolean primeiraLinha = true;
		try (BufferedWriter writer = Files.newWriter(file, StandardCharsets.UTF_8)) {
			for (Resposta resposta : respostas) {
				for (Pergunta pergunta : resposta.getPerguntas()) {
					if(!primeiraLinha) {
						writer.append("\n");
					}
					writer.append("\"" + pergunta.getDescricao() + "\", " + resposta.getTitulo());
					primeiraLinha = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void selecionarCliente() {
		this.lstTopico = carregarTopicosPorCliente();
	}
	
	public void gerarArquivoCSV() {
		recuperarResposta();
	}
}
