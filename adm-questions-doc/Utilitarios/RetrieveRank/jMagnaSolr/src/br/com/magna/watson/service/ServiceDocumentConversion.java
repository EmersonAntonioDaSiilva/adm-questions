/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.magna.watson.service;

import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;
import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author Cameag
 */
public class ServiceDocumentConversion {
    
    private DocumentConversion service;
    
    public ServiceDocumentConversion(String usuario, String senha){
        
        service = new DocumentConversion(DocumentConversion.VERSION_DATE_2015_12_01);
        service.setUsernameAndPassword(usuario, senha);
    
    }
    
    public Collection<SolrInputDocument> getDadosDocumentConversion(){
        Collection<SolrInputDocument> retorno = Collections.emptyList();

        File arquivos[];
        File diretorio = new File("c:\\Solr\\Watson\\arquivos\\");
        arquivos = diretorio.listFiles();

        if(arquivos.length > 0){

            retorno = new ArrayList<SolrInputDocument>();

            for(int i = 0; i < arquivos.length; i++){
                // TODO Colocar as regras de validação para os documentos
                //String mediaTypeFromFile = ConversionUtils.getMediaTypeFromFile(arquivos[i]);
                Answers execute = service.convertDocumentToAnswer(arquivos[i]).execute();

                SolrInputDocument document = new SolrInputDocument();
                document.addField("id", execute.getAnswerUnits().get(0).getId());
                document.addField("title", execute.getAnswerUnits().get(0).getTitle());
                document.addField("body", execute.getAnswerUnits().get(0).getContent().get(0).getText());

                retorno.add(document);
            }
        }

        return retorno;
    }
}
