package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Coordenada;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Repository para operações com Coordenada usando Criteria API (ORM)
 */
public class CoordenadaRepository extends BaseRepository<Coordenada, Long> {

    public CoordenadaRepository() {
        super(Coordenada.class);
    }

    /**
     * Encontra todas as coordenadas de um local usando Criteria API
     */
    public List<Coordenada> findByLocalId(Long localId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Coordenada> cq = cb.createQuery(Coordenada.class);
            Root<Coordenada> root = cq.from(Coordenada.class);
            
            cq.where(cb.equal(root.get("local").get("id"), localId));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Encontra coordenadas GPS de um local usando Criteria API
     */
    public List<Coordenada> findGPSByLocalId(Long localId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Coordenada> cq = cb.createQuery(Coordenada.class);
            Root<Coordenada> root = cq.from(Coordenada.class);
            
            Predicate localPredicate = cb.equal(root.get("local").get("id"), localId);
            Predicate typePredicate = cb.equal(root.get("tipo"), Coordenada.TipoCoordenada.GPS);
            
            cq.where(cb.and(localPredicate, typePredicate));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Encontra coordenadas WiFi de um local usando Criteria API
     */
    public List<Coordenada> findWiFiByLocalId(Long localId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Coordenada> cq = cb.createQuery(Coordenada.class);
            Root<Coordenada> root = cq.from(Coordenada.class);
            
            Predicate localPredicate = cb.equal(root.get("local").get("id"), localId);
            Predicate typePredicate = cb.equal(root.get("tipo"), Coordenada.TipoCoordenada.WIFI);
            
            cq.where(cb.and(localPredicate, typePredicate));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Conta coordenadas por local usando Criteria API
     */
    public long countByLocalId(Long localId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Coordenada> root = cq.from(Coordenada.class);
            
            cq.select(cb.count(root));
            cq.where(cb.equal(root.get("local").get("id"), localId));
            
            return em.createQuery(cq).getSingleResult();
        } finally {
            em.close();
        }
    }
}
