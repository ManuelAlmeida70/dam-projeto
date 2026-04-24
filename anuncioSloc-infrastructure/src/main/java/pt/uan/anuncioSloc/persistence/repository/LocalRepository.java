package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Local;
import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Repository para operações com Local usando Criteria API (ORM)
 */
public class LocalRepository extends BaseRepository<Local, Long> {

    public LocalRepository() {
        super(Local.class);
    }

    /**
     * Encontra um local pelo nome e infraestrutura usando Criteria API
     */
    public Local findByNomeAndInfraestrutura(String nome, Infraestrutura infraestrutura) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Local> cq = cb.createQuery(Local.class);
            Root<Local> root = cq.from(Local.class);
            
            Predicate namePredicate = cb.equal(root.get("nome"), nome);
            Predicate infraPredicate = cb.equal(root.get("infraestrutura").get("id"), infraestrutura.getId());
            
            cq.where(cb.and(namePredicate, infraPredicate));
            
            List<Local> results = em.createQuery(cq).getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Encontra todos os locais de uma infraestrutura usando Criteria API
     */
    public List<Local> findByInfraestrutura(Infraestrutura infraestrutura) {
        return findByInfraestruturaId(infraestrutura.getId());
    }

    /**
     * Encontra todos os locais de uma infraestrutura por ID usando Criteria API
     */
    public List<Local> findByInfraestruturaId(Long infraId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Local> cq = cb.createQuery(Local.class);
            Root<Local> root = cq.from(Local.class);
            
            cq.where(cb.equal(root.get("infraestrutura").get("id"), infraId));
            cq.orderBy(cb.asc(root.get("nome")));
            
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Conta locais por infraestrutura usando Criteria API
     */
    public long countByInfraestrutura(Long infraId) {
        EntityManager em = EntityManagerFactory_Factory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Local> root = cq.from(Local.class);
            
            cq.select(cb.count(root));
            cq.where(cb.equal(root.get("infraestrutura").get("id"), infraId));
            
            return em.createQuery(cq).getSingleResult();
        } finally {
            em.close();
        }
    }
}
