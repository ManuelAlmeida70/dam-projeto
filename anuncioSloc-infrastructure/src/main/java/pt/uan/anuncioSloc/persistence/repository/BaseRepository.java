package pt.uan.anuncioSloc.persistence.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Classe base genérica para todos os repositories usando Criteria API
 * Fornece operações CRUD e queries type-safe
 */
public abstract class BaseRepository<T, ID> {

    protected Class<T> entityClass;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Persiste uma nova entidade
     */
    public T save(T entity) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Atualiza uma entidade existente
     */
    public T update(T entity) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T merged = em.merge(entity);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Remove uma entidade
     */
    public void delete(T entity) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T merged = em.merge(entity);
            em.remove(merged);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Remove uma entidade por ID
     */
    public void deleteById(ID id) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Encontra uma entidade por ID
     */
    public T findById(ID id) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    /**
     * Retorna todas as entidades
     */
    public List<T> findAll() {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            cq.from(entityClass);
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Retorna todas as entidades com ordenação
     */
    public List<T> findAll(Order order) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.orderBy(order);
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Conta o total de entidades
     */
    public long count() {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            cq.select(cb.count(cq.from(entityClass)));
            return em.createQuery(cq).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Executa uma query Criteria e retorna uma lista
     */
    protected <R> List<R> executeQuery(CriteriaQuery<R> cq) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Executa uma query Criteria e retorna um único resultado
     */
    protected <R> R executeSingleQuery(CriteriaQuery<R> cq) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            List<R> results = em.createQuery(cq).getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Executa uma query Criteria com paginação
     */
    protected <R> List<R> executeQueryPaginated(CriteriaQuery<R> cq, int firstResult, int maxResults) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            return em.createQuery(cq)
                    .setFirstResult(firstResult)
                    .setMaxResults(maxResults)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Cria um CriteriaBuilder para queries
     */
    protected CriteriaBuilder getCriteriaBuilder() {
        return EntityManagerFactory_Factory.createEntityManager().getCriteriaBuilder();
    }

    /**
     * Helper para criar um predicado de igualdade type-safe
     */
    protected <Y> Predicate equal(CriteriaBuilder cb, Expression<Y> path, Y value) {
        return cb.equal(path, value);
    }

    /**
     * Helper para criar um predicado LIKE
     */
    protected Predicate like(CriteriaBuilder cb, Expression<String> path, String value) {
        return cb.like(path, "%" + value + "%");
    }

    /**
     * Helper para criar um predicado IN
     */
    protected <Y> Predicate in(CriteriaBuilder cb, Expression<Y> path, List<Y> values) {
        return path.in(values);
    }

    /**
     * Helper para criar um Order ASC
     */
    protected Order asc(CriteriaBuilder cb, Expression<?> path) {
        return cb.asc(path);
    }

    /**
     * Helper para criar um Order DESC
     */
    protected Order desc(CriteriaBuilder cb, Expression<?> path) {
        return cb.desc(path);
    }
}
