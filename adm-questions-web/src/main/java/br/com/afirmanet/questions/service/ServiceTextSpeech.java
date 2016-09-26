package br.com.afirmanet.questions.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.persistence.EntityManager;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

import br.com.afirmanet.core.exception.ApplicationException;
import br.com.afirmanet.questions.entity.Cliente;
import br.com.afirmanet.questions.enums.TypeServicoEnum;
import br.com.afirmanet.questions.factory.WatsonServiceFactory;
import br.com.afirmanet.questions.model.Voice_;
import lombok.Getter;

public class ServiceTextSpeech extends WatsonServiceFactory implements Serializable{

	private static final long serialVersionUID = 6879727087146281172L;

	@Getter
	private TextToSpeech service;
	
	public ServiceTextSpeech(Cliente cliente, EntityManager entityManager) throws ApplicationException{
		setEntityManager(entityManager); 
		
		setTypeServico(TypeServicoEnum.TEXT_TO_SPEECH);
		setCliente(cliente);
		
		service = getServiceTTS();
	}
	
	public void executa(String diretorio,String texto){
		try{
            InputStream stream = service.synthesize (texto, Voice_.PT_ISABELA, null);
            
            geraArquivoWav(diretorio,stream);
            
        } catch (Exception e) {}
		
    }
    
	private void geraArquivoWav(String diretorio,InputStream stream) throws Exception{
		
		// Arquivo a ser gravado 
		FileOutputStream arquivo = new FileOutputStream(getArquivo(diretorio));
		
		InputStream in = WaveUtils.reWriteWaveHeader(stream);
        
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
        	arquivo.write(buffer, 0, length);
        }
        arquivo.close();
        in.close();
        stream.close();
	}
	
	private String getArquivo(String diretorio) throws Exception{
		return diretorio + System.getProperty("file.separator") +"AAA-4673899229-0000-17272bdjjdnx568000.wav";
	}
}
