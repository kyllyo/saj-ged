package br.gov.serpro.saj.ged.business;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.serpro.saj.ged.exception.ArquivoNaoEncontradoException;
import br.gov.serpro.saj.ged.exception.EntidadeNaoEncontradaException;
import br.gov.serpro.saj.ged.exception.NegocioException;
import br.gov.serpro.saj.ged.model.PecaIntegra;
import br.gov.serpro.saj.ged.persistence.FileRepository;
import br.gov.serpro.saj.ged.persistence.PecaIntegraDatabase;

@Stateless
public class PecaIntegraBC {

	@Inject
	private PecaIntegraDatabase databaseRepository;

	@Inject
	private FileRepository fileRepository;
	
	/**
	 * Inclui uma PecaIntegra no banco e no sistema de arquivos. 
	 * O Numero do processo é obrigatorio.
	 * Retorna o identificador da peça.
	 * @param numeroProcesso String
	 * @param tipoArquivo Long
	 * @param arquivoStream InputStream
	 * @return Long
	 * @throws NegocioException
	 */
	public Long incluir(String numeroProcesso, Long tipoArquivo, InputStream arquivoStream) throws NegocioException {

		PecaIntegra pecaIntegra = new PecaIntegra();
		
		pecaIntegra.setCaminhoArquivo(fileRepository.gerarCaminhoArquivo());
		pecaIntegra.setNumeroProcesso(numeroProcesso);
		pecaIntegra.setTipoArquivo(tipoArquivo);		
		pecaIntegra.setDtCriacao(Calendar.getInstance().getTime());
		
		validaPeca(pecaIntegra);
		databaseRepository.insert(pecaIntegra);		
		
		//TODO criar runtimeexception
		try {
			fileRepository.gravar(arquivoStream, pecaIntegra.getCaminhoArquivo());
		}catch(IOException er) {
			er.printStackTrace();
		}
		return pecaIntegra.getId();
	}
	
	/**
	 * Exclui o arquivo e o registro no banco de uma PecaIntegra.
	 * @param id Long
	 * @return boolean
	 * @throws NegocioException
	 */
	public boolean excluir(Long id) throws NegocioException {
		PecaIntegra peca = buscarPecaExistente(id);
		try {			
			databaseRepository.remove(peca);
			fileRepository.excluir(peca.getCaminhoArquivo());
		}catch(Exception er) {
			throw new NegocioException("Operacao não realizada "+er.getMessage());
		}
		
		return true;
	}

	/**
	 * Consultar o arquivo de uma PecaIntegra a partir do id
	 * @param id Long
	 * @return File
	 * @throws NegocioException
	 */
	public File consultar(Long id) throws NegocioException {
		PecaIntegra peca = buscarPecaExistente(id);
		File file = new File(peca.getCaminhoArquivo());
		if (!file.canRead()) {
			//throw new NegocioException("Arquivo nao existe mo disco:" + peca.getCaminhoArquivo());
			throw new ArquivoNaoEncontradoException("Arquivo nao existe mo disco: " + peca.getCaminhoArquivo());
		}
		return file;
	}
	
	/**
	 * Consultar o arquivo de uma PecaIntegra a partir do id
	 * @param id String
	 * @return File
	 * @throws NegocioException
	 */
	public List<Long> consultarPecas(String numero) throws NegocioException {
		return databaseRepository.findIdByProcesso(numero);
	}
	
	
	/**
	 * True, se a peca possui os campos obrigatorios preenchidos.	 * 
	 * @param peca PecaIntegra
	 * @return boolean
	 * @throws NegocioException
	 */
	private boolean validaPeca(PecaIntegra peca) throws NegocioException {
		String error = "";
		if(peca.getNumeroProcesso() == null || peca.getNumeroProcesso().isEmpty()) {
			error += " | Número do processo não preenchido ";
		}
		if(peca.getCaminhoArquivo() == null || peca.getCaminhoArquivo().isEmpty()) {
			error += " | Caminho do arquivo não preenchido ";
		}
		
		if(error.isEmpty()) {
			return true;
		}		
		throw new NegocioException(error);

	}
	
	/**
	 * Retorna a PecaIntegra respectiva do id passado. 
	 * @param id Long
	 * @return PecaIntegra
	 * @throws NegocioException
	 */
	private PecaIntegra buscarPecaExistente(Long id) throws NegocioException {
		if(id == null || id.longValue() <= 0) {
			throw new NegocioException("Id inválido.");
		}
		PecaIntegra peca = databaseRepository.find(PecaIntegra.class, id);
		if(peca == null || peca.getId() == null) {
			//throw new NegocioException("Id inexistente na base.");
			throw new EntidadeNaoEncontradaException("Id inexistente na base.");
		}
		return peca;
	}


}
