package br.gov.serpro.saj.ged.security;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import br.gov.serpro.saj.ged.business.UsuarioRest;

@WebFilter(urlPatterns = "/*")
public class AuthorizationFilter implements Filter {

    private static final String AUTHENTICATION_SCHEME = "Basic";
    
    @Inject
    private UsuarioRest usuarioRest;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    		throws IOException, ServletException {
    	
    	HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authorizationHeader =
        		httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (isAuthorizationHeaderPresent(authorizationHeader)) {	
	        String credentials = authorizationHeader
	                            .substring(AUTHENTICATION_SCHEME.length()).trim();
	
	        if (validateCredentials(credentials)) {
	            chain.doFilter(httpRequest, response);
	        	return;
	        }
        }
    	HttpServletResponse httpResponse = (HttpServletResponse) response;
    	httpResponse.sendError(Response.Status.FORBIDDEN.getStatusCode());        
    }

    private boolean isAuthorizationHeaderPresent(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private boolean validateCredentials(String credentials) {
    	return usuarioRest.validateUser(credentials);
    	
    }
}