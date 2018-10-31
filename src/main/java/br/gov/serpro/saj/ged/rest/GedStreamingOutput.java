package br.gov.serpro.saj.ged.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;

import javax.ws.rs.core.StreamingOutput;


public class GedStreamingOutput implements StreamingOutput, Serializable {
	
	/**
	 * nuid
	 */
	private static final long serialVersionUID = 8079073296354278209L;
	
	private File file;
	private Boolean deleta = false;
	
	public GedStreamingOutput(File f){
		file  = f;
	}
	public GedStreamingOutput(File f, Boolean delete){
		file  = f;
		this.deleta = delete;
	}
	@Override
    public void write(OutputStream output) 
    		throws IOException{
		
        if(file != null && file.length() > 0){
        	int tamBloco = 4092; 

        	FileInputStream fileInput = new FileInputStream(file);
        	byte[] lidos = new byte[tamBloco];
        	while (fileInput.read(lidos, 0, tamBloco) != -1){
        		output.write(lidos);
        		output.flush();
        	}
        	fileInput.close();
        	output.close();

        	if(deleta){
            	Files.deleteIfExists(file.toPath());
            }
        }
        
    }

}
