package br.gov.serpro.saj.ged.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class NegocioException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NegocioException() {
		super();
	}

	public NegocioException(Throwable cause) {
		super(cause);
	}

	public NegocioException(String message) {
		super(message);
	}

}
