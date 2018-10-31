package br.gov.serpro.saj.ged.persistence;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileRepository {

	//TODO String raiz = System.getProperty("saj.ged.pastaArquivos"); ou ambienteUtil.getPastaRaiz();
	private String raiz = "/opt/Arquivos/V2/"; 	
	
	private SimpleDateFormat pastaFormat = new SimpleDateFormat("yyyy/MM/dd/");	
	
	public String gerarCaminhoArquivo() {		
		
		String pasta = pastaFormat.format(new Date());		
		String nome = UUID.randomUUID().toString();		
		return raiz + pasta + nome;
	}
	
	public String gerarCaminhoArquivoTemp() {
		
		String nome = UUID.randomUUID().toString();		
		return raiz + nome;
	}

	
	public void gravar(InputStream arquivoStream, String caminhoArquivo) throws IOException {
		
		File arquivo = new File(caminhoArquivo);		
		File pasta = arquivo.getParentFile();		
		pasta.mkdirs();
		
		try (InputStream buffer = new BufferedInputStream(arquivoStream)) {
		
			Files.copy(
				buffer, 
				arquivo.toPath(), 
				StandardCopyOption.REPLACE_EXISTING);
		
		}
	}
	
	public void excluir(String caminhoArquivo) throws IOException {
		
		File file = new File(caminhoArquivo);
		if (!file.canRead()) {
			throw new IOException("Arquivo não pode ser lido/excluído no disco: " + caminhoArquivo);
		}
		Files.delete(file.toPath());
	}


}
