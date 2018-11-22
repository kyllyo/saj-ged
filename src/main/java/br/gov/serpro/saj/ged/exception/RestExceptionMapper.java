package br.gov.serpro.saj.ged.exception;

import javax.ejb.EJBException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {

	public Response toResponse(Throwable e) {

		if (e instanceof EntidadeNaoEncontradaException) {
			return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
		}
		if (e instanceof ArquivoNaoEncontradoException) {
			return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();

	}

}
