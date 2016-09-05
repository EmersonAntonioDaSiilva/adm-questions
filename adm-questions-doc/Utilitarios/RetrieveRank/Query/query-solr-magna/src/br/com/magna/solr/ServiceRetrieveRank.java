/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.magna.solr;

import java.net.URI;

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

import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusters;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;

public class ServiceRetrieveRank {
    
    private RetrieveAndRank service;
    
    public ServiceRetrieveRank(){
        service = new RetrieveAndRank();
    }
    
    public String getClusterSolr(String loginRR, String passwordRR) {
	String idClusterSolr = "";
        
        service.setUsernameAndPassword(loginRR, passwordRR);
        SolrClusters listaClusterSolr = service.getSolrClusters().execute();
        if(listaClusterSolr.getSolrClusters().size() > 0)
        {
            idClusterSolr = listaClusterSolr.getSolrClusters().get(0).getId();
        }
        
        return idClusterSolr;
    }
    
    public CollectionAdminResponse getCollections(String loginRR, String passwordRR,
                                                                    String idClusterSolr) throws Exception{
        
        CollectionAdminResponse response;
        CollectionAdminRequest.List createCollectionRequest;
        
        try{
            createCollectionRequest = new CollectionAdminRequest.List();
            response = createCollectionRequest.process(getSolrClient(loginRR, passwordRR, idClusterSolr));
        }catch(Exception e){
            throw new Exception("Erro durante a pesquisa no Solr.");
        }
        
        return response;
    }
    
    public QueryResponse searchAllDocs(String loginRR, String passwordRR, 
                        String idClusterSolr, String nomeCollection, String pergunta) throws Exception{
        
        service.setUsernameAndPassword(loginRR, passwordRR);
        
        QueryResponse response;

        try {
            HttpSolrClient solrClient = getSolrClient(loginRR, passwordRR, idClusterSolr);
            
            String pesquisa = "".concat(pergunta); // monta String da pesquisa
            SolrQuery query = new SolrQuery(pesquisa); // cria os critérios da pesquisa

            response = solrClient.query(nomeCollection, query); // retorna pesquisa
            
            
            CollectionAdminRequest.listCollections();

        } catch (Exception e) {
            throw new Exception("Erro durante a pesquisa no Solr.");
        }

        return  response;
    }
	
    private HttpSolrClient getSolrClient(String loginRR, String passwordRR, String idClusterSolr) {
    	HttpSolrClient.Builder builderHttpSolrClient = new HttpSolrClient.Builder(service.getSolrUrl(idClusterSolr));
        builderHttpSolrClient.withHttpClient(createHttpClient(loginRR, passwordRR, idClusterSolr));
        return builderHttpSolrClient.build();
    }
    
    private HttpClient createHttpClient(String loginRR, String passwordRR, String idClusterSolr) {
        URI scopeUri = URI.create(service.getSolrUrl(idClusterSolr));

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(scopeUri.getHost(), scopeUri.getPort()),
                        new UsernamePasswordCredentials(loginRR, passwordRR));

        HttpClientBuilder builder = HttpClientBuilder.create().setMaxConnTotal(128).setMaxConnPerRoute(32)
                        .setDefaultRequestConfig(RequestConfig.copy(RequestConfig.DEFAULT)
                                .setRedirectsEnabled(true).build())
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
                    throw new HttpException("Credenciais informadas não previstas para autenticação preemptiva.");
                }
                authState.update(new BasicScheme(), creds);
            }
        }
    }
}