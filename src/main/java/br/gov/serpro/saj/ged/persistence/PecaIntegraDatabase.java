package br.gov.serpro.saj.ged.persistence;
import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import br.gov.serpro.saj.ged.model.PecaIntegra;

/**
 * Classe de persistência de entidades. Repositório genérico que pretende
 * desacoplar a tecnologia de persistência das demais camadas, além de prover
 * métodos utilitários.
 */
@ApplicationScoped
public class PecaIntegraDatabase extends DatabaseRepository{
	
	public static final String CAMPO_ID = "id";
	public static final String CAMPO_NUMERO_PROCESSO = "numeroProcesso";

	public List<Long> findIdByProcesso(Serializable value) {
		return super.findFieldByField(PecaIntegra.class, CAMPO_ID ,CAMPO_NUMERO_PROCESSO, value);
	}
	
}
