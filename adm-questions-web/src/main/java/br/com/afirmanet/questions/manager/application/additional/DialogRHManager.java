package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.manager.AbstractManager;
import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.core.util.TimeUtils;
import br.com.afirmanet.questions.entity.Topico;
import br.com.afirmanet.questions.manager.vo.DialogVO;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class DialogRHManager extends AbstractManager implements Serializable {
	private static final long serialVersionUID = 7201661374971816987L;

	private Client client = ClientBuilder.newClient().register(JacksonFeature.class);
	
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
	private List<DialogVO> lstDialog;

	@Getter
	@Setter
	private Boolean actionUsuarioPerfil;

	@Getter
	@Setter
	private Boolean actionDialog;

	private DialogVO dialogVO;
	
	@PostConstruct
	protected void inicializar() {
		try {
			lstDialog = new ArrayList<>();

			actionUsuarioPerfil = Boolean.TRUE;
			actionDialog = Boolean.FALSE;
		} catch (ApplicationException e) {
			addErrorMessage(e.getMessage(), e);
		}

	}

	@Transactional
	public void btPergunta() throws ApplicationException {
		try{
			if (pergunta != null && !"".equals(pergunta)) {

				
				DialogVO perguntaDialogVO = new DialogVO();
				perguntaDialogVO.setPessoa("Eu");
				perguntaDialogVO.setIdCliente(dialogVO.getIdCliente());
				perguntaDialogVO.setHorario(TimeUtils.timeNow());
				perguntaDialogVO.setDialogo(pergunta);
				lstDialog.add(perguntaDialogVO);

				
				Invocation saveBook = client.target(getRestServiceDialog()).path("/dialog").request().buildPost(Entity.entity(perguntaDialogVO, MediaType.APPLICATION_JSON));			 
				Response response = saveBook.invoke();
				DialogVO resposta = response.readEntity(DialogVO.class);

				lstDialog.add(resposta);
				pergunta = "";
			}
			
		}catch(ApplicationException e){
			addErrorMessage(e.getMessage());
		}
	}


	
	@Transactional
	public void btEmail() throws ApplicationException {
		try {
			if (email != null && !"".equals(email)) {

				actionUsuarioPerfil = Boolean.FALSE;
				actionDialog = Boolean.TRUE;

				dialogVO = client.target(getRestServiceDialog()).path("/dialogForeHand/{email}").resolveTemplate("email", email).request().get(DialogVO.class);

				lstDialog.add(dialogVO);
			}
		} catch (ApplicationException e) {
			addErrorMessage(e.getMessage());
		}
	}
}
