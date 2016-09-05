package br.com.afirmanet.questions.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.io.Files;

import br.com.afirmanet.questions.entity.Synonyms;

public class SynonymsArquivoUtils extends ArquivoUtils {

	public void gravarArquivoTxt(File file, List<Synonyms> synonyms) {

		try (BufferedWriter writer = Files.newWriter(file, StandardCharsets.UTF_8)) {
			writer.append(super.getHeader());
			
			List<Synonyms> groups = synonyms.stream().filter(s -> !s.getMapeado()).collect(Collectors.toList());
			List<Synonyms> mappings = synonyms.stream().filter(s -> s.getMapeado()).collect(Collectors.toList());
			
			groups.forEach(
				g -> {
					try {
						writer.append(g.getDescricao() + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			);
			
			writer.append("\n");
			writer.append("# Synonym mappings can be used for spelling correction too\n");
			
			mappings.forEach(
				m -> {
					try {
						writer.append(m.getDescricao() + "\n");
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
