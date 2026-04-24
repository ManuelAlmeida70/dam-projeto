package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Restricao;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Repository para operações com Restricao usando Criteria API (ORM)
 */
public class RestricaoRepository extends BaseRepository<Restricao, Long> {

    public RestricaoRepository() {
        super(Restricao.class);
    }

    /**
     * Encontra todas as restrições de uma infraestrutura usando Criteria API
     */
    public List<Restricao> findByInfraId(Long infraId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Restricao> cq = cb.createQuery(Restricao.class);
            Root<Restricao> root = cq.from(Restricao.class);
            
            cq.where(cb.equal(root.get("infraestrutura").get("id"), infraId));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Encontra restrições por tipo usando Criteria API
     */
    public List<Restricao> findByInfraIdAndTipo(Long infraId, String tipo) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Restricao> cq = cb.createQuery(Restricao.class);
            Root<Restricao> root = cq.from(Restricao.class);
            
            Predicate infraPredicate = cb.equal(root.get("infraestrutura").get("id"), infraId);
            Predicate tipoPredicate = cb.equal(root.get("tipo").as(String.class), tipo);
            
            cq.where(cb.and(infraPredicate, tipoPredicate));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Conta restrições por infraestrutura usando Criteria API
     */
    public long countByInfraId(Long infraId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Restricao> root = cq.from(Restricao.class);
            
            cq.select(cb.count(root));
            cq.where(cb.equal(root.get("infraestrutura").get("id"), infraId));
            
            return em.createQuery(cq).getSingleResult();
        } finally {
            em.close();
        }
    }
}
