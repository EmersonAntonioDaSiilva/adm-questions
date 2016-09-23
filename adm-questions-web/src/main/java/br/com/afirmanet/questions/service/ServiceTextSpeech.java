package br.com.afirmanet.questions.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

import br.com.afirmanet.questions.model.Voice_;

public class ServiceTextSpeech implements Serializable{

	private static final long serialVersionUID = 6879727087146281172L;

	private TextToSpeech service;
	
	public ServiceTextSpeech(){
		service = new TextToSpeech();
	}
	
	public void executa(String diretorio,String texto){
		        
		try{
            service.setUsernameAndPassword("6e4786a4-738d-4e56-ba39-c5f6cbb99bad", "K8aMBqJSYsft");
            
            InputStream stream = service.synthesize (texto, Voice_.PT_ISABELA, null);
            
            geraArquivoWav(diretorio,stream);
            
        } catch (Exception e) {}
    }
    
	private void geraArquivoWav(String diretorio,InputStream stream) throws Exception{
		
		FileOutputStream arquivo = new FileOutputStream(diretorio +"\\AAA-4673899229-0000-17272bdjjdnx568000.wav");
		
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
}
