package br.gov.serpro.saj.ged.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Classe responsável por recuperar todas as chaves referentes ao ambiente.
 */
@Singleton
@Named
public class AmbienteUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    private String usuarioIntegracaoWSGed;
    private String senhaIntegracaoWSGed;
    private String repositorioRaiz;
    private String ambiente;
    private Boolean producao;
    private Boolean homologacao;

    @PostConstruct
    private void postConstruct() {
        setProperties();
    }

    private void setProperties() {
        List<String> propriedadesAusentes = new ArrayList<String>();
        usuarioIntegracaoWSGed = getProperty("saj.processos.ged.username", propriedadesAusentes);
        senhaIntegracaoWSGed = getProperty("saj.processos.ged.password", propriedadesAusentes);
        repositorioRaiz = getProperty("saj.processos.ged.repositorioRaiz", propriedadesAusentes);
        ambiente = getProperty("saj.ambiente", propriedadesAusentes);
        if (!propriedadesAusentes.isEmpty()) {
            throw new RuntimeException("As seguintes propriedades de ambiente não foram definidas: " + propriedadesAusentes);
        }
    }

    private String getProperty(String key, Collection<String> propriedadesAusentes) {
        String value = System.getProperty(key);
        if (value == null) {
            propriedadesAusentes.add(key);
        }
        return value;
    }

    public String getUsuarioIntegracaoWSGed() {
        return usuarioIntegracaoWSGed;
    }

    public String getSenhaIntegracaoWSGed() {
        return senhaIntegracaoWSGed;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public boolean isProducao() {
        return producao;
    }

    public boolean isHomologacao() {
        return homologacao;
    }

    public String getRepositorioRaiz() {
        return repositorioRaiz;
    }
}