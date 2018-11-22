package br.gov.serpro.saj.ged.exception;

public class ArquivoNaoEncontradoException extends NegocioException	 {


	private static final long serialVersionUID = 1L;

	public ArquivoNaoEncontradoException() {
		super("Arquivo não encontrado.");
	}
	
	public ArquivoNaoEncontradoException(String message) {
		super(message);
	}

}
