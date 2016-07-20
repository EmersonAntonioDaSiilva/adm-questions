package br.com.afirmanet.questions.manager.application.additional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classifier;
import com.ibm.watson.developer_cloud.service.ServiceResponseException;

import br.com.afirmanet.questions.constante.ServiceResponseErrorEnum;
import br.com.afirmanet.questions.modelo.Questao;
import br.com.afirmanet.questions.reader.QuestaoReader;
import lombok.Getter;
import lombok.Setter;
 


@Named
@ViewScoped
public class UploadManager extends NaturalLanguage implements Serializable {
	private static final long serialVersionUID = 7201661374971816987L;
	
	@Getter
	@Setter
	private UploadedFile file;

	private List<Questao> questoes;
	
	@Override
	protected void inicializar() {
		setTitulo("Upload de Arquivos.");
	}

	public void handleFileUpload(FileUploadEvent event) {
		
		try {
			file = event.getFile();
			File arquivo = new File("../" + file.getFileName());
			FileUtils.copyInputStreamToFile(file.getInputstream(), arquivo);

						
			Classifier classifier = getServiceNLC().createClassifier("NLC-RH", "pt", arquivo);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
			session.setAttribute("CLASSIFIER_ID", classifier.getId());
			
			FacesMessage message = new FacesMessage("Upload do arquivo " + file.getFileName() + " feito com sucesso!");
			FacesContext.getCurrentInstance().addMessage(null, message);
			
			QuestaoReader questaoReader = new QuestaoReader();
			this.questoes = questaoReader.getLista(arquivo);
		} catch (IllegalArgumentException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Erro: " + "Arquivo não encontrado ou nulo"));
		} catch(ServiceResponseException e) {
			for (ServiceResponseErrorEnum erro : ServiceResponseErrorEnum.values()) {
				if (erro.getCodigo() == e.getStatusCode()) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage("Erro: " + erro.getMensagem()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private File getFileUpload(UploadedFile file2) {
		try {
			InputStreamReader reader = new InputStreamReader(file2.getInputstream());
			int partition = 1024;
			int length = 0;
			int position = 0;
			char[] buffer = new char[partition];
			FileWriter fstream = new FileWriter("../../" + file2.getFileName());
			do{
			    length = reader.read(buffer, position, partition);
			    if(length > 0)
			    	fstream.write(buffer, position, length);
			}while(length > 0);

		} catch (IOException e) {
			e.printStackTrace();
		}
		File fileRetorno = new File("../../" + file2.getFileName());
		
		return fileRetorno;
	}
	
	public static String removeAccents(String str) {
	    str = Normalizer.normalize(str, Normalizer.Form.NFD);
	    str = str.replaceAll("[^\\p{ASCII}]", "");
	    return str;
	}

	public List<Questao> getQuestoes() {
		System.out.println();
		return questoes;
	}

	public void setQuestoes(List<Questao> questoes) {
		this.questoes = questoes;
	}
}
