package pt.uan.anuncioSloc.persistence.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Factory para criar EntityManager instances
 */
public class EntityManagerFactory_Factory {

    private static EntityManagerFactory emf;

    private EntityManagerFactory_Factory() {
    }

    /**
     * Obtém a factory singleton
     */
    public static EntityManagerFactory getFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("AnunciosLocPU");
        }
        return emf;
    }

    /**
     * Cria um novo EntityManager
     */
    public static EntityManager createEntityManager() {
        return getFactory().createEntityManager();
    }

    /**
     * Fecha a factory
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
