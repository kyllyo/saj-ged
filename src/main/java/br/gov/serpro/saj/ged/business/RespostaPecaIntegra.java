package br.gov.serpro.saj.ged.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RespostaPecaIntegra implements Serializable {

	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 624008075241190052L;

	private String observacao;
	
    private List<Long> listaResposta = new ArrayList<Long>();

    public RespostaPecaIntegra() {
    	
    }
    
    public RespostaPecaIntegra(List<Long> listaResposta) {
    	this.listaResposta = listaResposta;    	
    }
    

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public List<Long> getListaResposta() {
		return listaResposta;
	}

	public void setListaResposta(List<Long> listaResposta) {
		this.listaResposta = listaResposta;
	}

	

}
