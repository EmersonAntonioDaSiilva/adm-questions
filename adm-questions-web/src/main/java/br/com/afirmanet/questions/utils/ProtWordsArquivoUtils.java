package br.com.afirmanet.questions.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.io.Files;

import br.com.afirmanet.questions.entity.ProtWords;

public class ProtWordsArquivoUtils extends ArquivoUtils {
	
	public void gravarArquivoTxt(File file, List<ProtWords> protWords) {
		try (BufferedWriter writer = Files.newWriter(file, StandardCharsets.UTF_8)) {
			writer.append(getHeader());
			protWords.forEach(
					s -> {
						try {
							writer.append(s.getDescricao() + "\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
