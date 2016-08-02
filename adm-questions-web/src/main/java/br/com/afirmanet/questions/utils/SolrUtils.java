package br.com.afirmanet.questions.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.google.gson.JsonObject;

import br.com.afirmanet.questions.manager.vo.SolrResult;

public class SolrUtils {
	  private static final String TITLE = "title";
	  private static final String ID = "id";
	  private static final String BODY = "body";
	  private static String FCSELECT_REQUEST_HANDLER = "/fcselect";
	  private static String FEATURE_VECTOR_FIELD = "featureVector";
	  private static String FIELD_LIST_PARAM = "fl";
	  private static String ID_FIELD = ID;
	  private static String RANKER_ID = "ranker_id";
	  private static String SCORE_FIELD = "score";
	  private String collectionName;
	  private String rankerId;
	  private HttpSolrClient solrClient;

	  /**
	   *
	   * @param solrClient the Solr client
	   * @param groundTruth the ground truth
	   * @param collectionName the collection name
	   * @param rankerID the ranker id
	   */
	  public SolrUtils(HttpSolrClient solrClient, JsonObject groundTruth, String collectionName,
	      String rankerID) {
	    this.rankerId = rankerID;
	    this.collectionName = collectionName;
	    this.solrClient = solrClient;
	  }

	  /**
	   * Process a Solr request up to 3 times.
	   *
	   * @param request the request
	   * @return the query response
	   * @throws IOException Signals that an I/O exception has occurred.
	   * @throws SolrServerException the Solr server exception
	   * @throws InterruptedException the interrupted exception
	   */
	  private QueryResponse processSolrRequest(QueryRequest request)
	      throws IOException, SolrServerException, InterruptedException {
	    int currentAttempt = 0;
	    QueryResponse response;
	    while (true) {
	      try {
	        currentAttempt++;
	        response = request.process(solrClient, collectionName);
	        break;
	      } catch (Exception e) {
	        if (currentAttempt < 3) {
	          Thread.sleep(1000);
	        } else {
	          throw e;
	        }
	      }
	    }
	    return response;
	  }

	  public SolrDocument search(String query, boolean useRanker) {
		  SolrDocument doc = null;

		  try {
			QueryResponse queryResponse = solrRuntimeQuery(query, useRanker);

			  Iterator<SolrDocument> it = queryResponse.getResults().iterator();
			  while (it.hasNext()) {
			    doc = it.next();
			    break;
			  }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	    return doc;
	  }


	  /**
	   * Create and process a Solr query
	   *
	   * @param query the query
	   * @param featureVector the feature vector
	   * @return the query response
	   * @throws IOException Signals that an I/O exception has occurred.
	   * @throws SolrServerException the Solr server exception
	   * @throws InterruptedException the interrupted exception
	   */
	  private QueryResponse solrRuntimeQuery(String query, boolean featureVector)
	      throws IOException, SolrServerException, InterruptedException {
	    SolrQuery featureSolrQuery = new SolrQuery(query);
	    if (featureVector) {
	      featureSolrQuery.setRequestHandler(FCSELECT_REQUEST_HANDLER);
	      // add the ranker id - this tells the plugin to re-reank the results in a single pass
	      featureSolrQuery.setParam(RANKER_ID, rankerId);

	    }

	    // bring back the id, score, and featureVector for the feature query
	    featureSolrQuery.setParam(FIELD_LIST_PARAM, ID_FIELD, SCORE_FIELD, FEATURE_VECTOR_FIELD);

	    // need to ask for enough rows to ensure the correct answer is included in the resultset
	    featureSolrQuery.setRows(1000);
	    QueryRequest featureRequest = new QueryRequest(featureSolrQuery, METHOD.POST);

	    return processSolrRequest(featureRequest);
	  }

	  /**
	   * Gets the documents by ids.
	   *
	   * @param idsToRetrieve the ids of documents to retrieve
	   * @return the documents
	   */
	  public Map<String, SolrResult> getDocumentsByIds(ArrayList<String> idsToRetrieve) {
	    SolrDocumentList docs;
	    Map<String, SolrResult> idsToDocs = new HashMap<>();
	    try {
	      
	      docs = solrClient.getById(collectionName, idsToRetrieve, new ModifiableSolrParams());

	      for (SolrDocument doc : docs) {
	        SolrResult result = new SolrResult();
	        result.setBody(doc.getFirstValue(BODY).toString().replaceAll("\\s+", " ").trim());
	        result.setId(doc.getFirstValue(ID).toString());
	        result.setTitle(doc.getFirstValue(TITLE).toString().replaceAll("\\s+", " ").trim());
	        idsToDocs.put(result.getId(), result);
	      }
	    } catch (IOException | SolrServerException e) {
	      //log(Level.SEVERE, "Error retrieven the Solr documents", e);
	    }
	    return idsToDocs;
	  }
}
