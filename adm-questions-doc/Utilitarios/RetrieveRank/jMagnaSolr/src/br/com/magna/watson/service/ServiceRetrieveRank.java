package br.com.magna.watson.service;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.common.SolrInputDocument;

import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusterOptions;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusters;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrConfigs;

public class ServiceRetrieveRank {
    
    private RetrieveAndRank service;
    
    private Integer clusterUnitSolr = null;
    private String idClusterSolr = "";
    private String nomeCluster = "MAGNA_RH_SOLR";
    private String nomeConfig = "CONF_RH";
    private String nomeCollection = "COLLEC_RH_FERIAS";
    
    private String loginRR = "";
    private String passwordRR = "";
    
    public ServiceRetrieveRank(String usuario, String senha){
        loginRR = usuario;
        passwordRR = senha;
    }
    
    public void createClusterSolr(ServiceDocumentConversion serviceDocumentConversion) throws Exception{
		
        try{
            // Recupera o identificador do cluster (se j√° foi criado)
            service = new RetrieveAndRank();
            service.setUsernameAndPassword(loginRR, passwordRR);
            
            System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Consultando se cluster j· foi criado.");
            getClusterSolr();

            // se o identificador for nulo
            // √© executado a rotina de cria√ß√£o da rotina do cluster
            if(idClusterSolr == null){
                
                createCluster(); // cria cluster
                uploadConfiguration(); // configura√ß√£o (arquivos xml)
                createCollection(); // cria√ß√£o da cole√ß√£o
                indexDocumentAndCommit(serviceDocumentConversion); // indexa os documentos
                
            }else{
            	System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Cluster j· criado: ["+ idClusterSolr +"]");
            	throw new Exception("Cluster j· criado: ["+ idClusterSolr +"]");
            }

        }catch(Exception e){
               throw new Exception(e);
        }

    }
    
    private void uploadConfiguration() throws Exception{
        File dirConfigZip = new File("C:\\Solr\\watson\\solrconfig.zip");
        service.uploadSolrClusterConfigurationZip(idClusterSolr, nomeConfig, dirConfigZip).execute();
        
        System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Configurando ["+ nomeConfig +"].");
        SolrConfigs conf = service.getSolrClusterConfigurations(idClusterSolr).execute();
        if(conf.getSolrConfigs().isEmpty()){
        	System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": ConfiguraÁ„o ["+ nomeConfig +"] falhou.");
        	throw new IllegalStateException("Falha ao configurar "+ nomeConfig);
        }
        
        System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": ConfiguraÁ„o ["+ nomeConfig +"] com sucesso.");
    }
	
    private void getClusterSolr() {

        SolrClusters listaClusterSolr = service.getSolrClusters().execute();
        if(listaClusterSolr.getSolrClusters().size() > 0)
        {
        	idClusterSolr = listaClusterSolr.getSolrClusters().get(0).getId();
        }
        else
        {
            idClusterSolr = null;
        }
    }
	
    private SolrCluster getSolrCluster(String idCluster) {
        return service.getSolrCluster(idCluster).execute();
    }

    private void createCluster() {
        
        SolrClusterOptions optionCluster = null;

        // Se a unit for null ele n√£o definir√° a unit size do cluster
        /// Com isso o cluster fica limitado a 500mb para testes
        if(clusterUnitSolr == null)
        {
        	System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Criando CLuster sem unidade.");
        	optionCluster = new SolrClusterOptions(nomeCluster);
        }
        else
        {
        	System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Criando CLuster com unidade ["+ clusterUnitSolr +"].");
            optionCluster = new SolrClusterOptions(nomeCluster,clusterUnitSolr);
        }

        // Cria√ß√£o do cluster
        SolrCluster cluster = service.createSolrCluster(optionCluster).execute();

        idClusterSolr = cluster.getId();
        System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Criou cluster Id ["+ idClusterSolr +"].");
        while (cluster.getStatus() == SolrCluster.Status.NOT_AVAILABLE) {

           try{
        	   System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Consultando status do cluster ["+ idClusterSolr +"]."); 
        	   
        	   Thread.sleep(30000);
                cluster = getSolrCluster(cluster.getId());
           }
           catch(InterruptedException e){
        	   System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Deu erro na consulta Id Cluster ["+ idClusterSolr +"]."); 
        	   
        	   idClusterSolr = null;
           }
        }

    }

    //TODO trocar os metodos deprecation por um em opera√ß√£o
    @SuppressWarnings("deprecation")
    private void createCollection() throws Exception {
        // Cria√ß√£o da collection
        /*final CollectionAdminRequest.Create createCollectionRequest = CollectionAdminRequest.createCollection(nomeColection, nomeConfig, 1, 1);*/

    	System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Criando collection ["+ nomeCollection +"].");
        final CollectionAdminRequest.Create createCollectionRequest = new CollectionAdminRequest.Create();
        createCollectionRequest.setCollectionName(nomeCollection);
        createCollectionRequest.setConfigName(nomeConfig);

        final CollectionAdminResponse response = createCollectionRequest.process(getSolrClient()); // Executa a processo de cria√ß√£o da cole√ß√£o
        if (!response.isSuccess()) {
        	System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": CriaÁ„o da collection ["+ nomeCollection +"] falhou.");
        	throw new IllegalStateException("Falha ao criar collection: "+ response.getErrorMessages().toString());
        }
        System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": CriaÁ„o da collection ["+ nomeCollection +"] sucesso.");
    }
	
    private HttpSolrClient getSolrClient() {
    	System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Criando inst‚ncia do Solr Client. Passo 1: ok");
    	HttpSolrClient.Builder builderHttpSolrClient = new HttpSolrClient.Builder(service.getSolrUrl(idClusterSolr));
    	System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Criando inst‚ncia do Solr Client. Passo 2: ok");
        builderHttpSolrClient.withHttpClient(createHttpClient());
        System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Criando inst‚ncia do Solr Client. Passo 3: ok");
        return builderHttpSolrClient.build();
    }

    private void indexDocumentAndCommit(ServiceDocumentConversion serviceDocumentConversion) throws Exception{
        // Inst√¢ncia do Solr
        HttpSolrClient solrCliente = getSolrClient();

        // Lista de documentos
        System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Pegando documento convertido de HTML para JSON.");
        Collection<SolrInputDocument> listDocument = serviceDocumentConversion.getDadosDocumentConversion(); 

        // Avalia se tem documento a ser indexado
        if(listDocument.size() > 0){
        	System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Adicionando documento a collection ["+ nomeCollection +"].");
        	solrCliente.add(nomeCollection,listDocument);

                // Commit da cole√ß√£o 
            solrCliente.commit(nomeCollection);
            System.out.println(new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date())+": Sucesso ao adicionar documento a collection ["+ nomeCollection +"].");
        }
    }
    
    private HttpClient createHttpClient() {
        URI scopeUri = URI.create(service.getSolrUrl(idClusterSolr));

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(scopeUri.getHost(), scopeUri.getPort()),
                        new UsernamePasswordCredentials(loginRR, passwordRR));

        HttpClientBuilder builder = HttpClientBuilder.create().setMaxConnTotal(128).setMaxConnPerRoute(32)
                        .setDefaultRequestConfig(RequestConfig.copy(RequestConfig.DEFAULT).setRedirectsEnabled(true).build())
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .addInterceptorFirst(new PreemptiveAuthInterceptor());
        return builder.build();
    }
    
    private static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
        public void process(HttpRequest request, HttpContext context) throws HttpException {
            AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);

            if (authState.getAuthScheme() == null) {
                CredentialsProvider credsProvider = (CredentialsProvider) context
                                .getAttribute(HttpClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
                Credentials creds = credsProvider
                                .getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
                if (creds == null) {
                        throw new HttpException("No creds provided for preemptive auth.");
                }
                authState.update(new BasicScheme(), creds);
            }
        }
    }
}
