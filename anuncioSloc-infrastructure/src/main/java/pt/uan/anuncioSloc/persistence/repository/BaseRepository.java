package pt.uan.anuncioSloc.persistence.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Classe base genérica para todos os repositories usando um armazenamento em memória.
 * Fornece operações CRUD e utilitários de consulta sobre coleções.
 */
public abstract class BaseRepository<T, ID> {

    protected final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        return InMemoryPersistenceStore.save(entityClass, entity);
    }

    public T update(T entity) {
        return InMemoryPersistenceStore.update(entityClass, entity);
    }

    public void delete(T entity) {
        InMemoryPersistenceStore.deleteEntity(entityClass, entity);
    }

    public void deleteById(ID id) {
        InMemoryPersistenceStore.delete(entityClass, id);
    }

    public T findById(ID id) {
        return InMemoryPersistenceStore.findById(entityClass, id);
    }

    public List<T> findAll() {
        return InMemoryPersistenceStore.findAll(entityClass);
    }

    public long count() {
        return InMemoryPersistenceStore.count(entityClass);
    }

    protected List<T> filter(Predicate<T> predicate) {
        return findAll().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    protected List<T> sort(List<T> values, Comparator<T> comparator) {
        return values.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    protected List<T> page(List<T> values, int firstResult, int maxResults) {
        if (firstResult < 0) {
            firstResult = 0;
        }
        if (maxResults <= 0 || firstResult >= values.size()) {
            return List.of();
        }
        int endIndex = Math.min(values.size(), firstResult + maxResults);
        return values.subList(firstResult, endIndex);
    }

    protected T firstOrNull(List<T> values) {
        return values.isEmpty() ? null : values.get(0);
    }

    protected boolean sameId(Object left, Object right) {
        return Objects.equals(left, right);
    }
}
