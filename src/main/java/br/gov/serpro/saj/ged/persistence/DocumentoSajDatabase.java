package br.gov.serpro.saj.ged.persistence;
import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import br.gov.serpro.saj.ged.model.AnexoManifestacao;
import br.gov.serpro.saj.ged.model.Manifestacao;
import br.gov.serpro.saj.ged.model.Peca;
import br.gov.serpro.saj.ged.model.TipoDocumento;

/**
 * Classe de persistência de entidades. Repositório genérico que pretende
 * desacoplar a tecnologia de persistência das demais camadas, além de prover
 * métodos utilitários.
 */
@ApplicationScoped
public class DocumentoSajDatabase extends DatabaseRepository{
	
	public static final String CAMPO_ID = "id";
	public static final String CAMPO_DESCRICAO_TIPO_DOCUMENTO = "descricaoTipoDocumento";
	public static final String CAMPO_ID_MANIFESTACAO = "idManifestacao";
	public static final String CAMPO_ID_PROCESSO_PECA = "idProcessoPeca";
	
	public Long findTipoDocByDescricao(Serializable value) {
		if(value == null) {
			return null;
		}
		List<Long> lista = super.findFieldByField(TipoDocumento.class, CAMPO_ID , CAMPO_DESCRICAO_TIPO_DOCUMENTO, value);
		if(lista == null || lista.size() == 0) {
			return null;
		}
		return lista.get(0);
	}
	
	public List<Manifestacao> findManifestacoes(Long id){
		return super.findEntityByField(Manifestacao.class, CAMPO_ID_MANIFESTACAO, id);
	}

	public List<AnexoManifestacao> findAnexosPorManifestacao(Long id){
		return super.findEntityByField(AnexoManifestacao.class, CAMPO_ID_MANIFESTACAO, id);
	}

	public List<Peca> findByProcesso(Serializable value) {
		return super.findEntityByField(Peca.class, CAMPO_ID_PROCESSO_PECA, value);
	}
	

}
