package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Local;
import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Repository para operações com Local usando armazenamento em memória
 */
public class LocalRepository extends BaseRepository<Local, Long> {

    public LocalRepository() {
        super(Local.class);
    }

    /**
     * Encontra um local pelo nome e infraestrutura
     */
    public Local findByNomeAndInfraestrutura(String nome, Infraestrutura infraestrutura) {
        return firstOrNull(filter(local -> Objects.equals(local.getNome(), nome)
                && local.getInfraestrutura() != null
                && infraestrutura != null
                && Objects.equals(local.getInfraestrutura().getId(), infraestrutura.getId())));
    }

    /**
     * Encontra todos os locais de uma infraestrutura
     */
    public List<Local> findByInfraestrutura(Infraestrutura infraestrutura) {
        return infraestrutura == null ? List.of() : findByInfraestruturaId(infraestrutura.getId());
    }

    /**
     * Encontra todos os locais de uma infraestrutura por ID
     */
    public List<Local> findByInfraestruturaId(Long infraId) {
        return sort(filter(local -> local.getInfraestrutura() != null
                        && Objects.equals(local.getInfraestrutura().getId(), infraId)),
                Comparator.comparing(Local::getNome));
    }

    /**
     * Conta locais por infraestrutura
     */
    public long countByInfraestrutura(Long infraId) {
        return findByInfraestruturaId(infraId).size();
    }
}
