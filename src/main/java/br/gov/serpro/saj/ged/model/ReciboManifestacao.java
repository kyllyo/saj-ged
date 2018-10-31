package br.gov.serpro.saj.ged.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "recibo_manifestacao")
public class ReciboManifestacao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id	
	@Column(name = "id_documento", updatable = false, nullable = false)
	private Long id;

	@Column(name = "id_manifestacao")
	private Long idManifestacao;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdManifestacao() {
		return idManifestacao;
	}

	public void setIdManifestacao(Long idManifestacao) {
		this.idManifestacao = idManifestacao;
	}
	
	public ReciboManifestacao(){}
	
	public ReciboManifestacao(Long id){
		this.id = id;		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ReciboManifestacao)) {
			return false;
		}
		ReciboManifestacao other = (ReciboManifestacao) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 81;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}