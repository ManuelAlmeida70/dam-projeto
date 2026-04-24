package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Repository para operações com Infraestrutura usando Criteria API (ORM)
 */
public class InfraestruturaRepository extends BaseRepository<Infraestrutura, Long> {

    public InfraestruturaRepository() {
        super(Infraestrutura.class);
    }

    /**
     * Encontra uma infraestrutura pelo nome usando Criteria API
     */
    public Infraestrutura findByNome(String nome) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Infraestrutura> cq = cb.createQuery(Infraestrutura.class);
            Root<Infraestrutura> root = cq.from(Infraestrutura.class);
            
            cq.where(cb.equal(root.get("nome"), nome));
            
            List<Infraestrutura> results = em.createQuery(cq).getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Encontra uma infraestrutura pela URL usando Criteria API
     */
    public Infraestrutura findByUrlServidor(String url) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Infraestrutura> cq = cb.createQuery(Infraestrutura.class);
            Root<Infraestrutura> root = cq.from(Infraestrutura.class);
            
            cq.where(cb.equal(root.get("urlServidor"), url));
            
            List<Infraestrutura> results = em.createQuery(cq).getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Lista todas as infraestruturas com paginação usando Criteria API
     */
    public List<Infraestrutura> findAllPaginated(int offset, int limit) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Infraestrutura> cq = cb.createQuery(Infraestrutura.class);
            Root<Infraestrutura> root = cq.from(Infraestrutura.class);
            
            cq.orderBy(cb.asc(root.get("id")));
            
            return em.createQuery(cq)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Retorna infraestruturas ativas usando Criteria API
     */
    public List<Infraestrutura> findAllAtivas() {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Infraestrutura> cq = cb.createQuery(Infraestrutura.class);
            Root<Infraestrutura> root = cq.from(Infraestrutura.class);
            
            cq.where(cb.greaterThan(root.get("capacidadeMaxima"), 0));
            cq.orderBy(cb.asc(root.get("nome")));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Encontra infraestruturas por capacidade mínima
     */
    public List<Infraestrutura> findByCapacidadeMinima(Integer capacidade) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Infraestrutura> cq = cb.createQuery(Infraestrutura.class);
            Root<Infraestrutura> root = cq.from(Infraestrutura.class);
            
            cq.where(cb.greaterThanOrEqualTo(root.get("capacidadeMaxima"), capacidade));
            cq.orderBy(cb.asc(root.get("nome")));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }
}
