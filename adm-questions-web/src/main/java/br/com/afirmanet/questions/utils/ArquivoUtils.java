package br.com.afirmanet.questions.utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArquivoUtils {
	
	public File criarDiretorioEArquivo(String caminho, String extensao) {
		return criarDiretorioEArquivo(caminho, extensao, null);
	}
	
	public File criarDiretorioEArquivo(String caminho, String extensao, String nomeArquivo) {
		File pastas = new File(caminho);
		pastas.mkdirs();
		
		// Caso o nome do arquivo não esteja identificado
		if(nomeArquivo == null) {
			LocalDateTime dataHoraAtual = LocalDateTime.now();
			DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
			nomeArquivo = dataHoraAtual.format(pattern);
		}
		
		// Cria o arquivo
		File file = new File(caminho + nomeArquivo + extensao);
		try {
			file.createNewFile();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
	}
	
	protected String getHeader() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("# A ASF (Apache Service Foundation) licencia este arquivo para você sob a Licença Apache, Versão 2.0\n");
		builder.append("# (the \"License\"); Você não pode usar este arquivo exceto em conformidade com\n");
		builder.append("# a licença. Você pode obter uma cópia da lincença em:\n");
		builder.append("#\n");
		builder.append("#     http://www.apache.org/licenses/LICENSE-2.0\n");
		builder.append("#\n");
		builder.append("# A menos que exigido pela lei aplicável ou acordado por escrito, o software\n");
		builder.append("# distribuído sob a Licença é distribuído \"COMO ESTÁ\",\n");
		builder.append("# SEM GARANTIAS OU CONDIÇÕES DE QUALQUER TIPO, expressa ou implícita.\n");
		builder.append("# Consulte a licença para as permissões específicas que regem a linguagem e\n");
		builder.append("# limitações sob a Licença.\n");
		builder.append("\n");
		builder.append("#-----------------------------------------------------------------------\n");
		
		return builder.toString();
	}
}
