package br.gov.serpro.saj.ged.business;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioRest {

//	private static final String USER = System.getProperty("user");
//	private static final String SENHA = System.getProperty("pass");
	
	private static final String USER = "user";
	private static final String SENHA = "senha";
	
	public static boolean validateUser(String token) {
		if(token != null) {
			String chunks[] = token.split(":");
			if(chunks != null && chunks.length == 2) {
		    	return USER.equals(chunks[0]) && SENHA.equals(chunks[1]);
			}
		}
		
		return false;
	}

}
