package br.com.afirmanet.questions.manager.application.additional;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.manager.AbstractManager;
import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.core.util.TimeUtils;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.manager.vo.ConversaVO;
import br.com.afirmanet.questions.manager.vo.InterlocucaoVO;
import br.com.afirmanet.questions.service.ServiceTextSpeech;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class DialogEADManager extends AbstractManager implements Serializable {
	
	private static final long serialVersionUID = -5121765995997987972L;

	private Client client = ClientBuilder.newClient().register(JacksonFeature.class);
	
	private ServiceTextSpeech serviceTextSpeech;

	@Inject
	@ApplicationManaged
	private EntityManager entityManager;

	@Getter
	@Setter
	private String pergunta;

	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private List<Topico> lstTopico;

	@Getter
	@Setter
	private List<String> lstDialog;

	@Getter
	@Setter
	private Boolean actionUsuarioPerfil;

	@Getter
	@Setter
	private Boolean actionDialog;

	private ConversaVO dialogVO;
	private Cliente cliente;
	
	@PostConstruct
	protected void inicializar() {
		try {
			lstDialog = new ArrayList<>();

			actionUsuarioPerfil = Boolean.FALSE;
			actionDialog = Boolean.FALSE;
			
			ClienteDAO clieteDAO = new ClienteDAO(entityManager);
			cliente = clieteDAO.findByNome("Alura");
			
			serviceTextSpeech = new ServiceTextSpeech(cliente,entityManager);
			
			defineCliente();
		} catch (ApplicationException e) {
			addErrorMessage(e.getMessage(), e);
		}

	}

	public void btPergunta() throws ApplicationException {
		try{
			if (pergunta != null && !"".equals(pergunta)) {

				
				if(dialogVO.getNome() == null)
					dialogVO.setNome("EU");
				
				dialogVO.setHorario(TimeUtils.timeNow());
				dialogVO.setLocucao(pergunta);
				
				lstDialog.add(dialogVO.getHorario() + " - " + dialogVO.getNome() + ": " + dialogVO.getLocucao());
				
				Invocation saveBook = client.target(getRestServiceDialog()).path("/dialog").request().buildPost(Entity.entity(dialogVO, MediaType.APPLICATION_JSON));			 
				Response response = saveBook.invoke();
				dialogVO = response.readEntity(ConversaVO.class);
				
				InterlocucaoVO interlocucaoVO = dialogVO.getLstInterlocucaoVO().get(dialogVO.getLstInterlocucaoVO().size() -1);
				
				String strResposta = interlocucaoVO.getHorario() + " - " + interlocucaoVO.getNome() + ": " + interlocucaoVO.getInterlocutor();
				
				lstDialog.add(strResposta);
				pergunta = "";
								
				// Gera audio reprodução Isabela
				geraAudioReproducao(interlocucaoVO.getInterlocutor());
				
			}
			
		}catch(ApplicationException e){
			addErrorMessage(e.getMessage());
		}
	}
	
	public void defineCliente() throws ApplicationException {
		try {
			
			email = "caguiar@magnasistemas.com.br";
			
			if (email != null && !"".equals(email)) {

				actionUsuarioPerfil = Boolean.FALSE;
				actionDialog = Boolean.TRUE;
				
				dialogVO = new ConversaVO();
				dialogVO = client.target(getRestServiceDialog()).path("/dialogForeHand/{email}/{cliente}").resolveTemplate("email", email).resolveTemplate("cliente", "Alura").request().get(ConversaVO.class);

				InterlocucaoVO interlocucaoVO = dialogVO.getLstInterlocucaoVO().get(dialogVO.getLstInterlocucaoVO().size() -1);
				
				String strResposta = interlocucaoVO.getHorario() + " - " + interlocucaoVO.getNome() + ": " + interlocucaoVO.getInterlocutor();
				
				lstDialog.add(strResposta);
				
				// Gera audio reprodução Isabela
				geraAudioReproducao(interlocucaoVO.getInterlocutor());
			}
		} catch (ApplicationException e) {
			addErrorMessage(e.getMessage());
		}
	}
	
	public void geraAudioReproducao(String texto){
		serviceTextSpeech.executa(FacesContext.getCurrentInstance().getExternalContext().getRealPath(""),texto);
	}
	
	public StreamedContent getMedia() throws Exception{
		InputStream stream = null; // 
		return new DefaultStreamedContent(stream, "video/quicktime");
	}
	
	
}