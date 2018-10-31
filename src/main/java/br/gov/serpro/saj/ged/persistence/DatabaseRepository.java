package br.gov.serpro.saj.ged.persistence;
import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Classe de persistência de entidades. Repositório genérico que pretende
 * desacoplar a tecnologia de persistência das demais camadas, além de prover
 * métodos utilitários.
 */
@ApplicationScoped
public class DatabaseRepository {

	/**
	 * Gerenciador de persistência.
	 */
	@PersistenceContext
	private EntityManager manager;

	public EntityManager getEntityManager() {
		return manager;
	}
	
	/**
	 * Insere a entidade.
	 * @param entity Entidade a ser inserida.
	 * @param <T> Tipo da entidade a ser inserida.
	 */
	public <T extends Serializable> void insert(T entity) {
		manager.persist(entity);
	}

	/**
	 * Atualiza a entidade.
	 * @param entity Entidade a ser atualizada.
	 * @param <T> Tipo da entidade a ser atualizada.
	 */
	public <T extends Serializable> void update(T entity) {
		manager.merge(entity);
	}

	/**
	 * Remove a entidade.
	 * @param entity Entidade a ser removida.
	 * @param <T> Tipo da entidade a ser removida.
	 */
	public <T extends Serializable> void remove(T entity) {
		manager.remove(entity);
	}

	/**
	 * Atualiza o estado da entidade com base no banco de dados.
	 * @param entity Entidade a ser atualizada.
	 * @param <T> Tipo da entidade a ser atualizada.
	 */
	public <T extends Serializable> void refresh(T entity) {
		manager.refresh(entity, LockModeType.NONE);
	}

	/**
	 * Recupera a entidade.
	 * @param <T> Tipo da entidade a ser recuperada.
	 * @param clazz Classe da entidade a ser recuperada
	 * @param id Id da entidade a ser recuperada.
	 * @return A entidade encontrada ou null caso contrario.
	 */
	public <T extends Serializable> T find(Class<T> clazz, Serializable id) {		
		return manager.find(clazz, id);
	}
	
	public <T extends Serializable> List<T> findEntityByField(Class<T> clazz, String field, Serializable value) {
		String queryStr = "select p from "+clazz.getName()+ " p where p."+field+" = :value";
		Query query = manager.createQuery(queryStr);
		query.setParameter("value", value);
		return query.getResultList();
	}
	
	public <T extends Serializable, E extends Serializable> List<T> 
		findFieldByField(Class<E> clazz, String fieldRetorno, String fieldConsulta, Serializable value) {
		
		String queryStr = "select p."+fieldRetorno+" from "+clazz.getName()+ " p where p."+fieldConsulta+" = :value ";
		Query query = manager.createQuery(queryStr);
		query.setParameter("value", value);
		return query.getResultList();
	}



}
