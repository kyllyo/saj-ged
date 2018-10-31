package br.gov.serpro.saj.ged.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import br.gov.serpro.saj.ged.business.DocumentoSajBC;
import br.gov.serpro.saj.ged.business.ObjetoMerge;

@ApplicationScoped
@Path("/processo/documentos")
@Produces(MediaType.TEXT_PLAIN)
public class DocumentoSajRS {

	@Inject
	private DocumentoSajBC docSajBC;


	/**
	 * 
	 * @param form IncludeForm
	 * @return Response
	 */
	@POST
	@Path("/incluir")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response incluir(@MultipartForm IncludeForm form) {
		
		try {			
			Long id = docSajBC.incluirDocumento(form.getTipoArquivo(),
					form.getDescricao(), form.getDados());
			
			return Response.created(
						UriBuilder.fromResource(DocumentoSajRS.class)
							.path(String.valueOf(id))
							.build()
					)
					.entity(id)
					.build();
			
		} catch (Exception e) {
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getMessage())
					.build();
		}
	}
	
	/**
	 * 
	 * @param form IncludeForm
	 * @return Response
	 */
	@POST
	@Path("/anexo")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response incluirAnexoManifestacao(@MultipartForm IncludeForm form) {
		
		try {			
			Long id = docSajBC.incluirAnexoManifestacao(
					form.getIdAnexo(),
					form.getIdManifestacao(), form.getTipoArquivo(), 
					form.getTipoDocumento(), form.getDados());
			
			return Response.created(
						UriBuilder.fromResource(DocumentoSajRS.class)
							.path(String.valueOf(id))
							.build()
					)
					.entity(id)
					.build();
			
		} catch (Exception e) {
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getMessage())
					.build();
		}
	}
	
	
	/**
	 * 
	 * @param form IncludeForm
	 * @return Response
	 */
	@POST
	@Path("/recibo")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response incluirReciboManifestacao(@MultipartForm IncludeForm form) {
		
		try {			
			Long id = docSajBC.incluirReciboManifestacao(
					form.getIdManifestacao(), form.getTipoArquivo(), 
					form.getTipoDocumento(), form.getDados());
			
			return Response.created(
						UriBuilder.fromResource(DocumentoSajRS.class)
							.path(String.valueOf(id))
							.build()
					)
					.entity(id)
					.build();
			
		} catch (Exception e) {
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getMessage())
					.build();
		}
	}
	
	
	/**
	 * 
	 * @param form IncludeForm
	 * @return Response
	 */
	@POST
	@Path("/manifestacao")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response incluirManifestacao(@MultipartForm IncludeForm form) {
		
		try {			
			Long id = docSajBC.incluirManifestacao(
					form.getIdManifestacao(), form.getIdObjetoTramitacao(), 
					form.getTipoArquivo(), form.getTipoDocumento(), form.getDados());
			
			return Response.created(
						UriBuilder.fromResource(DocumentoSajRS.class)
							.path(String.valueOf(id))
							.build()
					)
					.entity(id)
					.build();
			
		} catch (Exception e) {
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getMessage())
					.build();
		}
	}

	
	@GET
	@Path("/{id:[0-9]+}")
	@Produces("application/octet-stream")
	public Response consultar(@PathParam("id") Long id) {
		try {
			
			File arquivo = docSajBC.consultar(id);
			
			return Response.ok(new GedStreamingOutput(arquivo)).build();
			
		} catch (Exception e) {
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getClass().getName()+": "+e.getMessage())
					.build();
		}
	}
	
	@GET
	@Path("/manifestacao-anexos/{id:[0-9]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obterManifestacaoAnexos(@PathParam("id") Long id) {
		ObjetoMerge objMerge = null;
		try {
			 objMerge = docSajBC.obterManifestacaoAnexos(id);		 
			
			return Response.ok(objMerge).build();
			
		} catch (Exception e) {
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getClass().getName()+": "+e.getMessage())
					.build();
		} finally {
			try {				
				Files.deleteIfExists(objMerge.getFile().toPath());				
			} catch (IOException e) {
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	@GET
	@Path("/consulta-manifestacao-anexos/{id:[0-9]+}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response consultarManifestacaoAnexos(@PathParam("id") Long id) {
		try {
			File arquivo = docSajBC.obterManifestacaoAnexosv2(id);
			
			return Response.ok(new GedStreamingOutput(arquivo,true)).build();
			
		} catch (Exception e) {
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getClass().getName()+": "+e.getMessage())
					.build();
		} 
	}
	
	@GET
	@Path("/obter-pecas-processo/{id:[0-9]+}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response obterPecasProcessoPorId(@PathParam("id") Long id) {
		try {
			File arquivo = docSajBC.obterPecasProcesso(id);
			
			return Response.ok(new GedStreamingOutput(arquivo,true)).build();
			
		} catch (Exception e) {
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getClass().getName()+": "+e.getMessage())
					.build();
		} 
	}
	
	
	
	@DELETE
	@Path("/{id:[0-9]+}")
	public Response excluir(@PathParam("id") Long id) {
		try {
			boolean excluido = docSajBC.excluir(id);
			
			return Response
					.ok(excluido)
					.build();
			
		} catch (Exception e) {
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getMessage())
					.build();
		}

	}
	
	


}

