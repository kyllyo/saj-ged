package br.gov.serpro.saj.ged.business;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;

import br.gov.serpro.saj.ged.exception.NegocioException;
import br.gov.serpro.saj.ged.model.AnexoManifestacao;
import br.gov.serpro.saj.ged.model.Documento;
import br.gov.serpro.saj.ged.model.Manifestacao;
import br.gov.serpro.saj.ged.model.Peca;
import br.gov.serpro.saj.ged.model.PecaIntegra;
import br.gov.serpro.saj.ged.model.ReciboManifestacao;
import br.gov.serpro.saj.ged.persistence.DocumentoSajDatabase;
import br.gov.serpro.saj.ged.persistence.FileRepository;

@Stateless
public class DocumentoSajBC {

	@Inject
	private DocumentoSajDatabase databaseRepository;

	@Inject
	private FileRepository fileRepository;

	
	public Long incluirManifestacao(Long idManifestacao, Long idObjetoTramitacao, Long tipoArquivo, 
			InputStream arquivoStream) throws NegocioException {
		
		Manifestacao manifestacao = new Manifestacao();
		manifestacao.setIdManifestacao(idManifestacao);
		manifestacao.setIdObjetoTramitacao(idObjetoTramitacao);
		
		Documento doc = createDocumento(tipoArquivo, null, 1L);
		
		if(validaManifestacao(manifestacao) && validaDocumento(doc)) {

			databaseRepository.insert(doc);
			manifestacao.setId(doc.getId());
			databaseRepository.insert(manifestacao);
			try {
				fileRepository.gravar(arquivoStream, doc.getCaminhoArquivo());
			} catch (IOException e) {
				throw new NegocioException(e);			
			}
		}
		
		return manifestacao.getId();
	}

	
	public Long incluirReciboManifestacao(Long idManifestacao, Long tipoArquivo,
			InputStream arquivoStream) throws NegocioException {
		
		ReciboManifestacao recibo = new ReciboManifestacao();		
		recibo.setIdManifestacao(idManifestacao);
		
		Documento doc = createDocumento(tipoArquivo, null, 4L);
		
		if(validaReciboManifestacao(recibo) && validaDocumento(doc)) {

			databaseRepository.insert(doc);
			recibo.setId(doc.getId());
			databaseRepository.insert(recibo);
			try {
				fileRepository.gravar(arquivoStream, doc.getCaminhoArquivo());
			} catch (IOException e) {
				throw new NegocioException(e);			
			}
		}
		
		return recibo.getId();
	}
	
	public Long incluirAnexoManifestacao(Long idAnexo, Long idManifestacao, Long tipoArquivo, 
			InputStream arquivoStream) throws NegocioException {
		
		AnexoManifestacao anexo = new AnexoManifestacao();
		anexo.setIdAnexo(idAnexo);
		anexo.setIdManifestacao(idManifestacao);
		
		Documento doc = createDocumento(tipoArquivo, null, 2L);
		
		if(validaAnexoManifestacao(anexo) && validaDocumento(doc)) {

			databaseRepository.insert(doc);
			anexo.setId(doc.getId());
			databaseRepository.insert(anexo);
			try {
				fileRepository.gravar(arquivoStream, doc.getCaminhoArquivo());
			} catch (IOException e) {
				throw new NegocioException(e);			
			}
		}
		
		return anexo.getId();
	}
	
	public Long incluirDocumento(Long tipoArquivo, String descricaoTipoDocumento, InputStream arquivoStream) throws NegocioException {
		return incluirDocumento(tipoArquivo, descricaoTipoDocumento, null, arquivoStream);
	}

	public Long incluirDocumento(Long tipoArquivo, String descricaoTipoDocumento, Long tipoDocumento, InputStream arquivoStream) throws NegocioException {

		Documento doc = createDocumento(tipoArquivo, descricaoTipoDocumento, tipoDocumento);
		
		if(validaDocumento(doc)) {
			databaseRepository.insert(doc);		
			try {
				fileRepository.gravar(arquivoStream, doc.getCaminhoArquivo());
			} catch (IOException e) {
				throw new NegocioException(e);			
			}
		}
		
		return doc.getId();
	}
	
	public File consultar(Long id) throws NegocioException {
		Documento doc = buscarDocExistente(id);
		File file = new File(doc.getCaminhoArquivo());
		if (!file.canRead()) {
			throw new NegocioException("Arquivo nao existe mo disco:" + doc.getCaminhoArquivo());
		}
		return file;
	}
	
	public File obterManifestacaoAnexosv2(Long id) throws NegocioException {
		ObjetoMerge objMerge = obterManifestacaoAnexos(id);
		File fileZip = new File(fileRepository.gerarCaminhoArquivoTemp());
		try {
			File file = null;
			if(objMerge.getListaCorrompidos() != null && objMerge.getListaCorrompidos().size() > 0){
				file = new File(fileRepository.gerarCaminhoArquivoTemp());
				FileWriter fw = new FileWriter(file);
				for(String corrompido: objMerge.getListaCorrompidos()){
					fw.write(corrompido+"\n");
				}
				fw.close();
			}
			
			FileOutputStream fous = new FileOutputStream(fileZip);
			ZipOutputStream outZip = new ZipOutputStream(fous);
			
			writeToZipFile(objMerge.getFile().getAbsolutePath(), outZip);
			if(file != null){
				writeToZipFile(file.getAbsolutePath(), outZip);
				Files.deleteIfExists(file.toPath());
			}
			Files.deleteIfExists(objMerge.getFile().toPath());
			
			outZip.close();
			fous.close();
			
		} catch (IOException e) {
			throw new NegocioException(e.getMessage());
		}
		
		return fileZip;
		
	}
	
	/**
     * Add a file into Zip archive in Java.
     * 
     * @param fileName
     * @param zos
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void writeToZipFile(String path, ZipOutputStream zipStream)
            throws FileNotFoundException, IOException {

        File aFile = new File(path);
        FileInputStream fis = new FileInputStream(aFile);
        ZipEntry zipEntry = new ZipEntry(path);
        zipStream.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipStream.write(bytes, 0, length);
        }

        zipStream.closeEntry();
        fis.close();
    }


	
	public ObjetoMerge obterManifestacaoAnexos(Long id) throws NegocioException {
		
		ObjetoMerge objMerge = new ObjetoMerge();
		
		try {
			List<Manifestacao> manifestacoes = databaseRepository.findManifestacoes(id); 
			List<AnexoManifestacao> anexos = databaseRepository.findAnexosPorManifestacao(id);
		
			if(manifestacoes != null && manifestacoes.size() > 0){
				Document document = new Document();
				String nomeArq = fileRepository.gerarCaminhoArquivoTemp();
				String nomeArq2 = nomeArq+"2";
				File fileSemNumeracao = new File(nomeArq);
				File fileComNumeracao = new File(nomeArq2);
				PdfCopy mergeado = new PdfSmartCopy(document, new FileOutputStream(fileSemNumeracao.getAbsolutePath()));
		
				document.open();
		
				for(Manifestacao manifestacao: manifestacoes) {
					if(!adicionarArquivoAoDocumentoMergeado(mergeado, manifestacao.getDocumento())){
						objMerge.addListaCorrompidos(new Long(manifestacao.getId()).toString());
					}
				}
				if(anexos != null && anexos.size() > 0){
					for(AnexoManifestacao anexo: anexos){
						if(!adicionarArquivoAoDocumentoMergeado(mergeado, anexo.getDocumento())){
							objMerge.addListaCorrompidos(new Long(anexo.getId()).toString());
						}
					}
				}
				
				try{
					document.close();
				}catch(Exception e){
					Files.deleteIfExists(fileSemNumeracao.toPath());
					Files.deleteIfExists(fileComNumeracao.toPath());
					throw new NegocioException("Não foi possivel gerar o arquivo !");
				}
				
				numerarPaginasPdf(nomeArq, nomeArq2);
				objMerge.setFile(fileComNumeracao);
				Files.deleteIfExists(fileSemNumeracao.toPath());
			}else{
				throw new NegocioException("Nenhuma manifestacao encontrada");
			}
		
		} catch (FileNotFoundException e) {
			throw new NegocioException(e.getMessage());			
		} catch (DocumentException e) {
			throw new NegocioException(e.getMessage());
		} catch (IOException e) {
			throw new NegocioException(e.getMessage());
		}
		
		return objMerge;
		
	}
	
	public File obterPecasProcesso(Long id) throws NegocioException {
		
		try {
			List<Peca> pecas = databaseRepository.findByProcesso(id); 
		
			if(pecas != null && pecas.size() > 0){
				Document document = new Document();
				String nomeArq = fileRepository.gerarCaminhoArquivoTemp();
				String nomeArq2 = nomeArq+"2";
				File fileSemNumeracao = new File(nomeArq);
				File fileComNumeracao = new File(nomeArq2);
				PdfCopy mergeado = new PdfSmartCopy(document, new FileOutputStream(fileSemNumeracao.getAbsolutePath()));
		
				document.open();
		
				for(Peca peca: pecas) {
					adicionarArquivoAoDocumentoMergeado(mergeado, peca.getDocumento());
				}
				try{
					document.close();
				}catch(Exception e){
					Files.deleteIfExists(fileSemNumeracao.toPath());
					Files.deleteIfExists(fileComNumeracao.toPath());
					throw new NegocioException("Não foi possivel gerar o arquivo !");
				}
				
				numerarPaginasPdf(nomeArq, nomeArq2);
				Files.deleteIfExists(fileSemNumeracao.toPath());
			
				return fileComNumeracao;
			}else{
				throw new NegocioException("Nenhuma peca encontrada para o processo informado");
			}
		
		} catch (FileNotFoundException e) {
			throw new NegocioException(e.getMessage());			
		} catch (DocumentException e) {
			throw new NegocioException(e.getMessage());
		} catch (IOException e) {
			throw new NegocioException(e.getMessage());
		} catch(Exception e){
			throw new NegocioException(e.getMessage());
		}
	
	}
	
	/**
	 * Se o tipo de extensao eh passivel de ser convertido
	 * pelo algoritmo de conversao de imagem em pdf
	 * @param tipo Long
	 * @return boolean
	 */
	private boolean ehFigura(Long tipo){
		if(	tipo.longValue() == 2L 
			|| tipo.longValue() == 11L
			|| tipo.longValue() == 12L
			|| tipo.longValue() == 13L
			|| tipo.longValue() == 14L
			|| tipo.longValue() == 40L
			|| tipo.longValue() == 41L
			|| tipo.longValue() == 42L				
			){			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Se o tipo de extensao eh passivel de ser convertido
	 * pelo algoritmo de conversao de html em pdf
	 * @param tipo Long
	 * @return boolean
	 */
	private boolean ehHtml(Long tipo){
		if(	tipo.longValue() == 8L 
			|| tipo.longValue() == 15L
			){			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Se o tipo de extensao eh passivel de ser convertido
	 * pelo algoritmo de conversao de txt em pdf
	 * @param tipo Long
	 * @return boolean
	 */
	private boolean ehTxt(Long tipo){
		if(	tipo.longValue() == 1L 
			|| tipo.longValue() == 37L
			){			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Se o tipo de extensao eh passivel de ser convertido
	 * pelo algoritmo de conversao de word em pdf
	 * @param tipo Long
	 * @return boolean
	 */
	private boolean ehWord(Long tipo){
		if(	tipo.longValue() == 3L
			|| tipo.longValue() == 4L
			|| tipo.longValue() == 5L
			|| tipo.longValue() == 6L
			|| tipo.longValue() == 10L
			|| tipo.longValue() == 16L
			|| tipo.longValue() == 17L
			|| tipo.longValue() == 19L
			|| tipo.longValue() == 31L
			|| tipo.longValue() == 33L
			){			
			return true;
		}
		
		return false;
	}
	
	
	

	/**
	 * Adicionar arquivo do Path ao documento mergeado.
	 * @param mergeado PdfCopy
	 * @param path Path
	 * @param tipo Long[]
	 * Tipos Possiveis: 1L - txt / 2L - img / 3L - doc / 4L - xls / 5L - pps / 6L - rtf / 7L - pdf / 8L - html / 10L - odt
	 * 11L - gif / 12L - jpg / 13L - tif / 14 - bmp / 15 - htm / 16 - ods / 17 - odp / 19 - ppt 
	 * 31 - docx / 33 - xlsx / 37 - csv / 40 - jpeg / 41 - png / 42 - tiff / 
	 * @return boolean - true, se deu certo.
	 */
    private boolean adicionarArquivoAoDocumentoMergeado(PdfCopy mergeado, Documento documento){
    	
    	boolean retorno = false;
    	try{
        	String path = documento.getCaminhoArquivo();
        	Long tipo = documento.getTipoArquivo();
    		File arqFisico = new File(path);
    		if(arqFisico.exists()){    			
    			byte[] dados = Files.readAllBytes(arqFisico.toPath());
    			if(tipo != null){
    				if(ehFigura(tipo)){
    					dados = ConverterPDFUtil.tiffParaPdf(dados);
    				}else if(ehHtml(tipo)){
    					dados = ConverterPDFUtil.htmlParaPdf(dados);
    				}else if(ehWord(tipo)){
    					dados = ConverterPDFUtil.wordParaPdf(fileRepository.gerarCaminhoArquivoTemp() ,dados,"doc");
    				}else if(ehTxt(tipo)){
    					dados = ConverterPDFUtil.txtParaPdf(fileRepository.gerarCaminhoArquivoTemp(), dados);
    				}    			
    			}
		    		
				PdfReader arqPdf = new PdfReader(dados);			
				mergeado.addDocument(arqPdf);
				arqPdf.close();	    	
		    	retorno = true;
    		}
    	}catch(IOException er){
    		er.printStackTrace();
    	}catch(RuntimeException er){
    		er.printStackTrace();
    	}catch(Exception er){
    		er.printStackTrace();
    	}
    	return retorno;
    }

    private void numerarPaginasPdf(String src, String dest) throws IOException, DocumentException{
    	
	    	PdfReader reader = new PdfReader(src);
	    	int n = reader.getNumberOfPages();
	    	PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
	    	PdfContentByte pagecontent;
	    	for (int i = 0; i < n; ) {
	    	    pagecontent = stamper.getOverContent(++i);
	    	    ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT,
	    	            new Phrase(String.format("page %s of %s", i, n)), 559, 806, 0);
	    	}
	    	stamper.close();
	    	reader.close();
    	
    }
    
	
	
	/**
	 * Exclui o arquivo e o registro no banco de um Documento.
	 * @param id Long
	 * @return boolean
	 * @throws NegocioException
	 */
	public boolean excluir(Long id) throws NegocioException {
		Documento doc = buscarDocExistente(id);
		try {
			ReciboManifestacao recibo = databaseRepository.find(ReciboManifestacao.class,id);
			AnexoManifestacao anexo = databaseRepository.find(AnexoManifestacao.class,id);
			Manifestacao manifestacao = databaseRepository.find(Manifestacao.class,id);
			if(recibo != null)
				databaseRepository.remove(recibo);
			if(anexo != null)
				databaseRepository.remove(anexo);
			if(manifestacao != null)
				databaseRepository.remove(manifestacao);
			
			if(doc != null){
				databaseRepository.remove(doc);
				fileRepository.excluir(doc.getCaminhoArquivo());
			}
		}catch(Exception er) {
			throw new NegocioException("Operacao não realizada "+er.getMessage());
		}
		
		return true;
	}
	
	
	
	private Documento buscarDocExistente(Long id) throws NegocioException {
		if(id == null || id.longValue() <= 0) {
			throw new NegocioException("Id inválido.");
		}
		Documento doc = databaseRepository.find(Documento.class, id);
		if(doc == null || doc.getId() == null) {
			throw new NegocioException("Id inexistente na base.");
		}
		return doc;
	}
	
	private boolean validaDocumento(Documento doc) throws NegocioException {
		String error = "";
		if(doc.getTipoDocumento() == null || doc.getTipoDocumento().intValue() <= 0) {
			error += " | Tipo de Documento não preenchido ";			
		}
		if(doc.getCaminhoArquivo() == null || doc.getCaminhoArquivo().isEmpty()) {
			error += " | Caminho do arquivo não preenchido ";
		}
		
		
		if(error.isEmpty()) {
			return true;
		}		
		throw new NegocioException(error);
	}
	
	private boolean validaManifestacao(Manifestacao manifestacao) throws NegocioException {
		String error = "";
		if(manifestacao.getIdObjetoTramitacao() == null || manifestacao.getIdObjetoTramitacao().longValue() <= 0) {
			error += " | Código do objeto de tramitacao não informado ";
		}
		if(manifestacao.getIdManifestacao() == null || manifestacao.getIdManifestacao().longValue() <= 0) {
			error +=" | Código da manifestação não informado ";
		}
		
		
		if(error.isEmpty()) {
			return true;
		}		
		throw new NegocioException(error);
	}
	
	
	private boolean validaAnexoManifestacao(AnexoManifestacao anexo) throws NegocioException {
		String error = "";
		if(anexo.getIdAnexo() == null || anexo.getIdAnexo().longValue() <= 0) {
			error += " | Código do anexo da manifestacao nao informado ";
		}
		if(anexo.getIdManifestacao() == null || anexo.getIdManifestacao().longValue() <= 0) {
			error +=" | Código da manifestação não informado ";
		}
		
		
		if(error.isEmpty()) {
			return true;
		}		
		throw new NegocioException(error);
	}
	
	private boolean validaReciboManifestacao(ReciboManifestacao recibo) throws NegocioException {
		String error = "";
		if(recibo.getIdManifestacao() == null || recibo.getIdManifestacao().longValue() <= 0) {
			error +=" | Código da manifestação não informado ";
		}
		
		if(error.isEmpty()) {
			return true;
		}		
		throw new NegocioException(error);
	}


	private Documento createDocumento(Long tipoArquivo, String descricaoTipoDocumento, Long tipoDocumento){

		Documento doc = new Documento();
		
		doc.setCaminhoArquivo(fileRepository.gerarCaminhoArquivo());
		doc.setTipoArquivo(tipoArquivo);
		doc.setTipoDocumento(tipoDocumento != null ? tipoDocumento : databaseRepository.findTipoDocByDescricao(descricaoTipoDocumento));
		doc.setDtCriacao(Calendar.getInstance().getTime());
		
		return doc;
	}




	

}
