package br.gov.serpro.saj.ged.rest;
import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class IncludeForm {

    @FormParam("processo")
    @PartType(MediaType.TEXT_PLAIN)
	private String processo;
	
    @FormParam("tipo_arquivo")
    @PartType(MediaType.TEXT_PLAIN)
	private Long tipoArquivo;
    
    @FormParam("tipo_documento")
    @PartType(MediaType.TEXT_PLAIN)
	private Long tipoDocumento;
    
    @FormParam("id_anexo")
    @PartType(MediaType.TEXT_PLAIN)
	private Long idAnexo;
    
    @FormParam("id_objeto_tramitacao")
    @PartType(MediaType.TEXT_PLAIN)
	private Long idObjetoTramitacao;    

    @FormParam("id_manifestacao")
    @PartType(MediaType.TEXT_PLAIN)
	private Long idManifestacao;    
    
    @FormParam("descricao")
    @PartType(MediaType.TEXT_PLAIN)
	private String descricao;
    
    @FormParam("dados")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputStream dados;

	public Long getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Long tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public Long getIdObjetoTramitacao() {
		return idObjetoTramitacao;
	}

	public void setIdObjetoTramitacao(Long idObjetoTramitacao) {
		this.idObjetoTramitacao = idObjetoTramitacao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String name) {
		this.processo = name;
	}

	public Long getIdAnexo() {
		return idAnexo;
	}

	public void setIdAnexo(Long idAnexo) {
		this.idAnexo = idAnexo;
	}

	public Long getIdManifestacao() {
		return idManifestacao;
	}

	public void setIdManifestacao(Long idManifestacao) {
		this.idManifestacao = idManifestacao;
	}

	public Long getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(Long code) {
		this.tipoArquivo = code;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricaoTipo(String descricao) {
		this.descricao = descricao;
	}

	public InputStream getDados() {
		return dados;
	}

	public void setDados(InputStream data) {
		this.dados = data;
	}
    
    
}
