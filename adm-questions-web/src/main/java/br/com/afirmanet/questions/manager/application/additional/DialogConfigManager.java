package br.com.afirmanet.questions.manager.application.additional;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.questions.dialog.ChatflowNode;
import br.com.afirmanet.questions.dialog.DialogType;
import br.com.afirmanet.questions.dialog.FlowType;
import br.com.afirmanet.questions.dialog.FolderNodeType;
import br.com.afirmanet.questions.dialog.GrammarType;
import br.com.afirmanet.questions.dialog.GrammarTypeType;
import br.com.afirmanet.questions.dialog.InputNodeType;
import br.com.afirmanet.questions.dialog.ObjectFactory;
import br.com.afirmanet.questions.dialog.OutputNodeType;
import br.com.afirmanet.questions.dialog.PromptType;
import br.com.afirmanet.questions.dialog.PromptTypeType;
import br.com.afirmanet.questions.dialog.SelectionTypeType;
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
	private FlowType flow;
	private VarType varType;
	private FolderNodeType folderNode;
	private InputNodeType inputNode;
	private OutputNodeType outputNode;
	private GrammarType grammar;
	private PromptType prompt;
	private VarFolderType varFolder;
	private VariablesType variables;

	//Form Library
	private String idNo;
	private String entrada;
	private String saida;
	private String nomePastaLibrary;
	private String goTo;
	
	//Form Variables
	private String nomeVariavel;
	private String tipoVariavel;
	private String nomePasta;
	private List<SelectItem> listaPastas;

	@PostConstruct
	public void init() {
		objFactory = new ObjectFactory();
		dialog = objFactory.createDialogType();
		
		varType = objFactory.createVarType();
	}
	
	public void incluirNoLibrary() {
		if (flow != null && flow.getFolder().size() > 0) {
			for (FolderNodeType folderNodeIt : flow.getFolder()) {
				if (folderNodeIt.getId().equals(nomePastaLibrary)) {
					for (ChatflowNode chatflowNodeInputIt : folderNodeIt.getInputOrOutputOrDefault()) {
						if (chatflowNodeInputIt.getId().equals(idNo) && chatflowNodeInputIt instanceof InputNodeType){
							InputNodeType inputNodeTmp = (InputNodeType) chatflowNodeInputIt;
							for (Object itNo : inputNodeTmp.getActionOrScriptOrGrammar()) {
								if (itNo instanceof GrammarType){
									GrammarType conteudoNo = (GrammarType) itNo;
									conteudoNo.getItemOrSourceOrParam().add(objFactory.createGrammarTypeItem(entrada));
								}
							}
							for (ChatflowNode chatflowNodeConteudoIt : inputNodeTmp.getInputOrOutputOrDefault()) {
								if (chatflowNodeConteudoIt instanceof OutputNodeType) {
									OutputNodeType outputNodeNo = (OutputNodeType) chatflowNodeConteudoIt;
									for (Object outputConteudoIt : outputNodeNo.getActionOrScriptOrPrompt()){
										if (outputConteudoIt instanceof PromptType) {
											PromptType prompt = (PromptType) outputConteudoIt;
											prompt.getContent().add(objFactory.createPromptTypeItem(saida));
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (flow.equals(null)) {
				flow = objFactory.createFlowType();
			}
			
			//grammar
			grammar = objFactory.createGrammarType();
			grammar.getItemOrSourceOrParam().add(objFactory.createGrammarTypeItem(entrada));
			
			//output
			prompt = objFactory.createPromptType();
			prompt.setSelectionType(SelectionTypeType.RANDOM);
			prompt.getContent().add(objFactory.createPromptTypeItem(saida));
			outputNode = objFactory.createOutputNodeType();
			outputNode.getActionOrScriptOrPrompt().add(prompt);
			
			//inputNode
			inputNode = objFactory.createInputNodeType();
			inputNode.getActionOrScriptOrGrammar().add(grammar);
			inputNode.getInputOrOutputOrDefault().add(outputNode);
			
			folderNode = objFactory.createFolderNodeType();
			folderNode.setLabel(nomePastaLibrary);
			folderNode.getInputOrOutputOrDefault().add(inputNode);
			
			flow.getFolder().add(folderNode);
		}
	}
	
	public void incluirVariavel() {
		//Cria variavel
		//varType.setName(nomeVariavel);
		varType.setType(VarTypeType.TEXT);
		
		boolean criaFolder = true;
		
		if (variables != null && variables.getVarFolder().size() > 0) {
			for (VarFolderType varFolderIt : variables.getVarFolder()) {
				if (varFolderIt.getName().equals(nomePasta) && varFolderIt.getType().equals(VarFolderTypeType.VAR)) {
					varFolder.getVar().add(varType);
					criaFolder = false;
				}
			}
			if (criaFolder) {
				varFolder = objFactory.createVarFolderType();
				varFolder.setName(nomePasta);
				varFolder.setType(VarFolderTypeType.VAR);
				varFolder.getVar().add(varType);
				
				variables.getVarFolder().add(varFolder);
			}
		} else {
			//Cria pasta de variáveis do tipo Variavel
			varFolder = objFactory.createVarFolderType();
			varFolder.setName(nomePasta);
			varFolder.setType(VarFolderTypeType.VAR);
			varFolder.getVar().add(varType);
			
			//Cria sessao de variáveis do XML
			variables = objFactory.createVariablesType();
			variables.getVarFolder().add(varFolder);
		}
		varType = objFactory.createVarType();
		tipoVariavel = "";
		nomePasta = "";
	}

	public void gerarArquivoXMLDialog() {
		//Adiciona à configuração do Dialog
		dialog = objFactory.createDialogType();
		dialog.getVariables().add(variables);
		
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
	
	public VarType getVarType() {
		return varType;
	}

	public void setVarType(VarType varType) {
		this.varType = varType;
	}
	
	public VarFolderType getVarFolder() {
		return varFolder;
	}

	public void setVarFolder(VarFolderType varFolder) {
		this.varFolder = varFolder;
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
	
	public String getNomePasta() {
		return nomePasta;
	}

	public void setNomePasta(String nomePasta) {
		this.nomePasta = nomePasta;
	}

	public VariablesType getVariables() {
		return variables;
	}

	public void setVariables(VariablesType variables) {
		this.variables = variables;
	}

	public List<SelectItem> getListaPastas() {
		listaPastas = new ArrayList<SelectItem>();
		if (variables != null && variables.getVarFolder().size() > 0) {
			for (VarFolderType varFolderIt : variables.getVarFolder()) {
				listaPastas.add(new SelectItem(varFolderIt.getName(), varFolderIt.getName()));
			}
		}
		return listaPastas;
	}

	public void setListaPastas(List<SelectItem> listaPastas) {
		this.listaPastas = listaPastas;
	}
	
	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getEntrada() {
		return entrada;
	}

	public void setEntrada(String entrada) {
		this.entrada = entrada;
	}

	public String getSaida() {
		return saida;
	}

	public void setSaida(String saida) {
		this.saida = saida;
	}

	public String getNomePastaLibrary() {
		return nomePastaLibrary;
	}

	public void setNomePastaLibrary(String nomePastaLibrary) {
		this.nomePastaLibrary = nomePastaLibrary;
	}

	public String getGoTo() {
		return goTo;
	}

	public void setGoTo(String goTo) {
		this.goTo = goTo;
	}
}
