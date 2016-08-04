package br.com.afirmanet.questions.manager.application.additional;

import java.io.Serializable;

import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class ClusterSolrManager extends Watson implements Serializable {
	private static final long serialVersionUID = 3925507169074921562L;

	private static String nomeCluster = "MAGNA-RH-TESTE",
						  nomeConfig = "CONFIG-MAGNA-RH-TESTE",
			 			  nomeColection = "COLLECTION-MAGNA-RH-TESTE"; 
	
	@Override
	protected void inicializar() {}
	
	
	public void btGeraCluster(){
		
		try{
			String idCluster = createCluster(nomeCluster, null);
			uploadConfiguration(idCluster, nomeConfig);
			createCollection(idCluster, nomeConfig, nomeColection);
			indexDocumentAndCommit(idCluster, nomeColection);
		}
		catch(Exception e){
			
		}
	}
	
}