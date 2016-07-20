package br.com.afirmanet.questions.utils;

import java.text.Normalizer;

public class TextoUtils {
	
    public static String removeAcentosECaracteresEspeciais(final String texto) {
    	String textoSemAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD);
    	textoSemAcentos = textoSemAcentos.replaceAll("[^\\p{ASCII}]", "");
    	textoSemAcentos = textoSemAcentos.replaceAll("\"", "");
    	return textoSemAcentos;
    }
    
    public static String removeAcentos(final String texto) {
    	String textoSemAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD);
    	
    	return textoSemAcentos;
    }
}
