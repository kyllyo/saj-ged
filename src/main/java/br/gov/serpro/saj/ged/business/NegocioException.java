package br.gov.serpro.saj.ged.business;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class NegocioException extends Exception {

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
