package br.gov.serpro.saj.ged.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "anexo_manifestacao")
public class AnexoManifestacao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id	
	@Column(name = "id_documento", updatable = false, nullable = false)
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_documento", updatable = false, nullable = false, insertable = false)
	private Documento documento;

	@Column(name = "id_manifestacao")
	private Long idManifestacao;
	
	@Column(name = "legado_id_manifestacao")
	private Long legadoIdManifestacao;
	
	@Column(name = "id_anexo")
	private Long idAnexo;
	
	public AnexoManifestacao(){}
	
	public AnexoManifestacao(Long id){
		this.id = id;
	}
	
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

	public Long getLegadoIdManifestacao() {
		return legadoIdManifestacao;
	}

	public void setLegadoIdManifestacao(Long legadoIdManifestacao) {
		this.legadoIdManifestacao = legadoIdManifestacao;
	}

	public Long getIdManifestacao() {
		return idManifestacao;
	}

	public void setIdManifestacao(Long idManifestacao) {
		this.idManifestacao = idManifestacao;
	}

	public Long getIdAnexo() {
		return idAnexo;
	}

	public void setIdAnexo(Long idAnexo) {
		this.idAnexo = idAnexo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AnexoManifestacao)) {
			return false;
		}
		AnexoManifestacao other = (AnexoManifestacao) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 61;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}