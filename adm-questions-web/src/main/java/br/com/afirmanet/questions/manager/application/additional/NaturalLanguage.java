package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;

import lombok.Getter;
import lombok.Setter;

public abstract class NaturalLanguage implements Serializable {
	private static final long serialVersionUID = 5946605316434150596L;

	
	@Getter
	@Setter
	private String titulo;
	
	@Getter
	private NaturalLanguageClassifier serviceNLC;

	@Getter
	private DialogService serviceDialog;
	
	@PostConstruct
	public void init() {
		serviceNLC = new NaturalLanguageClassifier();
		serviceNLC.setUsernameAndPassword("b3753735-5a39-4aec-8180-b945ffcb5d9c", "gvCs1drkEJRf");
		
		serviceDialog = new DialogService();
		serviceDialog.setUsernameAndPassword("ccc5beca-13e5-4f63-a7e0-bbe04d90b0ee", "6q2YZPnCRrNC");
		
		inicializar();
	}
	
	
	
	protected abstract void inicializar();
}
