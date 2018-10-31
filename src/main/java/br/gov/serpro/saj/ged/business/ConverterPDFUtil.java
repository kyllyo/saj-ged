package br.gov.serpro.saj.ged.business;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.util.XRRuntimeException;
import org.xml.sax.SAXException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Jpeg;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;


/**
 * Utilitario para conversao de diferentes arquivos em PDF.
 * @author SUPDE/DEFLA/DE305
 */
public final class ConverterPDFUtil {

    /**
     * Construtor padrao privado.
     */
    private ConverterPDFUtil() {
        // NOP
    }

    /**
     * Converte imagem TIF em array de bytes de um documento PDF.
     * @param arquivo Array de bytes de arquivo TIF,
     * @return Array de bytes de arquivo PDF ou <code>null</code>.
     * @throws DocumentException - Caso ocorra erro na geracao do documento.
     * @throws IOException - Caso ocorra erro de IO.
     * @throws MalformedURLException - Caso ocorra erro de URL mal formada
     */
    public static byte[] tiffParaPdf(byte[] arquivo) throws NegocioException{

        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            Image imagem = Image.getInstance(arquivo);

            Document documento = new Document(new Rectangle(imagem.getWidth() + 60, imagem.getHeight() + 60));
            PdfWriter pdf = PdfWriter.getInstance(documento, saida);
            pdf.setStrictImageSequence(true);

            documento.open();
            documento.add(imagem);
            documento.close();

            saida.flush();

            return saida.toByteArray();

        } catch (BadElementException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());
        } catch (MalformedURLException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());
        } catch (IOException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());        	
        } catch (DocumentException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());        	
        }
    }

    /**
     * Converte imagem JPG/JPEG em array de bytes de um documento PDF.
     * @param arquivo Array de bytes de arquivo JPG/JPEG
     * @return Array de bytes de arquivo PDF ou <code>null</code>.
     * @throws DocumentException - Caso ocorra erro na geracao do documento.
     * @throws IOException - Caso ocorra erro de IO.
     * @throws MalformedURLException - Caso ocorra erro de URL mal formada
     */
    public static byte[] jpegParaPdf(byte[] arquivo) throws NegocioException {

        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();

            Document documento = new Document();
            PdfWriter pdf = PdfWriter.getInstance(documento, saida);
            pdf.setStrictImageSequence(true);

            documento.open();
            documento.add(new Jpeg(arquivo));
            documento.close();

            saida.flush();

            return saida.toByteArray();

        } catch (IOException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());
        } catch (DocumentException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());        	
        }
    }

    /**
     * Converte arquivo TXT em array de bytes de um documento PDF.
     * @param arquivo Array de bytes de arquivo TXT,
     * @return Array de bytes de arquivo PDF ou <code>null</code>.
     */
    public static byte[] txtParaPdf(String nomeArquivoTemp,byte[] arquivo) throws NegocioException {
        Document pdf = null;
        BufferedReader arquivoOrigem = null;
        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();

            pdf = new Document(PageSize.A4);
            File fileTXT = File.createTempFile(nomeArquivoTemp+"COT", ".txt");

            org.apache.commons.io.FileUtils.writeByteArrayToFile(fileTXT, arquivo);

            PdfWriter.getInstance(pdf, saida);

            pdf.open();

            arquivoOrigem = new BufferedReader(new FileReader(fileTXT));
            String linha;

            while ((linha = arquivoOrigem.readLine()) != null) {
                Paragraph paragrafo = new Paragraph(linha);
                pdf.add(paragrafo);
            }

            pdf.close();

            FileUtils.deleteQuietly(fileTXT);

            saida.flush();
            return saida.toByteArray();
        } catch (IOException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());
        } catch (DocumentException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());        	
        } finally {
            try {
                if (arquivoOrigem != null) {
                    arquivoOrigem.close();
                }
                if (pdf != null) {
                    pdf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Converte arquivos .doc/.docx em array de bytes de um documento PDF.
     * @param arquivo Array de bytes de arquivo .doc/.docx
     * @param extensao extansao do arquivo word: doc ou docx
     * @return Array de bytes de arquivo PDF ou <code>null</code>.
     */
    public static byte[] wordParaPdf(String nomeArquivoTemp, byte[] arquivo, String extensao) throws NegocioException {

        OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();

        try {

            // doc/docx temprario
            File arquivoWord = File.createTempFile(nomeArquivoTemp+"CON", ".".concat(extensao));

            org.apache.commons.io.FileUtils.writeByteArrayToFile(arquivoWord, arquivo);

            // pdf temporario
            File arquivoPDF = File.createTempFile(nomeArquivoTemp+"CON-01", ".pdf");

            officeManager.start();
            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
            converter.convert(arquivoWord, arquivoPDF);

            byte[] byteArrayOutputStream = org.apache.commons.io.FileUtils.readFileToByteArray(arquivoPDF);

            FileUtils.deleteQuietly(arquivoPDF);
            FileUtils.deleteQuietly(arquivoWord);

            return byteArrayOutputStream;
        } catch (FileNotFoundException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());
        } catch (IOException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());
        } catch (OfficeException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());
        } finally {
            officeManager.stop();
        }
    }

    /**
     * Converte arquivo HTML em array de bytes de um documento PDF.
     * @param arquivo Array de bytes de arquivo HTML,
     * @return Array de bytes de arquivo PDF ou <code>null</code>.
     * @throws DocumentException - Caso ocorra erro na geracao do documento.
     * @throws IOException - Caso ocorra erro de IO.
     * @throws ParserConfigurationException - Erro criar validador do HTML
     * @throws SAXException - Erro na validação do arquivo HTML
     */
    public static byte[] htmlParaPdf(byte[] arquivo) throws NegocioException {

        try {

            org.w3c.dom.Document document = XMLResource.load(new ByteArrayInputStream(arquivo)).getDocument();

            ITextRenderer pdfRenderer = new ITextRenderer();
            SharedContext sharedContext = pdfRenderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);
            sharedContext.setReplacedElementFactory(new ConverterPDFUtil.B64ImgReplacedElementFactory());
            sharedContext.getTextRenderer().setSmoothingThreshold(0);
            pdfRenderer.setDocument(document, null);

            pdfRenderer.layout();

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            pdfRenderer.createPDF(byteArray);

            return byteArray.toByteArray();

        } catch (DocumentException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());
        } catch (XRRuntimeException e) {
        	throw new NegocioException(e.getClass().getName()+" "+e.getMessage());
        }
    }

    /**
     * Classe para auxiliar a corversão de imagens dos arquivo HTML para PDF.
     * @author SUPDE/DEFLA/DE305
     */
    private static class B64ImgReplacedElementFactory implements ReplacedElementFactory {

        @Override
        public ReplacedElement createReplacedElement(LayoutContext layoutContext, BlockBox box,
            UserAgentCallback userAgentCallback, int cssWidthImage, int cssHeightImage) {

            try {
                Element element = box.getElement();
                if (element == null) {
                    return null;
                }

                String nodeName = element.getNodeName();
                if (nodeName.equals("img")) {

                    String attribute = element.getAttribute("src");
                    FSImage fsImage;

                    fsImage = buildImage(attribute, userAgentCallback);

                    if (fsImage != null) {
                        if (cssWidthImage != -1 || cssHeightImage != -1) {
                            fsImage.scale(cssWidthImage, cssHeightImage);
                        }
                        return new ITextImageElement(fsImage);
                    }
                }

                return null;
            } catch (BadElementException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        /**
         * Converte a imagem(em base64) do HTML para o PDF.
         * @param srcAttribute Valor do atributo src da tag html "img"
         * @param userAgentCallback UserAgentCallback
         * @return Valor em Base64 da imagem
         * @throws IOException caso ocorra erro ao obter valor da imagem
         * @throws BadElementException caso ocorra erro ao obter valor da imagem
         */
        protected FSImage buildImage(String srcAttribute, UserAgentCallback userAgentCallback)
            throws IOException, BadElementException {
            FSImage fsImage;
            if (srcAttribute.startsWith("data:image/")) {
                String b64encoded = srcAttribute.substring(srcAttribute.indexOf("base64,") + "base64,".length(),
                    srcAttribute.length());
                byte[] decodedBytes = java.util.Base64.getDecoder().decode(b64encoded);
                fsImage = new ITextFSImage(Image.getInstance(decodedBytes));
            } else {
                fsImage = userAgentCallback.getImageResource(srcAttribute).getImage();
            }
            return fsImage;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setFormSubmissionListener(FormSubmissionListener listener) {
        }

        @Override
        public void remove(Element arg0) {
        }
    }

     public static void main(String[] args) throws IOException {
    	 
    	 try {
    		 
    		 byte[] bytes = jpegParaPdf(Files.readAllBytes(new File("/home/87652404304/Desktop/tela.jpeg").toPath()));
    		 File destino = new File("/home/87652404304/Desktop/temporPdf1.pdf");
    		 
    		 System.out.println(destino.getAbsolutePath());
    		 
    		 Files.write(destino.toPath(), bytes);
    		 
    		 byte[] bytes1 = tiffParaPdf(Files.readAllBytes(new File("/home/87652404304/Desktop/tela.tiff").toPath()));
    		 File destino1 = new File("/home/87652404304/Desktop/temporPdf2.pdf");
    		 
    		 System.out.println(destino1.getAbsolutePath());
    		 
    		 Files.write(destino1.toPath(), bytes1);
    		 
    		 byte[] bytes2 = txtParaPdf("/home/87652404304/Desktop/cata",Files.readAllBytes(new File("/home/87652404304/Desktop/words-english.txt").toPath()));
    		 File destino2 = new File("/home/87652404304/Desktop/temporPdf3.pdf");
    		 
    		 System.out.println(destino2.getAbsolutePath());
    		 
    		 Files.write(destino2.toPath(), bytes2);
    		 
    		 byte[] bytes3 = wordParaPdf("/home/87652404304/Desktop/cata2",Files.readAllBytes(new File("/home/87652404304/Desktop/documento.doc").toPath()),"doc");
    		 File destino3 = new File("/home/87652404304/Desktop/temporPdf4.pdf");
    		 
    		 System.out.println(destino3.getAbsolutePath());
    		 
    		 Files.write(destino3.toPath(), bytes3);
    		 
    		 byte[] bytes4 = htmlParaPdf(Files.readAllBytes(new File("/home/87652404304/Desktop/documento2.html").toPath()));
    		 File destino4 = new File("/home/87652404304/Desktop/temporPdf5.pdf");
    		 
    		 System.out.println(destino4.getAbsolutePath());
    		 
    		 Files.write(destino4.toPath(), bytes4);
    		 
    		 
    		 byte[] bytes5 = htmlParaPdf(Files.readAllBytes(new File("/home/87652404304/Desktop/doc-completo.html").toPath()));
    		 File destino5 = new File("/home/87652404304/Desktop/temporPdf6.pdf");
    		 
    		 System.out.println(destino5.getAbsolutePath());
    		 
    		 Files.write(destino5.toPath(), bytes5);
    		 
    		 
    		 
    		 
			
		} catch (NegocioException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
    	 
    
//     File fos = new File(
//     "/home/83973869391/dev/saj/workspaces/saj-integra-des/saj-integra-util/src/test/resources/word/testeDOC.pdf");
//     File fis = new File(
//     "/home/83973869391/dev/saj/workspaces/saj-integra-des/saj-integra-util/src/test/resources/word/pecaDoc.doc");
//    
//     byte[] arquivo = org.apache.commons.io.FileUtils.readFileToByteArray(fis);
//     org.apache.commons.io.FileUtils.writeByteArrayToFile(fos, wordParaPDF(arquivo, "DOC"));
//    
//     fos = new File(
//     "/home/83973869391/dev/saj/workspaces/saj-integra-des/saj-integra-util/src/test/resources/word/testeDOCX.pdf");
//     fis = new File(
//     "/home/83973869391/dev/saj/workspaces/saj-integra-des/saj-integra-util/src/test/resources/word/pecaDoc.docx");
//    
//     arquivo = org.apache.commons.io.FileUtils.readFileToByteArray(fis);
//     org.apache.commons.io.FileUtils.writeByteArrayToFile(fos, wordParaPDF(arquivo, "DOCX"));
     }
}
