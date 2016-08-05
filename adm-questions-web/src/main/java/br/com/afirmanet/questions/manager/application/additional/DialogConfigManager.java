package br.com.afirmanet.questions.manager.application.additional;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.questions.dialog.DialogType;
import br.com.afirmanet.questions.dialog.ObjectFactory;
import br.com.afirmanet.questions.dialog.VarFolderType;
import br.com.afirmanet.questions.dialog.VarFolderTypeType;
import br.com.afirmanet.questions.dialog.VarType;
import br.com.afirmanet.questions.dialog.VarTypeType;
import br.com.afirmanet.questions.dialog.VariablesType;

@Named
@ViewScoped
public class DialogConfigManager implements Serializable {
	
	private static final long serialVersionUID = 6220339039091756313L;
	
	private ObjectFactory objFactory;
	private DialogType dialog;
	private VarType varType;
	
	private String nomeVariavel;
	private String tipoVariavel;
	
	@PostConstruct
	public void init() {
		objFactory = new ObjectFactory();
		dialog = objFactory.createDialogType();
		varType = objFactory.createVarType();
	}
	
	public void incluirVariavel() {
		//Cria variavel
		varType.setName(nomeVariavel);
		varType.setType(VarTypeType.TEXT);
		
		
		if (dialog.getVariables() != null && dialog.getVariables().size() > 0) {
			for (VariablesType variablesType : dialog.getVariables()) {
				for (VarFolderType varFolderType : variablesType.getVarFolder()) {
					if (varFolderType.getName().equals("Home") && varFolderType.getType().equals(VarFolderTypeType.VAR)) {
						varFolderType.getVar().add(varType);
					}
				}
			}
		} else {
			//Cria pasta de variáveis do tipo Variavel
			VarFolderType varFolderType = objFactory.createVarFolderType();
			varFolderType.setName("Home");
			varFolderType.setType(VarFolderTypeType.VAR);
			varFolderType.getVar().add(varType);
			
			//Cria sessao de variáveis do XML
			VariablesType variablesType = objFactory.createVariablesType();
			variablesType.getVarFolder().add(varFolderType);
			
			//Adiciona à configuração do Dialog
			dialog.getVariables().add(variablesType);
		}
		varType = objFactory.createVarType();
		tipoVariavel = "";
	}

	public void gerarArquivoXMLDialog() {
		try {
			JAXBContext context = JAXBContext.newInstance("br.com.afirmanet.questions.dialog");
			Marshaller marshaller = context.createMarshaller();
			JAXBElement<DialogType> element = objFactory.createDialog(dialog);
			
			OutputStream fileOS = new FileOutputStream("C:\\Teste\\Dialog.xml");
			
			marshaller.marshal(element, fileOS);
			
			fileOS.close();
			
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DialogType getDialog() {
		return dialog;
	}

	public void setDialog(DialogType dialog) {
		this.dialog = dialog;
	}
	
	public String getNomeVariavel() {
		return nomeVariavel;
	}
	public void setNomeVariavel(String nomeVariavel) {
		this.nomeVariavel = nomeVariavel;
	}
	public String getTipoVariavel() {
		return tipoVariavel;
	}
	public void setTipoVariavel(String tipoVariavel) {
		this.tipoVariavel = tipoVariavel;
	}

}
