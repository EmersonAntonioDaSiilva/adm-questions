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
		serviceNLC.setUsernameAndPassword("571a35dd-ad5d-42a0-8775-d44d9152a9bd", "A4nFDssYwktc");
		
		serviceDialog = new DialogService();
		serviceDialog.setUsernameAndPassword("f618b7dd-c8f7-47fb-9b90-b5d6e39ecd72", "hNl4yQmgZOMI");
		
		inicializar();
	}
	
	
	
	protected abstract void inicializar();
}
