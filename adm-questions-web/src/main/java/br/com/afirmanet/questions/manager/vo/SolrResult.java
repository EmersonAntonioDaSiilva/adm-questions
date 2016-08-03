package br.com.afirmanet.questions.manager.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class SolrResult implements Serializable {
	private static final long serialVersionUID = 2458584783056148753L;

	private String body;
	private String id;
	private String title;
}