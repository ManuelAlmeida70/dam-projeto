package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Anuncio;
import pt.uan.anuncioSloc.persistence.entity.Coordenada;
import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import pt.uan.anuncioSloc.persistence.entity.Local;
import pt.uan.anuncioSloc.persistence.entity.Restricao;
import pt.uan.anuncioSloc.persistence.entity.UtilizadorInfra;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class InMemoryPersistenceStore {

    private static final Map<Class<?>, LinkedHashMap<Long, Object>> TABLES = new HashMap<>();
    private static final Map<Class<?>, AtomicLong> SEQUENCES = new HashMap<>();

    private InMemoryPersistenceStore() {
    }

    static synchronized <T> T save(Class<T> entityClass, T entity) {
        Long id = getId(entity);
        if (id == null) {
            id = nextId(entityClass);
            setId(entity, id);
        }

        table(entityClass).put(id, entity);
        cascadeSave(entity);
        return entity;
    }

    static synchronized <T> T update(Class<T> entityClass, T entity) {
        return save(entityClass, entity);
    }

    static synchronized <T, ID> void delete(Class<T> entityClass, ID id) {
        if (id == null) {
            return;
        }

        Long entityId = toLong(id);
        Object entity = table(entityClass).remove(entityId);
        if (entity != null) {
            cascadeDelete(entity);
        }
    }

    static synchronized <T> void deleteEntity(Class<T> entityClass, T entity) {
        if (entity == null) {
            return;
        }

        Long id = getId(entity);
        if (id != null) {
            delete(entityClass, id);
        }
    }

    @SuppressWarnings("unchecked")
    static synchronized <T, ID> T findById(Class<T> entityClass, ID id) {
        if (id == null) {
            return null;
        }

        return (T) table(entityClass).get(toLong(id));
    }

    @SuppressWarnings("unchecked")
    static synchronized <T> List<T> findAll(Class<T> entityClass) {
        return new ArrayList<>((List<T>) new ArrayList<>(table(entityClass).values()));
    }

    static synchronized long count(Class<?> entityClass) {
        return table(entityClass).size();
    }

    public static synchronized void clear() {
        TABLES.clear();
        SEQUENCES.clear();
    }

    private static LinkedHashMap<Long, Object> table(Class<?> entityClass) {
        return TABLES.computeIfAbsent(entityClass, key -> new LinkedHashMap<>());
    }

    private static long nextId(Class<?> entityClass) {
        return SEQUENCES.computeIfAbsent(entityClass, key -> new AtomicLong(0L)).incrementAndGet();
    }

    private static Long getId(Object entity) {
        try {
            Method method = entity.getClass().getMethod("getId");
            return (Long) method.invoke(entity);
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível obter o ID da entidade " + entity.getClass().getSimpleName(), e);
        }
    }

    private static void setId(Object entity, Long id) {
        try {
            Method method = entity.getClass().getMethod("setId", Long.class);
            method.invoke(entity, id);
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível definir o ID da entidade " + entity.getClass().getSimpleName(), e);
        }
    }

    private static Long toLong(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    private static void cascadeSave(Object entity) {
        if (entity instanceof Infraestrutura) {
            Infraestrutura infraestrutura = (Infraestrutura) entity;
            for (Local local : new ArrayList<>(infraestrutura.getLocais())) {
                save(Local.class, local);
            }
            for (Anuncio anuncio : new ArrayList<>(infraestrutura.getAnuncios())) {
                save(Anuncio.class, anuncio);
            }
            for (UtilizadorInfra utilizador : new ArrayList<>(infraestrutura.getUtilizadores())) {
                save(UtilizadorInfra.class, utilizador);
            }
            for (Restricao restricao : new ArrayList<>(infraestrutura.getRestricoes())) {
                save(Restricao.class, restricao);
            }
        } else if (entity instanceof Local) {
            Local local = (Local) entity;
            for (Coordenada coordenada : new ArrayList<>(local.getCoordenadas())) {
                save(Coordenada.class, coordenada);
            }
            Infraestrutura infraestrutura = local.getInfraestrutura();
            if (infraestrutura != null) {
                infraestrutura.getLocais().add(local);
            }
        } else if (entity instanceof Anuncio) {
            Anuncio anuncio = (Anuncio) entity;
            Infraestrutura infraestrutura = anuncio.getInfraestrutura();
            if (infraestrutura != null) {
                infraestrutura.getAnuncios().add(anuncio);
            }
        } else if (entity instanceof UtilizadorInfra) {
            UtilizadorInfra utilizador = (UtilizadorInfra) entity;
            Infraestrutura infraestrutura = utilizador.getInfraestrutura();
            if (infraestrutura != null) {
                infraestrutura.getUtilizadores().add(utilizador);
            }
        } else if (entity instanceof Restricao) {
            Restricao restricao = (Restricao) entity;
            Infraestrutura infraestrutura = restricao.getInfraestrutura();
            if (infraestrutura != null) {
                infraestrutura.getRestricoes().add(restricao);
            }
        } else if (entity instanceof Coordenada) {
            Coordenada coordenada = (Coordenada) entity;
            Local local = coordenada.getLocal();
            if (local != null) {
                local.getCoordenadas().add(coordenada);
            }
        }
    }

    private static void cascadeDelete(Object entity) {
        if (entity instanceof Infraestrutura) {
            Infraestrutura infraestrutura = (Infraestrutura) entity;
            for (Local local : new ArrayList<>(infraestrutura.getLocais())) {
                deleteEntity(Local.class, local);
            }
            for (Anuncio anuncio : new ArrayList<>(infraestrutura.getAnuncios())) {
                deleteEntity(Anuncio.class, anuncio);
            }
            for (UtilizadorInfra utilizador : new ArrayList<>(infraestrutura.getUtilizadores())) {
                deleteEntity(UtilizadorInfra.class, utilizador);
            }
            for (Restricao restricao : new ArrayList<>(infraestrutura.getRestricoes())) {
                deleteEntity(Restricao.class, restricao);
            }
        } else if (entity instanceof Local) {
            Local local = (Local) entity;
            for (Coordenada coordenada : new ArrayList<>(local.getCoordenadas())) {
                deleteEntity(Coordenada.class, coordenada);
            }
            Infraestrutura infraestrutura = local.getInfraestrutura();
            if (infraestrutura != null) {
                infraestrutura.getLocais().remove(local);
            }
        } else if (entity instanceof Anuncio) {
            Anuncio anuncio = (Anuncio) entity;
            Infraestrutura infraestrutura = anuncio.getInfraestrutura();
            if (infraestrutura != null) {
                infraestrutura.getAnuncios().remove(anuncio);
            }
        } else if (entity instanceof UtilizadorInfra) {
            UtilizadorInfra utilizador = (UtilizadorInfra) entity;
            Infraestrutura infraestrutura = utilizador.getInfraestrutura();
            if (infraestrutura != null) {
                infraestrutura.getUtilizadores().remove(utilizador);
            }
        } else if (entity instanceof Restricao) {
            Restricao restricao = (Restricao) entity;
            Infraestrutura infraestrutura = restricao.getInfraestrutura();
            if (infraestrutura != null) {
                infraestrutura.getRestricoes().remove(restricao);
            }
        } else if (entity instanceof Coordenada) {
            Coordenada coordenada = (Coordenada) entity;
            Local local = coordenada.getLocal();
            if (local != null) {
                local.getCoordenadas().remove(coordenada);
            }
        }
    }
}