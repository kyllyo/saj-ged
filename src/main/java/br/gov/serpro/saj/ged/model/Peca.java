package br.gov.serpro.saj.ged.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "peca")
public class Peca implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id	
	@Column(name = "id_documento", updatable = false, nullable = false)
	private Long id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_documento", updatable = false, nullable = false, insertable = false)
	private Documento documento;

	@Column(name = "id_processo_peca")
	private Long idProcessoPeca;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public Long getIdProcessoPeca() {
		return idProcessoPeca;
	}

	public void setIdProcessoPeca(Long idProcessoPeca) {
		this.idProcessoPeca = idProcessoPeca;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Peca)) {
			return false;
		}
		Peca other = (Peca) obj;
		if (id != null) {
			if (!id.equals(other.getId())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 101;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}