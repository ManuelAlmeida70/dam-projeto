package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Anuncio;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Repository para operações com Anuncio usando Criteria API (ORM)
 */
public class AnuncioRepository extends BaseRepository<Anuncio, Long> {

    public AnuncioRepository() {
        super(Anuncio.class);
    }

    /**
     * Encontra todos os anúncios de um utilizador usando Criteria API
     */
    public List<Anuncio> findByEmail(String email) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Anuncio> cq = cb.createQuery(Anuncio.class);
            Root<Anuncio> root = cq.from(Anuncio.class);
            
            cq.where(cb.equal(root.get("email"), email));
            cq.orderBy(cb.desc(root.get("dataPublicacao")));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Encontra todos os anúncios de uma infraestrutura usando Criteria API
     */
    public List<Anuncio> findByInfraId(Long infraId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Anuncio> cq = cb.createQuery(Anuncio.class);
            Root<Anuncio> root = cq.from(Anuncio.class);
            
            cq.where(cb.equal(root.get("infraestrutura").get("id"), infraId));
            cq.orderBy(cb.desc(root.get("dataPublicacao")));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Encontra todos os anúncios de um local usando Criteria API
     */
    public List<Anuncio> findByLocalId(Long localId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Anuncio> cq = cb.createQuery(Anuncio.class);
            Root<Anuncio> root = cq.from(Anuncio.class);
            
            cq.where(cb.equal(root.get("local").get("id"), localId));
            cq.orderBy(cb.desc(root.get("dataPublicacao")));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Encontra anúncios ativos de uma infraestrutura usando Criteria API
     */
    public List<Anuncio> findAtivesByInfraId(Long infraId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Anuncio> cq = cb.createQuery(Anuncio.class);
            Root<Anuncio> root = cq.from(Anuncio.class);
            
            Predicate infraPredicate = cb.equal(root.get("infraestrutura").get("id"), infraId);
            Predicate activePredicate = cb.equal(root.get("ativo"), true);
            
            cq.where(cb.and(infraPredicate, activePredicate));
            cq.orderBy(cb.desc(root.get("dataPublicacao")));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Encontra anúncios ativos de um local usando Criteria API
     */
    public List<Anuncio> findAtivesByLocalId(Long localId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Anuncio> cq = cb.createQuery(Anuncio.class);
            Root<Anuncio> root = cq.from(Anuncio.class);
            
            Predicate localPredicate = cb.equal(root.get("local").get("id"), localId);
            Predicate activePredicate = cb.equal(root.get("ativo"), true);
            
            cq.where(cb.and(localPredicate, activePredicate));
            cq.orderBy(cb.desc(root.get("dataPublicacao")));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Encontra anúncios expirados usando Criteria API
     */
    public List<Anuncio> findExpired() {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Anuncio> cq = cb.createQuery(Anuncio.class);
            Root<Anuncio> root = cq.from(Anuncio.class);
            
            Long now = System.currentTimeMillis();
            Predicate expirationPredicate = cb.lessThan(root.get("dataExpiracao"), now);
            Predicate notNullPredicate = cb.isNotNull(root.get("dataExpiracao"));
            Predicate activePredicate = cb.equal(root.get("ativo"), true);
            
            cq.where(cb.and(expirationPredicate, notNullPredicate, activePredicate));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Conta anúncios por utilizador usando Criteria API
     */
    public long countByEmail(String email) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Anuncio> root = cq.from(Anuncio.class);
            
            cq.select(cb.count(root));
            cq.where(cb.equal(root.get("email"), email));
            
            return em.createQuery(cq).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Conta anúncios por infraestrutura usando Criteria API
     */
    public long countByInfraId(Long infraId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Anuncio> root = cq.from(Anuncio.class);
            
            cq.select(cb.count(root));
            cq.where(cb.equal(root.get("infraestrutura").get("id"), infraId));
            
            return em.createQuery(cq).getSingleResult();
        } finally {
            em.close();
        }
    }
}
