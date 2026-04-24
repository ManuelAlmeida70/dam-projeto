package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Restricao;
import java.util.Objects;
import java.util.List;

/**
 * Repository para operações com Restricao usando armazenamento em memória
 */
public class RestricaoRepository extends BaseRepository<Restricao, Long> {

    public RestricaoRepository() {
        super(Restricao.class);
    }

    /**
     * Encontra todas as restrições de uma infraestrutura
     */
    public List<Restricao> findByInfraId(Long infraId) {
        return filter(restricao -> restricao.getInfraestrutura() != null
                && Objects.equals(restricao.getInfraestrutura().getId(), infraId));
    }

    /**
     * Encontra restrições por tipo
     */
    public List<Restricao> findByInfraIdAndTipo(Long infraId, String tipo) {
        return filter(restricao -> restricao.getInfraestrutura() != null
                && Objects.equals(restricao.getInfraestrutura().getId(), infraId)
                && restricao.getTipo() != null
                && Objects.equals(restricao.getTipo().name(), tipo));
    }

    /**
     * Conta restrições por infraestrutura
     */
    public long countByInfraId(Long infraId) {
        return findByInfraId(infraId).size();
    }
}
