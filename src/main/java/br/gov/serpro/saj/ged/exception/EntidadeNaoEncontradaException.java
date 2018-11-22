package br.gov.serpro.saj.ged.exception;

public class EntidadeNaoEncontradaException extends NegocioException	 {


	private static final long serialVersionUID = 1L;

	public EntidadeNaoEncontradaException() {
		super("Entidade não encontrada.");
	}
	
	public EntidadeNaoEncontradaException(String message) {
		super(message);
	}

}
