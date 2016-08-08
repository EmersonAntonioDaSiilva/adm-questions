package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.omnifaces.cdi.ViewScoped;

import br.com.afirmanet.core.manager.AbstractManager;
import br.com.afirmanet.core.producer.ApplicationManaged;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.service.ServiceRetrieveAndRank;

@Named
@ViewScoped
public class ClusterSolrManager extends AbstractManager implements Serializable {
	private static final long serialVersionUID = 3925507169074921562L;

	@Inject
	@ApplicationManaged
	private EntityManager entityManager;
	
	private static String nomeCluster = "MAGNA_RH_TEST",
						  nomeConfig = "CONF_RH_TEST",
			 			  nomeColection = "COLLEC_RH_TEST"; 
	
	private ServiceRetrieveAndRank service;
	private Cliente cliente;
	
	@PostConstruct
	protected void inicializar() {
		ClienteDAO clieteDAO = new ClienteDAO(entityManager);
		cliente = clieteDAO.findByNome("m.watson");
	
		service = new ServiceRetrieveAndRank(cliente, entityManager);
	}

	public void btGeraCluster(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			String idCluster = "";
			if(!service.existsSolrCluster()){
				
				idCluster = service.createCluster(nomeCluster, null);
				service.uploadConfiguration(idCluster, nomeConfig);
				service.createCollection(idCluster, nomeConfig, nomeColection);
				service.indexDocumentAndCommit(idCluster, nomeColection);
				
				// Mensagem após cluster criado
				String mensagem = "Cluster foi criado com sucesso. O identificador do cluster é: "+ idCluster;
				context.addMessage("clusterMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem, mensagem));
			}
			else{
				// Mensagem de cluster criado
				context.addMessage("clusterMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Cluster já criado.", "Cluster já criado."));
			}
		}
		catch(Exception e){
			e.printStackTrace();
			
			// Exibe mensagem de erro na tela
			context.addMessage("clusterMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
		}
	}
	
}