package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.UtilizadorInfra;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Repository para operações com UtilizadorInfra usando Criteria API (ORM)
 */
public class UtilizadorInfraRepository extends BaseRepository<UtilizadorInfra, Long> {

    public UtilizadorInfraRepository() {
        super(UtilizadorInfra.class);
    }

    /**
     * Encontra um utilizador por email e infraestrutura usando Criteria API
     */
    public UtilizadorInfra findByEmailAndInfraId(String email, Long infraId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<UtilizadorInfra> cq = cb.createQuery(UtilizadorInfra.class);
            Root<UtilizadorInfra> root = cq.from(UtilizadorInfra.class);
            
            Predicate emailPredicate = cb.equal(root.get("email"), email);
            Predicate infraPredicate = cb.equal(root.get("infraestrutura").get("id"), infraId);
            
            cq.where(cb.and(emailPredicate, infraPredicate));
            
            List<UtilizadorInfra> results = em.createQuery(cq).getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Encontra todos os utilizadores de uma infraestrutura usando Criteria API
     */
    public List<UtilizadorInfra> findByInfraId(Long infraId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<UtilizadorInfra> cq = cb.createQuery(UtilizadorInfra.class);
            Root<UtilizadorInfra> root = cq.from(UtilizadorInfra.class);
            
            cq.where(cb.equal(root.get("infraestrutura").get("id"), infraId));
            cq.orderBy(cb.asc(root.get("email")));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Encontra todos os utilizadores por email usando Criteria API
     */
    public List<UtilizadorInfra> findByEmail(String email) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<UtilizadorInfra> cq = cb.createQuery(UtilizadorInfra.class);
            Root<UtilizadorInfra> root = cq.from(UtilizadorInfra.class);
            
            cq.where(cb.equal(root.get("email"), email));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Conta utilizadores por infraestrutura usando Criteria API
     */
    public long countByInfraId(Long infraId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<UtilizadorInfra> root = cq.from(UtilizadorInfra.class);
            
            cq.select(cb.count(root));
            cq.where(cb.equal(root.get("infraestrutura").get("id"), infraId));
            
            return em.createQuery(cq).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Retorna utilizadores com inatividade acima de X ms usando Criteria API
     */
    public List<UtilizadorInfra> findInactiveUsers(Long inactivityThresholdMs) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<UtilizadorInfra> cq = cb.createQuery(UtilizadorInfra.class);
            Root<UtilizadorInfra> root = cq.from(UtilizadorInfra.class);
            
            Long thresholdTime = System.currentTimeMillis() - inactivityThresholdMs;
            cq.where(cb.lessThan(root.get("ultimaAtividade"), thresholdTime));
            cq.orderBy(cb.asc(root.get("email")));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }
}
