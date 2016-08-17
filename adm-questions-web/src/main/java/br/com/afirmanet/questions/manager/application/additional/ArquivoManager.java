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
import javax.persistence.PersistenceException;

import org.omnifaces.cdi.ViewScoped;

import com.google.common.io.Files;

import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.RespostaDAO;
import br.com.afirmanet.questions.dao.TopicoDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Pergunta;
import br.com.afirmanet.questions.entity.Resposta;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.utils.ApplicationPropertiesUtils;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ArquivoManager extends GenericCRUD<Resposta, Integer, RespostaDAO> implements Serializable {
	private static final long serialVersionUID = -5823701722365522817L;
	
	
	@Getter
	@Setter
	private List<Pergunta> perguntas;
	
	@Getter
	private List<Cliente> lstCliente;
	
	@Getter
	private List<Topico> lstTopico;
	
	@Override
	public void init() {
		ClienteDAO clienteDAO = new ClienteDAO(entityManager);
		lstCliente = clienteDAO.findAll();
		showInsertButton = false;
	}	
	
	private List<Topico> carregarTopicosPorCliente() {
		List<Topico> lstTopico = new ArrayList<Topico>();

		if (searchParam.getCliente() != null) {	
			TopicoDAO topicoDAO = new TopicoDAO(entityManager);
			lstTopico = topicoDAO.findbyCliente(searchParam.getCliente());
		}
		
		return lstTopico;
	}
	
	private List<Resposta> recuperarResposta() {
			RespostaDAO respostaDAO =  new RespostaDAO(entityManager);
			return respostaDAO.getDadosGeraArquivo(genericDAO.createPaginationPredicates(searchParam));
	}

	private File criarDiretorioEArquivoCSV() {
		String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		caminho = caminho + ApplicationPropertiesUtils.getValue("path.csv.respostas");
		
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
		List<Resposta> respostas = recuperarResposta();
		File file = criarDiretorioEArquivoCSV();  
		
		gravarRespostasCSV(file, respostas);
	}
}
