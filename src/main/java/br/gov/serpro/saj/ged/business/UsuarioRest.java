package br.gov.serpro.saj.ged.business;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.gov.serpro.saj.ged.util.AmbienteUtil;

@ApplicationScoped
public class UsuarioRest {

    @Inject
    private AmbienteUtil ambienteUtil;

    public boolean validateUser(String token) {

        if (token != null) {
            String chunks[] = token.split(":");
            if (chunks != null && chunks.length == 2) {
                return ambienteUtil.getUsuarioIntegracaoWSGed().equals(chunks[0]) && ambienteUtil.getSenhaIntegracaoWSGed().equals(chunks[1]);
            }
        }

        return false;
    }

}
