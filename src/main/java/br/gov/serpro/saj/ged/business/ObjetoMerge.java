package br.gov.serpro.saj.ged.business;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ObjetoMerge {

	private File file;
	
    protected byte[] documento;
	
    protected List<String> listaCorrompidos = new ArrayList<String>();

    public ObjetoMerge() {
    	
    }

    public byte[] getDocumento() {
        return documento;
    }

    public void setDocumento(byte[] value) {
        this.documento = ((byte[]) value);
    }

	public List<String> getListaCorrompidos() {
        return listaCorrompidos;
    }

    public void setListaCorrompidos(List<String> value) {
        this.listaCorrompidos = value;
    }
    
    public void addListaCorrompidos(String id){
        listaCorrompidos.add(id);
    }

	public File getFile() {
		return file;
	}

	public void setFile(File file) throws IOException {
		this.file = file;
		setDocumento(Files.readAllBytes(this.file.toPath()));
	}
	

}
