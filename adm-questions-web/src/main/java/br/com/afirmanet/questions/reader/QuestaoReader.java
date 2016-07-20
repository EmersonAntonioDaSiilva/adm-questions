package br.com.afirmanet.questions.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.afirmanet.questions.modelo.Questao;
import br.com.afirmanet.questions.utils.TextoUtils;

public class QuestaoReader {
	
	public List<Questao> getLista(File arquivo) throws IOException {
		List<Questao> questoes = new ArrayList<Questao>();
		
		BufferedReader br = new BufferedReader(new FileReader(arquivo));
		String line = null;
		
		while ((line = br.readLine()) != null) {
			line = TextoUtils.removeAcentosECaracteresEspeciais(line);
			String[] colunas = line.split(",");
			
			String pergunta = colunas[0];
			String resposta = colunas[1];
			
			Questao questao = new Questao(pergunta, resposta);
			
			questoes.add(questao);
		}
		return questoes;
	}
}
