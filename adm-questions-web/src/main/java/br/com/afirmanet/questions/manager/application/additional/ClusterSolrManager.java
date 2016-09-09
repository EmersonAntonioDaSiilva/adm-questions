package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.omnifaces.cdi.ViewScoped;

import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.core.manager.GenericCRUD;
import br.com.afirmanet.questions.dao.ClienteDAO;
import br.com.afirmanet.questions.dao.ClusterSolrDAO;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.entity.ClusterSolr;
import br.com.afirmanet.questions.entity.ClusterSolr.Status;
import br.com.afirmanet.questions.entity.ClusterSolr.Unidade;
import br.com.afirmanet.questions.service.ServiceRetrieveAndRank;
import lombok.Getter;

@Named
@ViewScoped
public class ClusterSolrManager extends GenericCRUD<ClusterSolr, Integer, ClusterSolrDAO> implements Serializable {
	private static final long serialVersionUID = 3925507169074921562L;

	private ServiceRetrieveAndRank service;

	@Getter
	private List<Cliente> lstCliente;
	
	@Getter
	private List<ClusterSolrDAO> lstClustersSolr;
	
	@Getter
	private List<Unidade> lstUnidade;
	
	@Override
	public void init() {
		ClienteDAO clienteDAO = new ClienteDAO(entityManager);
		lstCliente = clienteDAO.findAll();
		lstUnidade = Arrays.asList(Unidade.values());
	}
	
	@Override
	protected void beforeSave() {
		validarRegistro();
		
		entity.setDatacadastro(LocalDateTime.now());
		entity.setStatusCluster(Status.INATIVO);
	}
	
	public void selecionarCliente() {
		service = new ServiceRetrieveAndRank(entity.getCliente(), entityManager);
	}
	
	@Transactional
	public void sincronizarCluster() {
		
		SolrCluster cluster = service.sincronizarConfiguracao(entity.getNomeCluster());
		FacesContext context = FacesContext.getCurrentInstance();
		
		if(cluster == null) {
			context.addMessage("sinchronizedCluster", new FacesMessage("Não existe o cluster criado"));
			
			entity.setIdCluster(null);
			entity.setStatusCluster(Status.INATIVO);
		} else {
			context.addMessage("sinchronizedCluster", new FacesMessage("Cluster sincronizado"));
			entity.setIdCluster(cluster.getId());
			entity.setStatusCluster(Status.ATIVO);
		}
		
		update(false);
	}
	
	@Transactional
	public void criarCluster() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			service.createClusterSolr(entity.getNomeCluster(), entity.getUnitCluster(), entity.getNomeConfig(), entity.getNomeCollection());
			SolrCluster cluster = service.sincronizarConfiguracao(entity.getNomeCluster());
			context.addMessage("sinchronizedCluster", new FacesMessage("Cluster sincronizado"));
			entity.setIdCluster(cluster.getId());
			
			entity.setStatusCluster(Status.ATIVO);
			update();
		} catch(Exception e) {
			context.addMessage("sinchronizedCluster", new FacesMessage(e.getMessage()));
		}
	}
	
	@Transactional
	public void apagarCluster() {
		FacesContext context = FacesContext.getCurrentInstance();
		service.deleteClusterSolr(entity.getIdCluster());
		SolrCluster cluster = service.sincronizarConfiguracao(entity.getNomeCluster());
		
		if(cluster == null) {
			entity.setIdCluster(null);
			entity.setStatusCluster(Status.INATIVO);
			update(false);
			
			context.addMessage("sinchronizedCluster", new FacesMessage("Cluster deletado com sucesso"));
		} else {
			context.addMessage("sinchronizedCluster", new FacesMessage("Não foi possível deletar o Cluster"));
		}
		
	}
	
	public void atualizarConfiguracao() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			if(service.existClusterConfig(entity.getIdCluster(), entity.getNomeConfig())) {
				service.uploadConfiguration(entity.getIdCluster(), entity.getNomeConfig());
				context.addMessage("sinchronizedCluster", new FacesMessage("Atualização feita com sucesso"));
			} else { 
				context.addMessage("sinchronizedCluster", new FacesMessage("Não pode ser a atualização, cluster não existente"));
			}
		} catch(Exception e) {
			context.addMessage("sinchronizedCluster", new FacesMessage(e.getMessage()));
		}
	}
	
	private void validarRegistro() throws ApplicationException {
		try {
			ClusterSolrDAO clusterSolrDAO = new ClusterSolrDAO(entityManager);
			ClusterSolr clusterSolr = clusterSolrDAO.findByNome(entity.getNomeCluster());
			
			if(clusterSolr != null && !entity.equals(clusterSolr)) {
				throw new ApplicationException("Já existe um registro com esse nome de cluster: " + entity.getNomeCluster());
			}
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(), e);
		}
	}
}