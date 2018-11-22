package br.gov.serpro.saj.ged.rest;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import br.gov.serpro.saj.ged.business.PecaIntegraBC;
import br.gov.serpro.saj.ged.business.RespostaPecaIntegra;

@ApplicationScoped
@Path("/integra/pecas")
public class PecaIntegraRS {

	@Inject
	private PecaIntegraBC pecaIntegraBC;
	
	private static final Logger logger = Logger.getLogger("br.gov.serpro.saj.ged.rest.PecaIntegraRS");
	
	/**
	 * 
	 * @param form IncludeForm
	 * @return Response
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response incluir(@MultipartForm IncludeForm form) {
	    logger.info("Disparo no método: incluir");
		try {			
			Long id = pecaIntegraBC.incluir(
					form.getProcesso(), form.getTipoArquivo(), form.getDados());
			
			return Response.created(
						UriBuilder.fromResource(PecaIntegraRS.class)
							.path(String.valueOf(id))
							.build()
					)
					.entity(id)
					.build();
		} catch (Exception e) {
		    logger.severe("Erro no método: incluir " + e.getMessage());
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getMessage())
					.build();
		}
	}
	
	@GET
	@Path("/{id:[0-9]+}")
	@Produces("application/octet-stream")
	public Response consultar(@PathParam("id") Long id) throws Exception {
	    logger.info("Disparo no método: consultar");
		try {
			
			File arquivo = pecaIntegraBC.consultar(id);
			return Response.ok(new GedStreamingOutput(arquivo))
					.build();
			
		} catch (Exception e) {
		    logger.severe("Erro no método: consultar " + e.getMessage());
			return Response
					.status(Status.NOT_FOUND)
					.type(MediaType.TEXT_PLAIN)
					.entity(e.getMessage())
					.build();
		}

	}
	
	@DELETE
	@Path("/{id:[0-9]+}")
	public Response excluir(@PathParam("id") Long id) {
	    logger.info("Disparo no método: excluir");
		try {
			boolean excluido = pecaIntegraBC.excluir(id);
			
			return Response.ok(excluido).build();
			
		} catch (Exception e) {
		    logger.severe("Erro no método: excluir " + e.getMessage());
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getMessage())
					.build();
		}

	}
	
	/**
	 * Consultar os ids dos documentos salvos associados a um número de processo.
	 * @param numero String
	 * @return Response
	 */
	@GET
	@Path("/consulta-id-pecas/{numero:[0-9]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response consultarIdsPecas(@PathParam("numero") String numero) {
	    logger.info("Disparo no método: consultarIdsPecas");
		try {
			List<Long> pecas = pecaIntegraBC.consultarPecas(numero);

			return Response.ok(new RespostaPecaIntegra(pecas)).build();
			
		} catch (Exception e) {
		    logger.severe("Erro no método: consultarIdsPecas " + e.getMessage());
			return Response
					.status(Status.NOT_FOUND)
					.entity(e.getMessage())
					.build();
		}

	}
}

